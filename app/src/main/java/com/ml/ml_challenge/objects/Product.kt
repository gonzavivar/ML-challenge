package com.ml.ml_challenge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Parcelize
data class Product(
    var id: String,
    var title: String,
    var price: Float,
    var thumbnail: String,
    var warranty: String? = null,
    var seller: Seller? = null,
    var pictures: ArrayList<Picture> = ArrayList(),
    var condition: String,
    @SerialName("available_quantity") var availableQuantity: Int,
    var shipping: Shipping,
    var installments: Installment? = null,
    @SerialName("sale_terms") var saleTerms: ArrayList<SaleTerm> = ArrayList(),
   var address: Address? = null
) : Parcelable
