package com.ml.ml_challenge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Seller(var eshop: Eshop? = null,@SerialName("seller_reputation") var reputation: Reputation? = null):Parcelable