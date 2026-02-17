package com.codepath.campgrounds

import android.os.Parcelable
import android.support.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CampgroundResponse(
    @SerialName("data")
    val data: List<Campground>?
)

@Keep
@Serializable
@Parcelize
data class Campground(
    @SerialName("name")
    val name: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("latitude")
    val latitude: String?,
    @SerialName("longitude")
    val longitude: String?,
    @SerialName("images")
    val images: List<CampgroundImage>?
) : Parcelable {
    val latLong: String
        get() {
            val lat = latitude?.takeIf { it.isNotBlank() }?.toDoubleOrNull() ?: 0.0
            val lng = longitude?.takeIf { it.isNotBlank() }?.toDoubleOrNull() ?: 0.0
            return "($lat, $lng)"
        }

    val imageUrl: String?
        get() = images?.firstOrNull()?.url
}

@Keep
@Serializable
@Parcelize
data class CampgroundImage(
    @SerialName("url")
    val url: String?
) : Parcelable

