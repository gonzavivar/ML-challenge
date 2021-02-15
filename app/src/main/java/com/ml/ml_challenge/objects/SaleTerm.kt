package com.ml.ml_challenge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*"INVOICE"*/
@Parcelize
@Serializable
data class SaleTerm(var id:String,
@SerialName("value_name") var valueName:String):Parcelable