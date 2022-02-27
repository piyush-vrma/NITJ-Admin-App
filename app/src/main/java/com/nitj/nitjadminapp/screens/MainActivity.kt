package com.nitj.nitjadminapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.nitj.nitjadminapp.R

class MainActivity : AppCompatActivity() {

    lateinit var addNotice: CardView;
    lateinit var addGallery: CardView;
    lateinit var addEbook: CardView;
    lateinit var faculty: CardView;
    lateinit var deleteNotice: CardView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
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
    }

    private fun bindViews() {
        addNotice = findViewById(R.id.addNotice)
        addEbook = findViewById(R.id.addEBook)
        addGallery = findViewById(R.id.addGalleryImage)
        faculty = findViewById(R.id.faculty)
        deleteNotice = findViewById(R.id.deleteNotice)
    }
}