package com.ml.ml_challenge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Eshop(
    @SerialName("nick_name") var nickName: String,
    @SerialName("eshop_logo_url") var eshopLogoUrl: String
) : Parcelable {
}