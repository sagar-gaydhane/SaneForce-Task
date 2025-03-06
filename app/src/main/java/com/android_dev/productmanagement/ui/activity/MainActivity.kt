package com.android_dev.productmanagement.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_dev.productmanagement.R
import com.android_dev.productmanagement.databinding.ActivityMainBinding
import com.android_dev.productmanagement.model.ProductBody
import com.android_dev.productmanagement.networks.client.RetrofitClient
import com.android_dev.productmanagement.repository.ProductRepository
import com.android_dev.productmanagement.ui.adapter.ProductAdapter
import com.android_dev.productmanagement.utility.ApiResult
import com.android_dev.productmanagement.utility.NetworkMonitor
import com.android_dev.productmanagement.utility.NetworkUtil
import com.android_dev.productmanagement.utility.dialog.CustomDialog
import com.android_dev.productmanagement.utility.dialog.LoadingDialog
import com.android_dev.productmanagement.viewmodel.MyViewModel
import com.android_dev.productmanagement.viewmodel.MyViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var viewModel: MyViewModel
    private val axn: String = "get/taskproducts"
    private val divisionCode: Int = 258
    private lateinit var adapter: ProductAdapter
    private lateinit var networkMonitor: NetworkMonitor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        loadingDialog = LoadingDialog(this)
        networkMonitor()
        val apiService = RetrofitClient.apiService
        val repository = ProductRepository(apiService)

        viewModel =
            ViewModelProvider(this, MyViewModelFactory(repository)).get(MyViewModel::class.java)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(
            mutableListOf(),

            onQuantityChange = { product ->
                Log.d(
                    "MainActivity",
                    "Quantity changed for product: ${product.productName} -> ${product.quantity}"
                )
            },
            onDeleteClick = { product ->
                Log.d("MainActivity", "Deleted product: ${product.productName}")
            }
        )
        binding.recyclerView.adapter = adapter


        binding.btnSave.setOnClickListener {
            if (NetworkUtil.isNetworkAvailable(this)) {
                saveDataToDatabase()
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                val failureDialog =
                    CustomDialog(this, CustomDialog.DialogType.FAILURE, "No internet connection")
                failureDialog.show()
            }
        }



        if (NetworkUtil.isNetworkAvailable(this)) {
            viewModel.loadProducts(axn, divisionCode)
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            val failureDialog =
                CustomDialog(this, CustomDialog.DialogType.FAILURE, "No internet connection")
            failureDialog.show()
        }


        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reload -> {
                if (NetworkUtil.isNetworkAvailable(this)) {
                    viewModel.loadProducts(axn, divisionCode)
                } else {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                    val failureDialog =
                        CustomDialog(
                            this,
                            CustomDialog.DialogType.FAILURE,
                            "No internet connection"
                        )
                    failureDialog.show()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveDataToDatabase() {
        val updatedProducts = adapter.getUpdatedProducts()
        val productBody = ProductBody(ArrayList(updatedProducts))
        Log.d("MainActivity", "productBody: $productBody")

        viewModel.saveProductDetails(productBody, divisionCode)
    }

    private fun observeViewModel() {
        viewModel.productList.observe(this) { result ->
            when (result) {
                is ApiResult.Loading -> {
                    if (!loadingDialog.isShowing) {
                        loadingDialog.show()
                    }
                    Log.d("MainActivity", "${result}")
                }

                is ApiResult.Success -> {
                    if (loadingDialog.isShowing) {
                        loadingDialog.dismiss()
                    }

                    val products = result.data
                    Log.d("MainActivity", "Products: $products")
                    adapter.updateProducts(products)
                    binding.btnSave.visibility = View.VISIBLE
                    binding.updateMessage.visibility = View.GONE

                }

                is ApiResult.Error -> {
                    if (loadingDialog.isShowing) {
                        loadingDialog.dismiss()
                    }

                    val errorMessage = result.message ?: "Unknown error occurred"
                    Log.e("MainActivity", "Error: $errorMessage")
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    val failureDialog =
                        CustomDialog(this, CustomDialog.DialogType.FAILURE, errorMessage)
                    failureDialog.show()
                }
            }
        }


        viewModel.saveProductResult.observe(this) { result ->
            when (result) {
                is ApiResult.Loading -> {
                    if (!loadingDialog.isShowing) {
                        loadingDialog.show()
                    }

                    Log.d("MainActivity", "${result}")
                }

                is ApiResult.Success -> {
                    if (loadingDialog.isShowing) {
                        loadingDialog.dismiss()
                    }

                    Log.d("MainActivity", "${result.data}")
                    Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
                    adapter.updateProducts(arrayListOf())
                    binding.btnSave.visibility = View.GONE
                    binding.updateMessage.visibility = View.VISIBLE
                    val successDialog = CustomDialog(
                        this,
                        CustomDialog.DialogType.SUCCESS,
                        "Data saved successfully!"
                    )
                    successDialog.show()

                }

                is ApiResult.Error -> {
                    if (loadingDialog.isShowing) {
                        loadingDialog.dismiss()
                    }

                    val errorMessage = result.message ?: "Error while saving data"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Error: $errorMessage")

                    val failureDialog =
                        CustomDialog(this, CustomDialog.DialogType.FAILURE, errorMessage)
                    failureDialog.show()

                }
            }
        }
    }

    private fun networkMonitor() {
        networkMonitor = NetworkMonitor(this)

        networkMonitor.observe(this) { isConnected ->
            if (isConnected) {
                Log.d("networkMonitor", "networkMonitor: $isConnected")
                Snackbar.make(binding.root, "Internet Connection Restored", Snackbar.LENGTH_SHORT)
                    .show()
            } else {

                Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") {
                        networkMonitor()
                    }
                    .show()
            }
        }

    }
}
