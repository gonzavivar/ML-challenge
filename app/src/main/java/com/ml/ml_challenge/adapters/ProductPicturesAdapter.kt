package com.ml.ml_challenge.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.ml.ml_challenge.objects.Picture
import kotlinx.android.synthetic.main.item_product_picture.view.*

class ProductPicturesAdapter(var pictures: ArrayList<Picture>) :
    RecyclerView.Adapter<ProductPicturesAdapter.PictureViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        return PictureViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_picture,parent, false))
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val picture = pictures[position]
        val context = holder.picture.context

        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(picture.url))
            .build()

        val imagePipeline = Fresco.getImagePipeline()
        val dataSource = imagePipeline.fetchDecodedImage(imageRequest, context)

        dataSource.subscribe(
            object : BaseBitmapDataSubscriber() {
                override fun onNewResultImpl(bitmap: Bitmap?) {
                    if (dataSource.isFinished && bitmap != null) {
                        holder.picture.setImageBitmap(bitmap)
                        dataSource.close()
                    }
                }

                override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage?>>) {
                    dataSource.close()
                }
            }, CallerThreadExecutor.getInstance()
        )
    }

    override fun getItemCount(): Int = pictures.size

    class PictureViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var picture:SimpleDraweeView= view.product_picture
    }
}