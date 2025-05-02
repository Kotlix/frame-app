package data.api

import data.model.BasicLoginRequest
import data.model.BasicRegisterRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path


interface AuthApi {
    @POST("/login")
    suspend fun login(
        @Body request: BasicLoginRequest
    ): Response<String>

    @POST("/register")
    suspend fun register(
        @Body request: BasicRegisterRequest
    ): Response<Void>
}