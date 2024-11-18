package com.zybooks.workingwithdata

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DataViewActivity : AppCompatActivity() {
    data class ImageData(val url: String, val description: String, val videoUrl: String? = null)

    private val dummyData = listOf(
        ImageData("https://apod.nasa.gov/apod/image/1908/EtnaCloudsMoon.jpg", "Beautiful moonlit sky above Mount Etna", null),
        ImageData("https://img.youtube.com/vi/f8rs3bcEO-o/0.jpg", "Black hole video thumbnail", "https://youtube.com/watch?v=f8rs3bcEO-o"),
        ImageData("https://apod.nasa.gov/apod/image/1908/OrionNebula.jpg", "Stunning view of Orion Nebula", null)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_view)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ImageAdapter(dummyData) { image ->
            // Handle item click
            val intent = Intent(this, ItemDetailActivity::class.java).apply {
                putExtra("url", image.url)
                putExtra("description", image.description)
                putExtra("videoUrl", image.videoUrl)
            }
            startActivity(intent)
        }
    }
}
