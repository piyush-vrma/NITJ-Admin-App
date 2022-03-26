package com.nitj.nitjadminapp.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseManager.FirebaseStorageManager

class UploadNotice : AppCompatActivity() {

    private lateinit var selectImage: CardView
    private lateinit var noticeTitle: EditText
    private lateinit var uploadNoticeButton: Button
    private lateinit var noticeImageView: ImageFilterView
    private var imageUri: Uri? = null
    private lateinit var firebaseStorageManager: FirebaseStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_notice)
        findView()

        selectImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        uploadNoticeButton.setOnClickListener {
            noticeTitle.clearFocus()
            when {
                noticeTitle.text.toString().trim().isEmpty() -> {
                    noticeTitle.error = "Please enter Notice Title"
                    noticeTitle.requestFocus()
                }
                imageUri == null -> {
                    Toast.makeText(this, "Please Select Image to Upload", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    uploadImage()
                }
            }
        }
    }


    private fun uploadImage() {
        firebaseStorageManager = FirebaseStorageManager()
        firebaseStorageManager.uploadImageToFirebaseStorage(
            this,
            imageUri!!,
            "Notice",
            noticeTitle,
            noticeImageView
        )
        imageUri = null

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                imageUri = data?.data!!

                // Use Uri object instead of File to avoid storage permissions
                noticeImageView.setImageURI(imageUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findView() {
        selectImage = findViewById(R.id.selectImage)
        noticeTitle = findViewById(R.id.noticeTitle)
        uploadNoticeButton = findViewById(R.id.uploadNoticeButton)
        noticeImageView = findViewById(R.id.noticeImageView)
    }

}