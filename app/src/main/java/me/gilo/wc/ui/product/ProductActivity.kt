package me.gilo.wc.ui.product

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.content_product.*
import me.gilo.wc.R
import me.gilo.wc.adapter.HomeProductAdapter
import me.gilo.wc.adapter.ImagePagerAdapter
import me.gilo.wc.common.BaseActivity
import me.gilo.wc.common.Status
import me.gilo.wc.ui.state.ProgressDialogFragment
import me.gilo.wc.viewmodels.ProductViewModel
import me.gilo.woodroid.models.Product
import java.util.ArrayList

class ProductActivity : BaseActivity() {

    lateinit var viewModel: ProductViewModel
    val TAG = this::getLocalClassName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        setSupportActionBar(toolbar)

        viewModel = getViewModel(ProductViewModel::class.java)

        title = "Product"

        val productId = intent.getIntExtra("productId", 0)

        if (productId != 0){
            product(productId)
            similarProducts()
        }

        fab.setOnClickListener{addToCart(productId)}

    }


    lateinit var adapter: HomeProductAdapter
    private lateinit var products: ArrayList<Product>

    //TODO(Use the include product filter to get related products from API)
    private fun similarProducts() {
        val layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
        rvShop.layoutManager = layoutManager
        rvShop.isNestedScrollingEnabled = false

        products = ArrayList()

        adapter = HomeProductAdapter(products)
        rvShop.adapter = adapter

        viewModel.products().observe(this, android.arch.lifecycle.Observer { response ->
            when (response!!.status()) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    products.clear()
                    val productsResponse = response.data()
                    for (product in productsResponse) {
                        products.add(product)
                    }

                    adapter.notifyDataSetChanged()

                }

                Status.ERROR -> {

                }

                Status.EMPTY -> {

                }
            }

        })
    }

    private fun addToCart(productId: Int) {
        viewModel.addToCart(baseContext, productId).observe(this, android.arch.lifecycle.Observer { response ->
            when (response!!.status()) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    val order = response.data()


                }

                Status.ERROR -> {

                }

                Status.EMPTY -> {

                }
            }

        })
    }


    private fun product(productId : Int) {
        viewModel.product(productId).observe(this, android.arch.lifecycle.Observer { response ->
            when (response!!.status()) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    val product = response.data()
                    setUpPage(product)

                }

                Status.ERROR -> {

                }

                Status.EMPTY -> {

                }
            }

        })

    }

    private fun setUpPage(product: Product) {
        tvTitle.text = product.name
        tvDescription.text = Html.fromHtml(product.description)

        if (product.images != null && product.images.isNotEmpty()){
            vpImages.offscreenPageLimit = product.images.size
            vpImages.adapter = ImagePagerAdapter(baseContext, product.images)

            indicator.setViewPager(vpImages)
        }

        if (product.isOn_sale) {
           tvCallToAction.text = Html.fromHtml(product.price_html)

            tvOnSale.visibility = View.VISIBLE
        }else{
            tvCallToAction.text = Html.fromHtml(product.price_html).trim()
            tvOnSale.visibility = View.GONE
        }
    }

    private lateinit var progressDialog: ProgressDialogFragment

    fun showLoading(title: String, message: String) {
        val manager = supportFragmentManager
        progressDialog = ProgressDialogFragment.newInstance(title, message)
        progressDialog.isCancelable = false
        progressDialog.show(manager, "progress")
    }

    fun showLoading() {
        showLoading("This will only take a sec", "Loading")
    }

    fun stopShowingLoading() {
        progressDialog.dismiss()
    }

}
