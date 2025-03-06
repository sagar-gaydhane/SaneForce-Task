package com.android_dev.productmanagement.ui.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android_dev.productmanagement.databinding.ItemProductBinding
import com.android_dev.productmanagement.model.Data
import com.android_dev.productmanagement.model.Product



class ProductAdapter(
    private var productList: MutableList<Product>,
    private val onQuantityChange: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val initialProductState = mutableListOf<Data>()
    private val updatedProducts = mutableListOf<Data>()

    init {
        initialProductState.addAll(productList.map { it.toData() })
        updatedProducts.addAll(initialProductState)
    }

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val product = productList[position]

        holder.binding.tvProductName.text = product.productName
        holder.binding.tvProductCode.text = "Code: ${product.productCode ?: "N/A"}"
        holder.binding.tvProductUnit.text = "Unit: ${product.productUnit ?: "N/A"}"
        holder.binding.etQuantity.setText(product.quantity.toString())
        holder.binding.etRate.setText(product.rate.toString())
        holder.binding.tvTotal.text = "Total: ${(product.rate * product.quantity)}"

        holder.binding.etQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val rate = holder.binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
                val quantity = holder.binding.etQuantity.text.toString().toIntOrNull() ?: 0
                holder.binding.tvTotal.text = "Total: ${(rate * quantity)}"
                productList[position].quantity = quantity
                onQuantityChange(productList[position])
                updateProductState(productList[position])
            }
        })

        holder.binding.etRate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val rate = holder.binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
                val quantity = holder.binding.etQuantity.text.toString().toIntOrNull() ?: 0
                holder.binding.tvTotal.text = "Total: ${(rate * quantity)}"
                productList[position].rate = rate.toInt()
                onQuantityChange(productList[position])
                updateProductState(productList[position])
            }
        })

        holder.binding.btnIncrease.setOnClickListener {
            val currentQuantity = productList[position].quantity
            val newQuantity = currentQuantity + 1
            productList[position].quantity = newQuantity
            holder.binding.etQuantity.setText(newQuantity.toString())
            val rate = holder.binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
            holder.binding.tvTotal.text = "Total: ${(rate * newQuantity)}"
            onQuantityChange(productList[position])
            updateProductState(productList[position])
        }

        holder.binding.btnDecrease.setOnClickListener {
            val currentQuantity = productList[position].quantity
            if (currentQuantity > 0) {
                val newQuantity = currentQuantity - 1
                productList[position].quantity = newQuantity
                holder.binding.etQuantity.setText(newQuantity.toString())
                val rate = holder.binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
                holder.binding.tvTotal.text = "Total: ${(rate * newQuantity)}"
                onQuantityChange(productList[position])
                updateProductState(productList[position])
            }
        }

        holder.binding.btnDelete.setOnClickListener {
            val removedProduct = productList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, productList.size)
            onDeleteClick(removedProduct)
            removeProductFromState(removedProduct)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    // Update the product list with new data
    fun updateProducts(data: List<Product>) {
        productList.clear()
        productList.addAll(data)
        notifyDataSetChanged()

        initialProductState.clear()
        initialProductState.addAll(productList.map { it.toData() })

        updatedProducts.clear()
        updatedProducts.addAll(initialProductState)
    }

    // Update a product in the updatedProducts list
    private fun updateProductState(product: Product) {
        val updatedData = product.toData()
        val index = updatedProducts.indexOfFirst { it.productCode == updatedData.productCode }
        if (index != -1) {
            updatedProducts[index] = updatedData
        }
    }

    // Remove a product from the updatedProducts list
    private fun removeProductFromState(product: Product) {
        val data = product.toData()
        updatedProducts.removeAll { it.productCode == data.productCode }
    }


    fun getUpdatedProducts(): List<Data> {
        return updatedProducts
    }

    // Extension function to convert a Product to a Data object
    private fun Product.toData(): Data {
        return Data(
            productCode = this.productCode ?: "",
            productName = this.productName,
            rate = this.rate,
            productQty = this.quantity,
            productAmount = this.rate * this.quantity,

        )
    }
}




//
//class ProductAdapter(
//    private var productList: MutableList<Product>,
//    private val onQuantityChange: (Product) -> Unit,
//    private val onDeleteClick: (Product) -> Unit
//) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
//
//    val updatedProducts = mutableListOf<Data>()
//
//    inner class ProductViewHolder(val binding: ItemProductBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
//        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ProductViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ProductViewHolder, @SuppressLint("RecyclerView") position: Int) {
//        val product = productList[position]
//
//        holder.binding.tvProductName.text = product.productName
//        holder.binding.tvProductCode.text = "Code: ${product.productCode ?: "N/A"}"
//        holder.binding.tvProductUnit.text = "Unit: ${product.productUnit ?: "N/A"}"
//        holder.binding.etQuantity.setText(product.quantity.toString())
//        holder.binding.etRate.setText(product.rate.toString())
//        holder.binding.tvTotal.text = "Total: ${(product.rate * product.quantity)}"
//
//
//        holder.binding.etQuantity.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                val rate = holder.binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
//                val quantity = holder.binding.etQuantity.text.toString().toIntOrNull() ?: 0
//                holder.binding.tvTotal.text = "Total: ${(rate * quantity)}"
//                productList[position].quantity = quantity
//                onQuantityChange(productList[position])
//            }
//        })
//
//
//        holder.binding.etRate.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                val rate = holder.binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
//                val quantity = holder.binding.etQuantity.text.toString().toIntOrNull() ?: 0
//                holder.binding.tvTotal.text = "Total: ${(rate * quantity)}"
//                productList[position].rate = rate.toInt()
//                onQuantityChange(productList[position])
//            }
//        })
//
//        holder.binding.btnIncrease.setOnClickListener {
//            val currentQuantity = productList[position].quantity
//            val newQuantity = currentQuantity + 1
//            productList[position].quantity = newQuantity
//            holder.binding.etQuantity.setText(newQuantity.toString())
//            val rate = holder.binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
//            holder.binding.tvTotal.text = "Total: ${(rate * newQuantity)}"
//            onQuantityChange(productList[position])
//        }
//
//
//        holder.binding.btnDecrease.setOnClickListener {
//            val currentQuantity = productList[position].quantity
//            if (currentQuantity > 0) {
//                val newQuantity = currentQuantity - 1
//                productList[position].quantity = newQuantity
//                holder.binding.etQuantity.setText(newQuantity.toString())
//                val rate = holder.binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
//                holder.binding.tvTotal.text = "Total: ${(rate * newQuantity)}"
//                onQuantityChange(productList[position])
//            }
//        }
//
//        holder.binding.btnDelete.setOnClickListener {
//            val removedProduct = productList.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, productList.size)
//            onDeleteClick(removedProduct)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return productList.size
//    }
//
//    // Update the product list with new data
//    fun updateProducts(data: List<Product>) {
//        productList.clear()
//        productList.addAll(data)
//        notifyDataSetChanged()
//    }
//
//    // Delete a specific product from the list
//    fun deleteProduct(product: Product) {
//        val index = productList.indexOf(product)
//        if (index != -1) {
//            productList.removeAt(index)
//            notifyItemRemoved(index)
//        }
//    }
//
//    // Get the current product list
//    fun getProducts(): List<Data> {
//        return updatedProducts
//    }
//}
