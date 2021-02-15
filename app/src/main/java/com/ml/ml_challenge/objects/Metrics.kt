package com.ml.ml_challenge.objects

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Metrics(
    @SerialName("delayed_handling_time") var delayedHandlingTime: DelayedHandlingTime,
    var sales: Sales
) : Parcelable {

    @Parcelize
    @Serializable
    data class Sales(var period: String, var completed: Int) : Parcelable

    @Parcelize
    @Serializable
    data class DelayedHandlingTime(var rate: Double, var value: Int) : Parcelable
}