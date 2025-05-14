package data

import data.model.response.FindPublicResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface HomeApi {
    @GET("/communities")
    suspend fun findPublicCommunities(
        @Header("Authorization") token: String,
        @Query("name") name: String?,
        @Query("pageOffset") pageOffset: Long,
        @Query("pageCount") pageCount: Long
    ): Response<FindPublicResponse>

    @GET("/my-communities")
    suspend fun findMyCommunities(
        @Header("Authorization") token: String
    ): Response<FindPublicResponse>

    @POST("/community-join")
    suspend fun joinCommunity(
        @Header("Authorization") token: String,
        @Query("communityId") communityId: Long
    ): Response<Void>

    @POST("/community-leave")
    suspend fun leaveCommunity(
        @Header("Authorization") token: String,
        @Query("communityId") communityId: Long
    ): Response<Void>

//    @POST("/register")
//    suspend fun register(
//        @Body request: BasicRegisterRequest
//    ): Response<Void>
}