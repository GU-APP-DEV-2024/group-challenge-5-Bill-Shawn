package com.zybooks.workingwithdata

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

const val TAG = "NASA_API"

class NasaAPI : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var imageDataSet: ArrayList<ImageData>
    lateinit var imageCustomAdapter: ImageCustomAdapter
    lateinit var countEditText: EditText
    lateinit var startDateEditText: EditText
    lateinit var endDateEditText: EditText

    data class ImageData(val url: String, val description: String = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nasa_api)
        Log.d(TAG, "API Key from BuildConfig: ${BuildConfig.NASA_API_KEY}")

        // Initialize dataset and adapter
        imageDataSet = arrayListOf()
        imageCustomAdapter = ImageCustomAdapter(imageDataSet)

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = imageCustomAdapter

        // Initialize UI elements
        countEditText = findViewById(R.id.countEditText)
        startDateEditText = findViewById(R.id.startDateEditText)
        endDateEditText = findViewById(R.id.endDateEditText)

        // Set up search button listener
        findViewById<Button>(R.id.searchButton).setOnClickListener {
            searchAPOD()
        }
    }

    private fun searchAPOD() {
        val apiKey = BuildConfig.NASA_API_KEY

        // Validate API key
        if (apiKey.isNullOrEmpty()) {
            Log.e(TAG, "API key is missing or invalid!")
            Toast.makeText(this, "API key is not configured. Check your setup.", Toast.LENGTH_LONG).show()
            return
        }

        // Build the API URL
        val baseUrl = "https://api.nasa.gov/planetary/apod"
        var url = "$baseUrl?thumbs=true&api_key=$apiKey"

        val count = countEditText.text.toString().toIntOrNull()
        if (count != null && count > 0) {
            url += "&count=$count"
        } else {
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                url += "&start_date=$startDate&end_date=$endDate"
            } else if (startDate.isNotEmpty()) {
                url += "&date=$startDate"
            }
        }

        // Log the API request URL for debugging
        Log.d(TAG, "API Request URL: $url")

        // Create the network request
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response -> processRequest(response) },
            { error ->
                Log.e(TAG, "Error fetching data: ${error.message}")
                Toast.makeText(this, "Failed to fetch data. Please try again.", Toast.LENGTH_LONG).show()
            }
        )

        // Add the request to the queue
        queue.add(request)
    }

    private fun processRequest(response: JSONArray) {
        imageDataSet.clear()
        for (i in 0 until response.length()) {
            val jsonObject = response.getJSONObject(i)
            val url = jsonObject.getString("url")
            val description = jsonObject.optString("explanation", "No description")
            imageDataSet.add(ImageData(url, description))
        }
        imageCustomAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Data successfully loaded!", Toast.LENGTH_SHORT).show()
    }
}
