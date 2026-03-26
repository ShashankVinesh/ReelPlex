package com.example.reelplex.Activity

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.reelplex.R
import com.example.reelplex.ViewModels.ReelUiState
import com.example.reelplex.ViewModels.ReelViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ReelViewModel
    private var videoUrl: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val etUrl = findViewById<EditText>(R.id.etUrl)
        val ivSearch = findViewById<ImageView>(R.id.ivSearchIcon)
        val ivThumbnail = findViewById<ImageView>(R.id.ivThumbnail)
        val tvTitle = findViewById<TextView>(R.id.tvVideoTitle)
        val btnDownload = findViewById<Button>(R.id.btnDownload)
        val progressBar = findViewById<ProgressBar>(R.id.progress_Bar)
        val previewPlayBtn = findViewById<ImageView>(R.id.previewPlayBtn)



        viewModel = ViewModelProvider(this)[ReelViewModel::class.java]

        ivSearch.setOnClickListener {
            viewModel.loadReel(etUrl.text.toString().trim())

        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->

                when (state) {

                    is ReelUiState.Idle -> {
                        progressBar.visibility = View.GONE
                        ivThumbnail.visibility = View.GONE
                        btnDownload.visibility = View.GONE
                        tvTitle.visibility = View.GONE
                        previewPlayBtn.visibility = View.GONE
                    }

                    is ReelUiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        ivThumbnail.visibility = View.GONE
                        btnDownload.visibility = View.GONE
                        tvTitle.visibility = View.GONE
                        previewPlayBtn.visibility = View.GONE

                    }

                    is ReelUiState.Success -> {
                        progressBar.visibility = View.GONE

                        ivThumbnail.visibility = View.VISIBLE
                        btnDownload.visibility = View.VISIBLE
                        tvTitle.visibility = View.VISIBLE
                        previewPlayBtn.visibility = View.VISIBLE

                        videoUrl = state.videoUrl

                        Glide.with(this@MainActivity)
                            .load(state.thumbnail ?: state.videoUrl)
                            .into(ivThumbnail)

                        tvTitle.text = state.caption ?: "Instagram Reel"
                    }

                    is ReelUiState.Error -> {
                        progressBar.visibility = View.GONE

                        ivThumbnail.visibility = View.GONE
                        btnDownload.visibility = View.GONE
                        tvTitle.visibility = View.GONE
                        previewPlayBtn.visibility = View.GONE

                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnDownload.setOnClickListener {

            if (videoUrl == null) {
                Toast.makeText(this, "Load a reel first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = DownloadManager.Request(Uri.parse(videoUrl))
                .setTitle("Reel Download")
                .setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )

            val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }
}