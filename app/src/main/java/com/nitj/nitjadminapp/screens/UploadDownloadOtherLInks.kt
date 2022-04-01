package com.nitj.nitjadminapp.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseManager.FirebaseDatabaseManager

class UploadDownloadOtherLInks : AppCompatActivity() {

    private lateinit var tvDownload: TextView
    private lateinit var tvOtherLinks: TextView
    private lateinit var etTitle: EditText
    private lateinit var etUrl: EditText
    private lateinit var uploadButton: Button
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager

    var downloads = true
    var otherLinks = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_download_other_links)
        findViews()

        defaultSetting()

        tvDownload.setOnClickListener {
            if (!downloads) {
                defaultSetting()
            }
        }


        tvOtherLinks.setOnClickListener {

            if (!otherLinks) {
                etTitle.text.clear()
                etUrl.text.clear()
                otherLinks = true
                tvOtherLinks.setBackgroundResource(R.color.nitJ_primary)
                tvOtherLinks.setTextColor(resources.getColor(R.color.white))
                downloads = false
                tvDownload.setBackgroundResource(R.color.white)
                tvDownload.setTextColor(resources.getColor(R.color.nitJ_primary))
                etTitle.hint = "OtherLinks Title"
                etUrl.hint = "OtherLinks Url"
                uploadButton.text = "Upload OtherLinks"
            }
        }

        uploadButton.setOnClickListener {
            when {
                etTitle.text.toString().trim().isEmpty() -> {
                    etTitle.error = "Please enter Title"
                    etTitle.requestFocus()
                }
                etUrl.text.toString().trim().isEmpty() -> {
                    etUrl.error = "Please enter Url"
                    etUrl.requestFocus()
                }
                else -> {
                    uploadData()
                }
            }
        }
    }

    private fun uploadData() {
        when {
            downloads -> {
                firebaseDatabaseManager.uploadCommonData(
                    this,
                    etTitle,
                    etUrl,
                    "Downloads_OtherLinks",
                    "Downloads"
                )
            }
            otherLinks -> {
                firebaseDatabaseManager.uploadCommonData(
                    this,
                    etTitle,
                    etUrl,
                    "Downloads_OtherLinks",
                    "OtherLinks"
                )
            }
        }
    }


    private fun defaultSetting() {
        downloads = true
        tvDownload.setBackgroundResource(R.color.nitJ_primary)
        tvDownload.setTextColor(resources.getColor(R.color.white))
        otherLinks = false
        tvOtherLinks.setBackgroundResource(R.color.white)
        tvOtherLinks.setTextColor(resources.getColor(R.color.nitJ_primary))
        etTitle.hint = "Downloads Title"
        etUrl.hint = "Downloads Url"
        uploadButton.text = "Upload Downloads"
        etTitle.text.clear()
        etUrl.text.clear()
    }


    private fun findViews() {
        tvDownload = findViewById(R.id.tvDownload)
        tvOtherLinks = findViewById(R.id.tvOtherLinks)
        etTitle = findViewById(R.id.etTitle)
        etUrl = findViewById(R.id.etUrl)
        uploadButton = findViewById(R.id.uploadButton)
        firebaseDatabaseManager = FirebaseDatabaseManager()
    }
}