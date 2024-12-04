package com.zybooks.workingwithdata

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // Retrieve data from intent
        val imageUrl = intent.getStringExtra("url")
        val description = intent.getStringExtra("description")
        val videoUrl = intent.getStringExtra("videoUrl")

        // Initialize views
        val imageView: ImageView = findViewById(R.id.imageView)
        val textView: TextView = findViewById(R.id.textView)
        val shareButton: Button = findViewById(R.id.shareButton)
        val videoButton: Button = findViewById(R.id.videoButton)

        // Load image and set description
        Glide.with(this).load(imageUrl).into(imageView)
        textView.text = description

        // Set up share functionality
        // bonus I
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "$description\n$imageUrl")
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        // Set up video button if video URL exists
        // Bonus III
        if (videoUrl != null && videoUrl.isNotEmpty()) {
            videoButton.visibility = Button.VISIBLE
            videoButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                startActivity(intent)
            }
        } else {
            videoButton.visibility = Button.GONE
        }
    }
}
