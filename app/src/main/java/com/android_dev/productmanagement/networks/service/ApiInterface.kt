package com.android_dev.productmanagement.networks.service

import com.android_dev.productmanagement.model.Product
import com.android_dev.productmanagement.model.ProductBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @GET("native_Db_V13.php")
    suspend fun getProducts(
        @Query("axn") axn: String ,
        @Query("divisionCode") divisionCode: Int
    ): Response<ArrayList<Product>>

    @POST("native_Db_V13.php")
    suspend fun saveProducts(
        @Query("axn") axn: String,
        @Query("divisionCode") divisionCode: Int,
        @Body productBody: ProductBody
    )

}