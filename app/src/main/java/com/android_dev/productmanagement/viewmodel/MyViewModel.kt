package com.android_dev.productmanagement.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_dev.productmanagement.model.Product
import com.android_dev.productmanagement.model.ProductBody
import com.android_dev.productmanagement.repository.ProductRepository
import com.android_dev.productmanagement.utility.ApiResult
import kotlinx.coroutines.launch


class MyViewModel(private val repository: ProductRepository) : ViewModel() {

        private val _productList = MutableLiveData<ApiResult<ArrayList<Product>>>()
        val productList: LiveData<ApiResult<ArrayList<Product>>> get() = _productList

        fun loadProducts(axn: String , divisionCode: Int) {
            viewModelScope.launch {
                _productList.value = ApiResult.Loading
                val result = repository.fetchProducts(axn , divisionCode)
                _productList.value = result
            }
        }


    private val _saveProductResult = MutableLiveData<ApiResult<Unit>>()
    val saveProductResult: LiveData<ApiResult<Unit>> = _saveProductResult

    fun saveProductDetails(productBody: ProductBody, divisionCode: Int) {
        viewModelScope.launch {
            _saveProductResult.value = ApiResult.Loading
            try {
                repository.saveProducts("save/taskproddets", divisionCode, productBody)
                _saveProductResult.value = ApiResult.Success(Unit)
            } catch (e: Exception) {
                _saveProductResult.value = ApiResult.Error(e.message.toString())
            }
        }
    }



    }


