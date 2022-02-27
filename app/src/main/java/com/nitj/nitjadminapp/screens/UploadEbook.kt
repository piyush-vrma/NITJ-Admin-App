package com.nitj.nitjadminapp.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseManager.FirebaseStorageManager
import java.io.File


class UploadEbook : AppCompatActivity() {

    private lateinit var selectEbook: MaterialCardView
    private lateinit var ebookTitle: EditText
    private lateinit var selectDepartmentForEbookSpinner: Spinner
    private lateinit var uploadEbookButton: Button
    private lateinit var fileNameTextField: TextView

    private var fileUri: Uri? = null
    private var department: String = "Select Department"
    private lateinit var firebaseStorageManager: FirebaseStorageManager
    private val REQ: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_ebook)
        findViews()
        inflateSpinner()

        // pick file - pdf/docs/ppt
        pickFile()

        // selection of department in spinner
        selectDepartment()

        uploadEbookButton.setOnClickListener {
            when {
                ebookTitle.text.toString().trim().isEmpty() -> {
                    ebookTitle.error = "Please enter Ebook Title"
                    ebookTitle.requestFocus()
                }
                department == "Select Department" -> {
                    Toast.makeText(this, "Please Select Department", Toast.LENGTH_LONG).show()
                }
                fileUri == null -> {
                    Toast.makeText(this, "Please Select PDF/DOCS/PPT to Upload", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    uploadEbook()
                }
            }
        }

    }

    private fun uploadEbook() {
        firebaseStorageManager = FirebaseStorageManager()
        firebaseStorageManager.uploadEbookToFireBaseStorage(
            this,
            fileUri!!,
            department,
            selectDepartmentForEbookSpinner,
            ebookTitle,
            fileNameTextField
        )
        fileUri = null
    }

    private fun selectDepartment() {
        selectDepartmentForEbookSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    department = selectDepartmentForEbookSpinner.selectedItem.toString().trim()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

    }

    // Most important piece of code to pick files from android device
    private fun pickFile() {
        selectEbook.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            val mimetypes = arrayOf(
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.ms-powerpoint"
            )
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
            startActivityForResult(intent, REQ)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ && resultCode == RESULT_OK) {
            fileUri = data?.data!!
            Log.e("TAG", fileUri.toString())
            fileNameTextField.text = getFileName(fileUri!!)
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // This function is returning the file name that is selected from the gallery
    @SuppressLint("Range", "Recycle")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
            } finally {
                cursor!!.close()
            }
        } else if (fileUri.toString().startsWith("file://")) {
            result = File(fileUri.toString()).name
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun inflateSpinner() {
        val items: Array<String> =
            arrayOf(
                "Select Department",
                "Computer Science & Engineering",
                "Information Technology",
                "Electronics & Comm Engineering",
                "Electrical Engineering",
                "Instrumentation & Control Engineering",
                "Chemical Engineering",
                "Mechanical Engineering",
                "Industrial & Production Engineering",
                "Textile Technology",
                "Civil Engineering",
                "Bio Technology",
                "Humanities & Management",
                "Physics",
                "Chemistry",
                "Mathematics",
                "Centre for Continuing Education (CCE)",
                "Centre for Energy and Environment",
                "Centre for Artificial Intelligence"
            )
        selectDepartmentForEbookSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
    }

    private fun findViews() {
        selectEbook = findViewById(R.id.selectEbook)
        ebookTitle = findViewById(R.id.ebookTitle)
        selectDepartmentForEbookSpinner = findViewById(R.id.selectDepartmentForEbookSpinner)
        uploadEbookButton = findViewById(R.id.uploadEbookButton)
        fileNameTextField = findViewById(R.id.fileNameTextField)
    }
}
