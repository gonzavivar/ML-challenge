package com.ml.ml_challenge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import org.json.JSONObject

@Serializable
@Parcelize
data class Search(var paging:Paging, var results:ArrayList<Product>):Parcelable