package com.nitj.nitjadminapp.screens

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseManager.FirebaseDatabaseManager
import com.nitj.nitjadminapp.firebaseManager.FirebaseStorageManager
import com.nitj.nitjadminapp.models.FacultyData
import com.nitj.nitjadminapp.util.CustomDialog
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AddUpdateFaculty : AppCompatActivity() {
    private lateinit var teacherProfileImage: CircleImageView
    private lateinit var addTeacherName: EditText
    private lateinit var addTeacherDesignation: EditText
    private lateinit var addTeacherDepartment: EditText
    private lateinit var addTeacherQualification1: EditText
    private lateinit var addTeacherQualification2: EditText
    private lateinit var addTeacherQualification3: EditText
    private lateinit var addTeacherEmail: EditText
    private lateinit var addTeacherResearchInterests: EditText
    private lateinit var updateDeleteButtonLayout: LinearLayout
    private lateinit var addTeacherFax: EditText
    private lateinit var btnAddTeacher: Button
    private lateinit var btnUpdateTeacher: Button
    private lateinit var btnDeleteTeacher: Button
    private lateinit var facultyData: FacultyData
    private var imageUri: Uri? = null
    private lateinit var firebaseStorageManager: FirebaseStorageManager
    private lateinit var mode: String
    private var facImageLink: String? = null
    private lateinit var facKey: String
    private lateinit var progressDialog: ProgressDialog
    private lateinit var name: String
    private lateinit var designation: String
    private lateinit var department: String
    private lateinit var qualification1: String
    private lateinit var qualification2: String
    private lateinit var qualification3: String
    private lateinit var emailId: String
    private lateinit var fax: String
    private lateinit var researchInterests: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_faculty)
        findView()

        teacherProfileImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        btnAddTeacher.setOnClickListener {
            when {
                validate() -> {
                    addData()
                }
                imageUri == null -> {
                    Toast.makeText(this, "Please Select Image to Upload", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Please check the Input Fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnUpdateTeacher.setOnClickListener {
            when {
                validate() -> {
                    updateData()
                }
                imageUri == null && facImageLink == null -> {
                    Toast.makeText(this, "Please Select Image to Upload", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Please check the Input Fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        btnDeleteTeacher.setOnClickListener {
            val title = "Delete Faculty"
            val msg = "Are you sure you want to delete this faculty from the DATABASE?"
            val posText = "YES"
            val negText = "NO"
            CustomDialog().getDialog(
                this,
                title,
                msg,
                ::deleteFaculty,
                {},
                posText,
                negText
            )
        }
    }

    private fun deleteFaculty() {
        initiateFaculty()
        facultyData.profileImage = facImageLink!!.trim()
        facultyData.key = facKey.trim()
        firebaseStorageManager = FirebaseStorageManager()
        firebaseStorageManager.deleteFacultyDataFromFireBaseStorage(
            this,
            facultyData
        )
    }

    private fun updateData() {
        facultyData.key = facKey
        if (imageUri != null) {
            addData()
        } else {
            facultyData.profileImage = facImageLink!!
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("Updating.....")
            progressDialog.setCancelable(false)
            progressDialog.show()
            val firebaseDatabaseManager = FirebaseDatabaseManager()
            firebaseDatabaseManager.updateFacultyData(
                this,
                progressDialog,
                facultyData
            )
        }

    }

    private fun addData() {
        firebaseStorageManager = FirebaseStorageManager()
        firebaseStorageManager.uploadFacultyDataToFireBaseStorage(
            this,
            imageUri!!,
            facultyData,
            mode
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
                teacherProfileImage.setImageURI(imageUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validate(): Boolean {

        initiateFaculty()

        var isValidate = true
        if (name.isEmpty()) {
            addTeacherName.error = "Please enter Name"
            isValidate = false
        }
        if (designation.isEmpty()) {
            addTeacherDesignation.error = "Please enter Designation"
            isValidate = false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            addTeacherEmail.error = "Please enter a valid Email Address"
            isValidate = false
        }
        if (qualification1.isEmpty()) {
            addTeacherQualification1.error =
                "Please enter Qualification\ne.g : PhD Branch College\nor Write NA if not applicable"
            isValidate = false
        }
        if (qualification2.isEmpty()) {
            addTeacherQualification2.error =
                "Please enter Qualification\ne.g : Master's Branch College\nor Write NA if not applicable"
            isValidate = false
        }
        if (qualification3.isEmpty()) {
            addTeacherQualification3.error =
                "Please enter Qualification\ne.g : Bachelor's Branch College\nor Write NA if not applicable"
            isValidate = false
        }

        if (imageUri == null && facImageLink == null) {
            isValidate = false
        }


        return isValidate
    }

    private fun initiateFaculty() {
        name = addTeacherName.text.toString().trim()
        designation = addTeacherDesignation.text.toString().trim()
        department = addTeacherDepartment.text.toString().trim()
        qualification1 = addTeacherQualification1.text.toString().trim()
        qualification2 = addTeacherQualification2.text.toString().trim()
        qualification3 = addTeacherQualification3.text.toString().trim()
        emailId = addTeacherEmail.text.toString().trim()
        researchInterests = addTeacherResearchInterests.text.toString().trim()
        fax = addTeacherFax.text.toString().trim()

        facultyData = FacultyData(
            name = name,
            designation = designation,
            department = department,
            qualification1 = qualification1,
            qualification2 = qualification2,
            qualification3 = qualification3,
            emailId = emailId,
            researchInterests = researchInterests,
            fax = fax
        )
    }

    private fun findView() {
        teacherProfileImage = findViewById(R.id.teacherProfileImage)
        addTeacherName = findViewById(R.id.addTeacherName)
        addTeacherDesignation = findViewById(R.id.addTeacherDesignation)
        addTeacherDepartment = findViewById(R.id.addTeacherDepartment)
        addTeacherQualification1 = findViewById(R.id.addTeacherQualification1)
        addTeacherQualification2 = findViewById(R.id.addTeacherQualification2)
        addTeacherQualification3 = findViewById(R.id.addTeacherQualification3)
        addTeacherEmail = findViewById(R.id.addTeacherEmail)
        addTeacherResearchInterests = findViewById(R.id.addTeacherResearchInterests)
        addTeacherFax = findViewById(R.id.addTeacherFax)
        btnAddTeacher = findViewById(R.id.btnAddTeacher)
        btnUpdateTeacher = findViewById(R.id.btnUpdateTeacher)
        btnDeleteTeacher = findViewById(R.id.btnDeleteTeacher)
        updateDeleteButtonLayout = findViewById(R.id.updateDeleteButtonLayout)


        // Setting the department because it is not editable
        val departmentName = intent?.getStringExtra("departmentName").toString().trim()
        addTeacherDepartment.setText(departmentName)
        addTeacherDepartment.isEnabled = false

        // Getting the mode value
        mode = intent?.getStringExtra("Mode").toString().trim()

        if (mode == "Add") {

            title = "Add New Faculty"
            btnAddTeacher.visibility = View.VISIBLE
            updateDeleteButtonLayout.visibility = View.GONE

        } else if (mode == "Update") {

            title = "Update Faculty"
            updateDeleteButtonLayout.visibility = View.VISIBLE
            btnAddTeacher.visibility = View.GONE

            val facName = intent?.getStringExtra("facName").toString().trim()
            val facDesignation = intent?.getStringExtra("facDesignation").toString().trim()
            val facQ1 = intent?.getStringExtra("facQ1").toString().trim()
            val facQ2 = intent?.getStringExtra("facQ2").toString().trim()
            val facQ3 = intent?.getStringExtra("facQ3").toString().trim()
            val facEmail = intent?.getStringExtra("facEmail").toString().trim()
            val facResearch = intent?.getStringExtra("facResearch").toString().trim()
            val facFax = intent?.getStringExtra("facFax").toString().trim()
            facImageLink = intent?.getStringExtra("facImage").toString().trim()
            facKey = intent?.getStringExtra("facKey").toString().trim()

            addTeacherName.setText(facName)
            addTeacherDesignation.setText(facDesignation)
            addTeacherQualification1.setText(facQ1)
            addTeacherQualification2.setText(facQ2)
            addTeacherQualification3.setText(facQ3)
            addTeacherEmail.setText(facEmail)
            addTeacherResearchInterests.setText(facResearch)
            addTeacherFax.setText(facFax)
            Picasso.get().load(facImageLink).error(R.drawable.logo)
                .into(teacherProfileImage)
        }

    }
}