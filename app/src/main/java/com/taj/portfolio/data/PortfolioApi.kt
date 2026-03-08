package com.taj.portfolio.data

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface PortfolioApi {
    @GET("api/mobile/home")
    suspend fun getHome(): HomeResponseDto

    @GET("api/mobile/work")
    suspend fun getWork(): WorkListResponseDto

    @GET("api/mobile/work/{slug}")
    suspend fun getWorkDetail(@Path("slug") slug: String): WorkDetailResponseDto

    @GET("api/mobile/about")
    suspend fun getAbout(): AboutResponseDto

    @GET("api/mobile/contact")
    suspend fun getContact(): ContactResponseDto

    @POST("api/mobile/contact/submit")
    suspend fun submitContact(@Body payload: ContactSubmitRequestDto): ContactSubmitResponseDto
}
