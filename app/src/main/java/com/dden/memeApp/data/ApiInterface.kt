package com.dden.memeApp.data

import com.dden.memeApp.models.AllMemeData
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("get_memes")
    suspend fun getThumbnailList(): Response<AllMemeData>
}