package com.codepath.campgrounds

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

const val CAMPGROUND_EXTRA = "CAMPGROUND_EXTRA"

class DetailActivity : AppCompatActivity() {
    private lateinit var campgroundNameTV: TextView
    private lateinit var campgroundDescriptionTV: TextView
    private lateinit var campgroundLatLongTV: TextView
    private lateinit var campgroundImageIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        campgroundNameTV = findViewById(R.id.campgroundName)
        campgroundDescriptionTV = findViewById(R.id.campgroundDescription)
        campgroundLatLongTV = findViewById(R.id.campgroundLocation)
        campgroundImageIV = findViewById(R.id.campgroundImage)

        val campground = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CAMPGROUND_EXTRA, Campground::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(CAMPGROUND_EXTRA) as? Campground
        }

        if (campground != null) {
            campgroundNameTV.text = campground.name ?: "Unknown Campground"
            campgroundLatLongTV.text = campground.latLong
            campgroundDescriptionTV.text = campground.description ?: "No description available"

            Glide.with(this)
                .load(campground.imageUrl)
                .into(campgroundImageIV)
        }
    }
}
