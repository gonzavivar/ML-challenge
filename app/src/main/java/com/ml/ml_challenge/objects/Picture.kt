package com.ml.ml_challenge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Picture(var url:String):Parcelable