package com.nitj.nitjadminapp.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseManager.FirebaseStorageManager

class AutoImageSlider : AppCompatActivity() {

    private lateinit var selectImage: CardView
    private lateinit var sliderImageTitle: EditText
    private lateinit var uploadSliderImageButton: Button
    private lateinit var homeSliderImageView: ImageFilterView
    private var imageUri: Uri? = null
    private lateinit var firebaseStorageManager: FirebaseStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_image_slider)

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

        uploadSliderImageButton.setOnClickListener {
            sliderImageTitle.clearFocus()
            when {
                sliderImageTitle.text.toString().trim().isEmpty() -> {
                    sliderImageTitle.error = "Please enter Slider Image Title"
                    sliderImageTitle.requestFocus()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadImage() {
        firebaseStorageManager = FirebaseStorageManager()
        firebaseStorageManager.uploadImageToFirebaseStorage(
            this,
            imageUri!!,
            "Slider Image",
            sliderImageTitle,
            homeSliderImageView
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
                homeSliderImageView.setImageURI(imageUri)
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
        sliderImageTitle = findViewById(R.id.sliderImageTitle)
        uploadSliderImageButton = findViewById(R.id.uploadSliderImageButton)
        homeSliderImageView = findViewById(R.id.homeSliderImageView)
    }

}