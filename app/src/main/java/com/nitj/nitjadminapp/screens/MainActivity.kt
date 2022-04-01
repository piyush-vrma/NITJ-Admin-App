package com.nitj.nitjadminapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.google.firebase.messaging.FirebaseMessaging
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseNotificationJava.Constant.TOPIC

class MainActivity : AppCompatActivity() {

    private lateinit var addNotice: CardView
    private lateinit var addGallery: CardView
    private lateinit var addEbook: CardView
    private lateinit var faculty: CardView
    private lateinit var deleteNotice: CardView
    private lateinit var addHomePageSlidingImage: CardView
    private lateinit var addStudentCorner: CardView
    private lateinit var addDownloadLinks: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        addNotice.setOnClickListener {
            val addNoticeIntent = Intent(this, UploadNotice::class.java)
            startActivity(addNoticeIntent)
        }
        addGallery.setOnClickListener {
            val addGalleryIntent = Intent(this, UploadGalleryImages::class.java)
            startActivity(addGalleryIntent)
        }
        addEbook.setOnClickListener {
            val addEbookIntent = Intent(this, UploadEbook::class.java)
            startActivity(addEbookIntent)
        }

        faculty.setOnClickListener {
            val addFacultyIntent = Intent(this, AllBranches::class.java)
            startActivity(addFacultyIntent)
        }

        deleteNotice.setOnClickListener {
            val deleteNoticeIntent = Intent(this, DeleteNotice::class.java)
            startActivity(deleteNoticeIntent)
        }
        addHomePageSlidingImage.setOnClickListener {
            val addHomePageSlidingImageIntent = Intent(this, AutoImageSlider::class.java)
            startActivity(addHomePageSlidingImageIntent)
        }
        addStudentCorner.setOnClickListener {
            val addStudentCornerIntent = Intent(this, UploadStudentCorner::class.java)
            startActivity(addStudentCornerIntent)
        }
        addDownloadLinks.setOnClickListener {
            val addDownloadLinksIntent = Intent(this, UploadDownloadOtherLInks::class.java)
            startActivity(addDownloadLinksIntent)
        }
    }

    private fun bindViews() {
        addNotice = findViewById(R.id.addNotice)
        addEbook = findViewById(R.id.addEBook)
        addGallery = findViewById(R.id.addGalleryImage)
        faculty = findViewById(R.id.faculty)
        deleteNotice = findViewById(R.id.deleteNotice)
        addHomePageSlidingImage = findViewById(R.id.addHomePageSlidingImage)
        addStudentCorner = findViewById(R.id.addStudentCorner)
        addDownloadLinks = findViewById(R.id.addDownloadLinks)
    }
}