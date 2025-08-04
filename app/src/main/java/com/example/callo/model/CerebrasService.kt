import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface CerebrasApi {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.0,
    val max_tokens: Int = -1,
    val stream: Boolean = false,
    val seed: Int = 0,
    val top_p: Double = 1.0
)

data class Message(
    val role: String,
    val content: String
)

data class ChatCompletionResponse(
    val id: String,
    val object1: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

class CerebrasClient(private val apiKey: String) {
    private val api: CerebrasApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.cerebras.ai/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(CerebrasApi::class.java)
    }

    suspend fun createChatCompletion(
        messages: List<Message>,
        model: String = "llama3.1-8b",
        temperature: Double = 0.0,
        maxTokens: Int = -1,
        stream: Boolean = false,
        seed: Int = 0,
        topP: Double = 1.0
    ): Result<ChatCompletionResponse> {
        return try {
            val request = ChatCompletionRequest(
                model = model,
                messages = messages,
                temperature = temperature,
                max_tokens = maxTokens,
                stream = stream,
                seed = seed,
                top_p = topP
            )

            val response = api.createChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class Conversation {
    private val messages = mutableListOf<Message>()

    fun addMessage(role: String, content: String) {
        messages.add(Message(role, content))
    }

    fun getMessages(): List<Message> = messages.toList()

    fun clear() {
        messages.clear()
    }

    fun removeSystemMessages() {
        messages.removeAll { it.role == "system" }
    }

    fun getSystemContext(): String? {
        return messages.find { it.role == "system" }?.content
    }
}

class CerebrasService(apiKey: String) {
    private val client = CerebrasClient(apiKey)
    private val conversations = mutableMapOf<String, Conversation>()

    fun createConversation(
        systemContext: String? = null
    ): String {
        val conversationId = generateConversationId()
        conversations[conversationId] = Conversation().apply {
            systemContext?.let { context ->
                addMessage("system", context)
            }
        }
        return conversationId
    }

    fun setSystemContext(
        conversationId: String,
        systemContext: String
    ) {
        val conversation = conversations[conversationId]
            ?: throw Exception("Conversation not found")

        conversation.removeSystemMessages()
        conversation.addMessage("system", systemContext)
    }

    suspend fun askQuestion(
        question: String,
        model: String = "llama3.1-8b",
        temperature: Double = 0.0
    ): String {
        val messages = listOf(
            Message(
                role = "user",
                content = question
            )
        )

        return try {
            val result = client.createChatCompletion(
                messages = messages,
                model = model,
                temperature = temperature
            )

            result.fold(
                onSuccess = { response ->
                    response.choices.firstOrNull()?.message?.content
                        ?: throw Exception("No response received")
                },
                onFailure = { error ->
                    throw Exception("Failed to get response: ${error.message}")
                }
            )
        } catch (e: Exception) {
            throw Exception("Error processing request: ${e.message}")
        }
    }

    suspend fun chat(
        conversationId: String,
        userMessage: String,
        model: String = "llama3.1-8b",
        temperature: Double = 0.0
    ): String {
        val conversation = conversations[conversationId]
            ?: throw Exception("Conversation not found")

        conversation.addMessage("user", userMessage)

        return try {
            val result = client.createChatCompletion(
                messages = conversation.getMessages(),
                model = model,
                temperature = temperature
            )

            result.fold(
                onSuccess = { response ->
                    val assistantMessage = response.choices.firstOrNull()?.message?.content
                        ?: throw Exception("No response received")
                    conversation.addMessage("assistant", assistantMessage)
                    assistantMessage
                },
                onFailure = { error ->
                    throw Exception("Failed to get response: ${error.message}")
                }
            )
        } catch (e: Exception) {
            throw Exception("Error processing request: ${e.message}")
        }
    }

    fun getConversationHistory(conversationId: String): List<Message> {
        return conversations[conversationId]?.getMessages()
            ?: throw Exception("Conversation not found")
    }

    fun clearConversation(conversationId: String) {
        conversations[conversationId]?.clear()
    }

    fun deleteConversation(conversationId: String) {
        conversations.remove(conversationId)
    }

    private fun generateConversationId(): String {
        return "conv-${System.currentTimeMillis()}-${(0..9999).random()}"
    }
}

suspend fun main() {
    val cerebrasService = CerebrasService("csk-nwj9ppct8kjwc43rtrwnnee6yxve46wmrydhv4jpn5yf2hec")

    val conversationId = cerebrasService.createConversation(
        systemContext = "You are a helpful AI assistant specializing in software development. " +
                "You have extensive knowledge of Kotlin and Android development."
    )

    val response = cerebrasService.chat(
        conversationId = conversationId,
        userMessage = "How do I implement a coroutine scope?"
    )
}