package com.nitj.nitjadminapp.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseManager.FirebaseDatabaseManager

class UploadStudentCorner : AppCompatActivity() {

    private lateinit var tvUpEv: TextView
    private lateinit var tvSC: TextView
    private lateinit var tvScShip: TextView
    private lateinit var etTitle: EditText
    private lateinit var etUrl: EditText
    private lateinit var uploadButton: Button
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager

    var upEvent = true
    var studentCorner = false
    var scholarShip = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_student_corner)
        findViews()

        defaultSetting()

        tvUpEv.setOnClickListener {
            if (!upEvent) {
                defaultSetting()
            }
        }


        tvSC.setOnClickListener {

            if (!studentCorner) {
                etTitle.text.clear()
                etUrl.text.clear()
                studentCorner = true
                tvSC.setBackgroundResource(R.color.nitJ_primary)
                tvSC.setTextColor(resources.getColor(R.color.white))
                upEvent = false
                tvUpEv.setBackgroundResource(R.color.white)
                tvUpEv.setTextColor(resources.getColor(R.color.nitJ_primary))
                scholarShip = false
                tvScShip.setBackgroundResource(R.color.white)
                tvScShip.setTextColor(resources.getColor(R.color.nitJ_primary))
                etTitle.hint = "Student Corner Title"
                etUrl.hint = "Student Corner Url"
                uploadButton.text = "Upload Student Corner"
            }
        }

        tvScShip.setOnClickListener {
            if (!scholarShip) {
                etTitle.text.clear()
                etUrl.text.clear()
                scholarShip = true
                tvScShip.setBackgroundResource(R.color.nitJ_primary)
                tvScShip.setTextColor(resources.getColor(R.color.white))
                studentCorner = false
                tvSC.setBackgroundResource(R.color.white)
                tvSC.setTextColor(resources.getColor(R.color.nitJ_primary))
                upEvent = false
                tvUpEv.setBackgroundResource(R.color.white)
                tvUpEv.setTextColor(resources.getColor(R.color.nitJ_primary))
                etTitle.hint = "ScholarShip Title"
                etUrl.hint = "ScholarShip Url"
                uploadButton.text = "Upload ScholarShip Data"
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
            upEvent -> {
                firebaseDatabaseManager.uploadCommonData(
                    this,
                    etTitle,
                    etUrl,
                    "Student Common Data",
                    "Upcoming Event"
                )
            }
            studentCorner -> {
                firebaseDatabaseManager.uploadCommonData(
                    this,
                    etTitle,
                    etUrl,
                    "Student Common Data",
                    "Student Corner"
                )
            }
            scholarShip -> {
                firebaseDatabaseManager.uploadCommonData(
                    this,
                    etTitle,
                    etUrl,
                    "Student Common Data",
                    "ScholarShip"
                )
            }
        }
    }


    private fun defaultSetting() {
        upEvent = true
        tvUpEv.setBackgroundResource(R.color.nitJ_primary)
        tvUpEv.setTextColor(resources.getColor(R.color.white))
        studentCorner = false
        tvSC.setBackgroundResource(R.color.white)
        tvSC.setTextColor(resources.getColor(R.color.nitJ_primary))
        scholarShip = false
        tvScShip.setBackgroundResource(R.color.white)
        tvScShip.setTextColor(resources.getColor(R.color.nitJ_primary))
        etTitle.hint = "Upcoming Event Title"
        etUrl.hint = "Upcoming Event Url"
        uploadButton.text = "Upload Upcoming Event"
        etTitle.text.clear()
        etUrl.text.clear()
    }


    private fun findViews() {
        tvUpEv = findViewById(R.id.tvUpEv)
        tvSC = findViewById(R.id.tvSC)
        tvScShip = findViewById(R.id.tvScShip)
        etTitle = findViewById(R.id.etTitle)
        etUrl = findViewById(R.id.etUrl)
        uploadButton = findViewById(R.id.uploadButton)
        firebaseDatabaseManager = FirebaseDatabaseManager()
    }
}