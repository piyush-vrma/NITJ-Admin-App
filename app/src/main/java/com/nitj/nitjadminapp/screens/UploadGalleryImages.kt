package com.nitj.nitjadminapp.screens

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.adapters.FacultyAdapter
import com.nitj.nitjadminapp.firebaseManager.FirebaseStorageManager
import com.nitj.nitjadminapp.models.FacultyData
import com.nitj.nitjadminapp.util.ConnectionManager
import java.util.*

class UploadGalleryImages : AppCompatActivity() {

    private lateinit var selectImageForGallery: MaterialCardView
    private lateinit var addNewCategory: EditText
    private lateinit var selectImageCategory: Spinner
    private lateinit var uploadGalleryImageButton: Button
    private lateinit var galleryImageView: ImageFilterView
    private lateinit var fabGallery: FloatingActionButton
    private var imageUri: Uri? = null
    private var category: String = "Select Category"
    private lateinit var firebaseStorageManager: FirebaseStorageManager
    private lateinit var progressDialog: ProgressDialog
    private var databaseRef = FirebaseDatabase.getInstance().reference
    private var addCategoryBool: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_gallery_images)
        findViews()
        inflateSpinner()

        selectImageCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category = selectImageCategory.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        selectImageForGallery.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        fabGallery.setOnClickListener {
            if (addNewCategory.text.trim().isEmpty() && category == "Select Category") {
                addCategoryBool = !addCategoryBool
                if (addCategoryBool) {
                    addNewCategory.visibility = View.VISIBLE
                    addNewCategory.requestFocus()
                    selectImageCategory.visibility = View.GONE
                } else {
                    addNewCategory.visibility = View.GONE
                    selectImageCategory.visibility = View.VISIBLE
                }
            }
        }

        uploadGalleryImageButton.setOnClickListener {
            when {
                category == "Select Category" && addNewCategory.text.trim().isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Please Select Category or Add New Category",
                        Toast.LENGTH_LONG
                    ).show()
                }
                category != "Select Category" && addNewCategory.text.trim().isNotEmpty() -> {
                    Toast.makeText(
                        this,
                        "You can't select both select and add category option at same time",
                        Toast.LENGTH_LONG
                    ).show()
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


    private fun inflateSpinner() {

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading.....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if (ConnectionManager().checkConnectivity(this)) {
            try {
                databaseRef = databaseRef.ref.child("Gallery")
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            selectImageCategory.visibility = View.GONE
                            addNewCategory.visibility = View.VISIBLE
                            val items = mutableListOf("Select Category")
                            val immutableList = Collections.unmodifiableList(items)
                            selectImageCategory.adapter = ArrayAdapter(
                                this@UploadGalleryImages,
                                android.R.layout.simple_spinner_dropdown_item,
                                immutableList.toList()
                            )
                            progressDialog.dismiss()
                        } else {
                            val items = mutableListOf("Select Category")
                            for (data in dataSnapshot.children) {
                                val category = data.key
                                items.add(category!!)
                            }
                            val immutableList = Collections.unmodifiableList(items)
                            selectImageCategory.adapter = ArrayAdapter(
                                this@UploadGalleryImages,
                                android.R.layout.simple_spinner_dropdown_item,
                                immutableList.toList()
                            )
                            progressDialog.dismiss()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@UploadGalleryImages,
                            "${databaseError.toException()}\nSomething went wrong : Notice Deletion Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.w(
                            "Database Get Faculty",
                            "loadFaculty:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    this@UploadGalleryImages,
                    "$e\nSomething went wrong : Notice Deletion Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            progressDialog.dismiss()
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }

    }

    private fun uploadImage() {
        firebaseStorageManager = FirebaseStorageManager()
        val addNewCatText: String?
        addNewCatText = addNewCategory.text.toString().trim()

        if (addNewCatText.isNotEmpty() && category == "Select Category") {
            firebaseStorageManager.uploadImageToFirebaseStorage(
                this,
                imageUri!!,
                "Gallery",
                addNewCategory,
                galleryImageView,
                addNewCatText,
                selectImageCategory
            )
        } else if (addNewCatText.isEmpty() && category != "Select Category") {
            firebaseStorageManager.uploadImageToFirebaseStorage(
                this,
                imageUri!!,
                "Gallery",
                addNewCategory,
                galleryImageView,
                category,
                selectImageCategory
            )
        } else {
            Toast.makeText(
                this,
                "You can't select both select and add category option at same time",
                Toast.LENGTH_LONG
            ).show()
        }

        imageUri = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                imageUri = data?.data!!

                // Use Uri object instead of File to avoid storage permissions
                galleryImageView.setImageURI(imageUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findViews() {
        selectImageForGallery = findViewById(R.id.selectImageForGallery)
        addNewCategory = findViewById(R.id.addNewCategory)
        selectImageCategory = findViewById(R.id.selectImageCategory)
        uploadGalleryImageButton = findViewById(R.id.uploadGalleryImageButton)
        galleryImageView = findViewById(R.id.galleryImageView)
        fabGallery = findViewById(R.id.fabGallery)
        addNewCategory.visibility = View.GONE
    }
}