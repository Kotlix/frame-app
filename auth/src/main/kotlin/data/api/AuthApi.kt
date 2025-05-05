package data.api

import data.model.BasicLoginRequest
import data.model.BasicRegisterRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface AuthApi {
    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body request: BasicLoginRequest
    ): Response<String>

    @POST("/api/v1/auth/register")
    suspend fun register(
        @Body request: BasicRegisterRequest
    ): Response<Void>

    @GET("/api/v1/auth/register-verify/{secret}")
    suspend fun verifyRegister(
        @Path("secret") secret: String
    ): Response<Void>
}