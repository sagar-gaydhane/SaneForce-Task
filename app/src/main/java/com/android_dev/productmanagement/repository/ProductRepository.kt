package com.android_dev.productmanagement.repository

import com.android_dev.productmanagement.model.Product
import com.android_dev.productmanagement.model.ProductBody
import com.android_dev.productmanagement.networks.client.RetrofitClient.apiService
import com.android_dev.productmanagement.networks.service.ApiInterface
import com.android_dev.productmanagement.utility.ApiResult


class ProductRepository(private val api: ApiInterface) {

    suspend fun fetchProducts(axn: String , divisionCode: Int): ApiResult<ArrayList<Product>> {
        return try {
            val response = api.getProducts(axn , divisionCode)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Exception: ${e.message}")
        }
    }


    suspend fun saveProducts(axn: String, divisionCode: Int, productBody: ProductBody) {
        apiService.saveProducts(axn, divisionCode, productBody)
    }


}
