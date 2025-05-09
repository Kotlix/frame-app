package data

import data.model.request.SendMessageRequest
import data.model.response.MessageDto
import retrofit2.Response
import retrofit2.http.*

interface MessageApi {
    @POST("/api/v1/chat/{chatId}/send")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Long,
        @Body request: SendMessageRequest
    ): Response<MessageDto>

    @GET("/api/v1/chat/{chatId}/all")
    suspend fun getMessages(
        @Path("chatId") chatId: Long,
        @Query("page") page: Long,
        @Query("size") size: Long,
        @Header("Authorization") authToken: String
    ): Response<List<MessageDto>>


    @GET("/api/v1/chat-message/{id}")
    fun getById(
        @Header("Authorization") token: String,
        @Path("id") messageId: Long
    ): Response<MessageDto>
}