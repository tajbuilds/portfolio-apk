package com.taj.portfolio.data

import retrofit2.http.GET
import retrofit2.http.Path

interface PortfolioApi {
    @GET("api/mobile/home")
    suspend fun getHome(): HomeResponse

    @GET("api/mobile/work")
    suspend fun getWork(): WorkListResponse

    @GET("api/mobile/work/{slug}")
    suspend fun getWorkDetail(@Path("slug") slug: String): WorkDetailResponse

    @GET("api/mobile/about")
    suspend fun getAbout(): AboutResponse

    @GET("api/mobile/contact")
    suspend fun getContact(): ContactResponse
}
