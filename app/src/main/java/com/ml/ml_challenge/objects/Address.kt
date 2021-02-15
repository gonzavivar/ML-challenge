package com.ml.ml_challenge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Address(
    @SerialName("state_name") var stateName: String,
    @SerialName("city_name") var cityName: String
) : Parcelable