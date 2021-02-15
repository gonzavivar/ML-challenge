package com.ml.ml_challenge.adapters

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.ml.ml_challenge.R
import com.ml.ml_challenge.objects.Product
import kotlinx.android.synthetic.main.item_product.view.*
import kotlin.math.roundToInt

class ProductsAdapter(
    private val products: ArrayList<Product>,
    private val onProductClickListener: OnProductClickListener
) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product: Product = products[position]
        val context: Context = holder.title.context
        holder.title.text = product.title
        holder.price.text = context.getString(R.string.price, product.price.roundToInt())

        if (product.shipping.freeShipping) {
            holder.shippingTv.visibility = View.VISIBLE
        } else {
            holder.shippingTv.visibility = View.GONE
        }

        if (product.seller != null && product.seller!!.eshop != null) {
            holder.eshopName.text = context.getString(R.string.sold_by,product.seller!!.eshop!!.nickName)
            holder.eshopName.visibility = View.VISIBLE
        } else {
            holder.eshopName.visibility = View.GONE
        }

        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(product.thumbnail))
            .build()

        val imagePipeline = Fresco.getImagePipeline()
        val dataSource = imagePipeline.fetchDecodedImage(imageRequest, context)

        dataSource.subscribe(
            object : BaseBitmapDataSubscriber() {
                override fun onNewResultImpl(bitmap: Bitmap?) {
                    if (dataSource.isFinished && bitmap != null) {
                        holder.image.setImageBitmap(bitmap)
                        dataSource.close()
                    }
                }

                override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage?>>) {
                    dataSource.close()
                }
            }, CallerThreadExecutor.getInstance()
        )

        holder.view.setOnClickListener {
            onProductClickListener.onProductClick(position)
        }
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.product_title
        var price: TextView = view.product_price
        var image: SimpleDraweeView = view.product_picture
        var shippingTv: TextView = view.shipping_title
        var eshopName: TextView = view.eshop_name
    }

    companion object {
        interface OnProductClickListener {
            fun onProductClick(position: Int)
        }
    }
}