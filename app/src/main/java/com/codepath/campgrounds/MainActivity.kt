package com.codepath.campgrounds

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.campgrounds.databinding.ActivityMainBinding
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.serialization.json.Json
import okhttp3.Headers

fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}

private const val TAG = "CampgroundsMain/"
private val PARKS_API_KEY = BuildConfig.API_KEY
private val CAMPGROUNDS_URL =
    "https://developer.nps.gov/api/v1/campgrounds?api_key=${PARKS_API_KEY}"

class MainActivity : AppCompatActivity() {
    private lateinit var campgroundsRecyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private val campgrounds = mutableListOf<Campground>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        campgroundsRecyclerView = findViewById(R.id.campgrounds)

        val campgroundAdapter = CampgroundAdapter(this, campgrounds)
        campgroundsRecyclerView.adapter = campgroundAdapter

        campgroundsRecyclerView.layoutManager = LinearLayoutManager(this).also {
            val dividerItemDecoration = DividerItemDecoration(this, it.orientation)
            campgroundsRecyclerView.addItemDecoration(dividerItemDecoration)
        }

        campgrounds.add(
            Campground(
                name = "Test Campground",
                description = "This is a test to verify the RecyclerView works",
                latitude = "47.5",
                longitude = "-120.5",
                images = listOf(CampgroundImage(url = "https://www.nps.gov/common/uploads/parks/8f8a5c3a-1dd1-b71b-0b23-d1234567890a/8f8a5c3a-1dd1-b71b-0b23-d1234567890a.jpg"))
            )
        )
        campgroundAdapter.notifyDataSetChanged()

        Log.d(TAG, "API Key: $PARKS_API_KEY")
        Log.d(TAG, "API URL: $CAMPGROUNDS_URL")

        val client = AsyncHttpClient()
        client.get(CAMPGROUNDS_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch campgrounds: $statusCode")
                Log.e(TAG, "Response: $response")
                Log.e(TAG, "Throwable: ${throwable?.message}", throwable)
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "Successfully fetched campgrounds")
                Log.d(TAG, "Full JSON response: ${json.jsonObject.toString().take(500)}")
                try {
                    val jsonString = json.jsonObject.toString()
                    Log.d(TAG, "JSON Keys: ${json.jsonObject.keys()}")

                    val parsedJson = createJson().decodeFromString(
                        CampgroundResponse.serializer(),
                        jsonString
                    )
                    Log.d(TAG, "Successfully parsed response. Campgrounds count: ${parsedJson.data?.size ?: 0}")

                    parsedJson.data?.let { list ->
                        Log.d(TAG, "Adding ${list.size} campgrounds to list")
                        list.forEach { camp ->
                            Log.d(TAG, "Campground: ${camp.name}, Images: ${camp.images?.size ?: 0}")
                        }
                        campgrounds.clear()
                        campgrounds.addAll(list)

                        campgroundAdapter.notifyDataSetChanged()
                        Log.d(TAG, "Adapter notified. Campgrounds in list: ${campgrounds.size}")
                    } ?: run {
                        Log.w(TAG, "No data in response - attempting alternative parsing")
                        try {
                            val directList = createJson().decodeFromString<List<Campground>>(jsonString)
                            Log.d(TAG, "Successfully parsed ${directList.size} campgrounds from direct array")
                            campgrounds.clear()
                            campgrounds.addAll(directList)
                            campgroundAdapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                            Log.e(TAG, "Alternative parsing also failed: ${e.message}")
                        }
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Exception: ${e.message}", e)
                    e.printStackTrace()
                }
            }

        })
    }
}
