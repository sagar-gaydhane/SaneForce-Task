package com.android_dev.productmanagement.model

import com.google.gson.annotations.SerializedName

data class Product(
        @SerializedName("product_code" ) var productCode : String? = null,
        @SerializedName("product_name" ) var productName : String? = null,
        @SerializedName("product_unit" ) var productUnit : String? = null,
        @SerializedName("convQty"      ) var convQty     : String? = null,
        var quantity: Int = 0, // Local property to manage quantity in the UI
        var rate: Int = 0 // Local property to manage quantity in the UI

)

data class ProductBody (
    @SerializedName("data" ) var data : ArrayList<Data> = arrayListOf()
)

data class Data (
    @SerializedName("product_code"   ) var productCode   : String? = null,
    @SerializedName("product_name"   ) var productName   : String? = null,
    @SerializedName("Product_Qty"    ) var productQty    : Int?    = null,
    @SerializedName("Rate"           ) var rate          : Int?    = null,
    @SerializedName("Product_Amount" ) var productAmount : Int?    = null

)

data class SavedProductResponse(
    @SerializedName("success" ) var success : Boolean? = null

)