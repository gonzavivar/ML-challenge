package com.ml.ml_challenge.activities

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig
import com.ml.ml_challenge.R
import com.ml.ml_challenge.RestUtils
import com.ml.ml_challenge.adapters.ProductsAdapter
import com.ml.ml_challenge.objects.Product
import com.ml.ml_challenge.objects.Search
import kotlinx.android.synthetic.main.action_bar_searcher.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = ImagePipelineConfig.newBuilder(this)
            .setProgressiveJpegConfig(SimpleProgressiveJpegConfig())
            .setResizeAndRotateEnabledForNetwork(true)
            .setDownsampleEnabled(true)
            .build()

        Fresco.initialize(this, config)

        search_box.setOnEditorActionListener { v: TextView, actionId: Int, _: KeyEvent? ->
            searchAndShowResponse()
            false
        }

        search_icon.setOnClickListener {
            searchAndShowResponse()
        }

    }

    private fun searchAndShowResponse() {
        search_icon.isFocusable = false
        search_icon.isClickable = false
        progress_bar.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.Main) {

            val response = withContext(Dispatchers.IO) {
                RestUtils.get("https://api.mercadolibre.com/sites/MLA/search?q=${search_box.text}")
            }

            val json = Json {
                ignoreUnknownKeys = true
            }

            var search: Search? = null
            var results: ArrayList<Product> = ArrayList<Product>()
            try {
                search =
                    json.decodeFromString(response.toString())
                search?.let {
                    results = search.results
                    result_counter.text = getString(R.string.results, search.paging.total)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            products_rv.adapter =
                ProductsAdapter(results, object : ProductsAdapter.Companion.OnProductClickListener {
                    override fun onProductClick(position: Int) {
                        val intent = Intent(this@MainActivity, ProductDetailsActivity::class.java)
                        intent.putExtra(EXTRA_PRODUCT, results[position])
                        startActivity(intent)
                    }
                })

            progress_bar.visibility = View.GONE
            search_icon.isFocusable = true
            search_icon.isClickable = true
            //create and load adapter
        }
    }

    companion object {
        const val EXTRA_PRODUCT:String = "extra-product"
    }
}