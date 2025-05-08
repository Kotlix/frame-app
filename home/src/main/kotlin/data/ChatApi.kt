package data

import data.model.request.CreateChatRequest
import data.model.request.UpdateChatRequest
import data.model.response.ChatDto
import retrofit2.Response
import retrofit2.http.*

interface ChatApi {
    @GET("/api/v1/community/{communityId}/chat")
    suspend fun getAllChats(
        @Path("communityId") communityId: Long,
        @Header("Authorization") authToken: String
    ): Response<List<ChatDto>>

    @GET("/api/v1/chat/{id}")
    suspend fun getChatById(
        @Path("id") id: Long,
        @Header("Authorization") authToken: String
    ): Response<ChatDto>

    @POST("/api/v1/community/{communityId}/chat")
    suspend fun createChat(
        @Path("communityId") communityId: Long,
        @Body request: CreateChatRequest,
        @Header("Authorization") authToken: String
    ): Response<ChatDto>

    @PUT("/api/v1/chat/{id}")
    suspend fun updateChat(
        @Path("id") id: Long,
        @Body request: UpdateChatRequest,
        @Header("Authorization") authToken: String
    ): Response<ChatDto>

    @DELETE("/api/v1/chat/{id}")
    suspend fun deleteChat(
        @Path("id") id: Long,
        @Header("Authorization") authToken: String
    ) : Response<Void>
}