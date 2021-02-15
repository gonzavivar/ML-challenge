package com.ml.ml_challenge.activities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.ml.ml_challenge.R
import com.ml.ml_challenge.RestUtils
import com.ml.ml_challenge.activities.MainActivity.Companion.EXTRA_PRODUCT
import com.ml.ml_challenge.adapters.ProductPicturesAdapter
import com.ml.ml_challenge.objects.Product
import kotlinx.android.synthetic.main.action_bar_product_details.*
import kotlinx.android.synthetic.main.action_bar_searcher.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONException
import kotlin.math.roundToInt

class ProductDetailsActivity : AppCompatActivity() {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val productSelected = intent.getParcelableExtra<Product>(EXTRA_PRODUCT)
        GlobalScope.launch(Dispatchers.Main) {

            if (productSelected == null) {
                finish()
            }

            val description = getProductDescription(productSelected!!.id).await()
            val productDetails = getProductDetails(productSelected.id).await()

            back_arrow.setOnClickListener {
                onBackPressed()
            }

            if (productSelected.condition == STATE_NEW) {
                product_state.text = getString(R.string.product_state_new)
            } else {
                product_state.text = getString(R.string.product_state_used)
            }

            product_title.text = productSelected.title
            product_price.text = getString(R.string.price, productSelected.price.roundToInt())

            productSelected.installments?.let {
                installments.text =
                    getString(R.string.installments_text, it.quantity, it.amount.roundToInt())
            }

            if (productSelected.shipping.freeShipping) {
                shipping_title.text = getString(R.string.free_shepping_text)
            } else {
                shipping_title.text = getString(R.string.shepping_text)
                shipping_desc.visibility = View.GONE
            }

            productSelected.seller?.let { seller ->
                seller.eshop?.let {
                    eshop_information.visibility = View.VISIBLE
                    seller_information.visibility = View.GONE

                    store_name.text = getString(R.string.official_store_text, it.nickName)
                    eshop_name.text =  it.nickName

                    val imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(it.eshopLogoUrl))
                        .build()

                    val imagePipeline = Fresco.getImagePipeline()
                    val dataSource = imagePipeline.fetchDecodedImage(imageRequest, this@ProductDetailsActivity)
                    dataSource.subscribe(
                        object : BaseBitmapDataSubscriber() {
                            override fun onNewResultImpl(bitmap: Bitmap?) {
                                if (dataSource.isFinished && bitmap != null) {
                                    eshop_picture.setImageBitmap(bitmap)
                                    dataSource.close()
                                }
                            }

                            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage?>>) {
                                dataSource.close()
                            }
                        }, CallerThreadExecutor.getInstance()
                    )
                }

                seller_sales.text = seller.reputation.metrics.sales.completed.toString()

                if (seller.reputation.metrics.sales.period == SIXTY_SALES_PERIOD_ID) {
                    seller_sales_desc.text =
                        getString(R.string.last_days_sales, 60)
                }else{
                    seller_sales_desc.text =
                        getString(R.string.last_days_sales, 365)
                }

                if (seller.reputation.metrics.delayedHandlingTime.rate < 0.2) {
                    delayed_handling_time_desc.text =
                        getString(R.string.not_ship_your_products_on_time)
                }

            }

            product_description.text = description

            if (productSelected.availableQuantity == 1) {
                quantity_selector.visibility = View.GONE
                stock_desc.text = getString(R.string.last_unit)
            } else {
                stock_desc.text = getString(R.string.available_stock)
                quantity_selector.text =
                    getString(R.string.quantity_selector, productSelected.availableQuantity)
            }

            if (productSelected.address != null) {
                seller_address.text = getString(
                    R.string.seller_address,
                    productSelected.address!!.cityName,
                    productSelected.address!!.stateName
                )
            } else {
                seller_address.visibility = View.GONE
                seller_address_icon.visibility = View.GONE
                seller_address_title.visibility = View.GONE
            }


            productDetails?.let { product ->
                val billingTerm = productDetails.saleTerms.find { it.id == SALE_TERM_BILLING_ID }
                if (billingTerm != null) {
                    store_billing.text = getString(R.string.billing, billingTerm.valueName)
                } else {
                    store_billing.visibility = View.GONE
                }

                if (productDetails.warranty != null) {
                    warranty_desc.text = productDetails.warranty
                } else {
                    warranty_icon.visibility = View.GONE
                    warranty_desc.visibility = View.GONE
                }

                page_number.text = getString(R.string.pege_number_text, 1, product.pictures.size)
                product_pictures_vp.adapter = ProductPicturesAdapter(product.pictures)
                product_pictures_vp.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        page_number.text =
                            getString(
                                R.string.pege_number_text,
                                position + 1,
                                product.pictures.size
                            )
                    }
                })
            }

        }

    }

    private fun getProductDetails(productId: String) = GlobalScope.async(Dispatchers.IO) {
        val response = RestUtils.get("https://api.mercadolibre.com/items?ids=${productId}")
        response?.let {
            val responseJsonArray = JSONArray(response)
            var productDetails: Product? = null
            try {
                productDetails =
                    json.decodeFromString(
                        responseJsonArray.getJSONObject(0).getJSONObject("body").toString()
                    )
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return@async productDetails
        }

        return@async null
    }

    private fun getProductDescription(productId: String) = GlobalScope.async(Dispatchers.IO) {
        val response =
            RestUtils.get("https://api.mercadolibre.com/items?ids=${productId}/descriptions")
        var description: String? = null
        response?.let {
            val responseJsonArray = JSONArray(response)
            try {
                description =
                    responseJsonArray.getJSONObject(0).getJSONArray("body").getJSONObject(0)
                        .getString("plain_text")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return@async description
        }

        return@async description
    }

    companion object {
        const val STATE_NEW = "new"
        const val SALE_TERM_BILLING_ID = "INVOICE"
        const val SIXTY_SALES_PERIOD_ID = "60 days"
        const val SALES_PERIOD_ID = "365 days"
    }
}