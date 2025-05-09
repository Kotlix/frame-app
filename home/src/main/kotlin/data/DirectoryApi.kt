package data

import data.model.request.CreateDirectoryRequest
import data.model.request.UpdateDirectoryRequest
import data.model.response.DirectoryDto
import retrofit2.Response
import retrofit2.http.*

interface DirectoryApi {
    @GET("/api/v1/community/{communityId}/directory")
    suspend fun getAllDirectories(
        @Path("communityId") communityId: Long,
        @Header("Authorization") authToken: String
    ): Response<List<DirectoryDto>>

    @GET("/api/v1/directory/{id}")
    suspend fun getDirectoryById(
        @Path("id") id: Long,
        @Header("Authorization") authToken: String
    ): Response<DirectoryDto>

    @POST("/api/v1/community/{communityId}/directory")
    suspend fun createDirectory(
        @Path("communityId") communityId: Long,
        @Body request: CreateDirectoryRequest,
        @Header("Authorization") authToken: String
    ): Response<DirectoryDto>

    @PUT("/api/v1/directory/{id}")
    suspend fun updateDirectory(
        @Path("id") id: Long,
        @Body request: UpdateDirectoryRequest,
        @Header("Authorization") authToken: String
    ): Response<DirectoryDto>

    @DELETE("/api/v1/directory/{id}")
    suspend fun deleteDirectory(
        @Path("id") id: Long,
        @Header("Authorization") authToken: String
    ): Response<Void>
}