package com.nitj.nitjadminapp.firebaseManager

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.app.ActivityCompat
import com.google.firebase.storage.FirebaseStorage
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.models.FacultyData
import com.nitj.nitjadminapp.models.NoticeData
import com.nitj.nitjadminapp.util.ConnectionManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FirebaseStorageManager {
    private val TAG = "FirebaseStorageManager"
    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var progressDialog: ProgressDialog
    private var downloadUrl: String = "";
    private lateinit var firebaseDatabaseManager: FirebaseDatabaseManager
    private lateinit var completeFilePathName: String;
    private lateinit var fileName: String
    private lateinit var noticeData: NoticeData

    fun uploadImageToFirebaseStorage(
        context: Context,
        imageUri: Uri,
        storageFolderName: String,
        textField: EditText? = null,
        imageView: ImageFilterView? = null,
        storageSubFolderName: String? = null,
        spinner: Spinner? = null,
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Uploading.....")
        progressDialog.setCancelable(false)
        progressDialog.show()
        firebaseDatabaseManager = FirebaseDatabaseManager()

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                noticeData = NoticeData("", "", "", "", "")
                if (storageFolderName == "Notice") {
                    fileName = textField!!.text.toString().trim()
                    noticeData.title = fileName
                    noticeData.date = formatDate()
                    noticeData.time = formatTime()
                    completeFilePathName =
                        "$storageFolderName/$fileName ${noticeData.date} ${noticeData.time}"
                }
                if (storageFolderName == "Gallery") {
                    completeFilePathName =
                        "$storageFolderName/$storageSubFolderName/${LocalDateTime.now()}"
                }

                val uploadTask = storageRef.child(completeFilePathName).putFile(imageUri)

                uploadTask.addOnSuccessListener {
                    Log.e(TAG, "Image Upload Success")
                    val downloadURLTask = storageRef.child(completeFilePathName).downloadUrl
                    downloadURLTask.addOnSuccessListener {

                        Log.e(TAG, "IMAGE PATH : $it")
                        downloadUrl = it.toString().trim()

                        when (storageFolderName) {
                            "Notice" -> {
                                noticeData.image = downloadUrl
                                firebaseDatabaseManager.uploadNoticeData(
                                    context,
                                    progressDialog,
                                    noticeData,
                                    textField!!,
                                    imageView!!
                                )
                            }
                            "Gallery" -> {
                                firebaseDatabaseManager.uploadGalleryData(
                                    context,
                                    progressDialog,
                                    downloadUrl,
                                    storageSubFolderName!!,
                                    spinner!!,
                                    imageView!!,
                                    textField!!
                                )
                            }
                            else -> {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    context,
                                    "Image Uploaded Successfully $downloadUrl",
                                    Toast.LENGTH_SHORT
                                ).show()
                                print(downloadUrl)
                            }
                        }

                    }.addOnFailureListener {
                        if (storageFolderName == "Notice") {
                            clearFields(textField, imageView, null, null, null)
                        } else if (storageFolderName == "Gallery") {
                            clearFields(textField, imageView, spinner, null, null)
                        }
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
                            "${it.printStackTrace()} : ImageURL DOWNLOAD FAILED",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }.addOnFailureListener {
                    if (storageFolderName == "Notice") {
                        clearFields(textField, imageView, null, null, null)
                    } else if (storageFolderName == "Gallery") {
                        clearFields(textField, imageView, spinner, null, null)
                    }
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "${it.printStackTrace()} Image upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "Image upload Failed ${it.printStackTrace()}")

                }
            } catch (e: Exception) {
                if (storageFolderName == "Notice") {
                    clearFields(textField, imageView, null, null, null)
                } else if (storageFolderName == "Gallery") {
                    clearFields(textField, imageView, spinner, null, null)
                }
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e Image upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "Image upload Failed ${e.printStackTrace()}")
            }
        } else {
            if (storageFolderName == "Notice") {
                clearFields(textField, imageView, null, null, null)
            } else if (storageFolderName == "Gallery") {
                clearFields(textField, imageView, spinner, null, null)
            }
            progressDialog.dismiss()
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(settingIntent)
                (context as Activity).finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    fun uploadEbookToFireBaseStorage(
        context: Context,
        fileUri: Uri,
        department: String,
        spinner: Spinner,
        ebookTitleEditText: EditText,
        pickedFileNameTextView: TextView
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Uploading.....")
        progressDialog.setCancelable(false)
        progressDialog.show()
        firebaseDatabaseManager = FirebaseDatabaseManager()

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                fileName = pickedFileNameTextView.text.toString().trim()
                val ebookTitle = ebookTitleEditText.text.toString().trim()
                completeFilePathName =
                    "Ebook/$department/$ebookTitle ${LocalDateTime.now()} $fileName"

                val uploadTask = storageRef.child(completeFilePathName).putFile(fileUri)

                uploadTask.addOnSuccessListener {
                    Log.e(TAG, "file Upload Success")
                    val downloadURLTask = storageRef.child(completeFilePathName).downloadUrl
                    downloadURLTask.addOnSuccessListener {

                        Log.e(TAG, "File Path Url : $it")
                        downloadUrl = it.toString().trim()
                        print(downloadUrl)
                        firebaseDatabaseManager.uploadEbookData(
                            context,
                            progressDialog,
                            department,
                            spinner,
                            ebookTitleEditText,
                            pickedFileNameTextView,
                            downloadUrl
                        )
                    }.addOnFailureListener {
                        clearFields(
                            ebookTitleEditText,
                            null,
                            spinner,
                            pickedFileNameTextView,
                            context
                        )
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
                            "${it.printStackTrace()} : File Url DOWNLOAD FAILED",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }.addOnFailureListener {
                    clearFields(ebookTitleEditText, null, spinner, pickedFileNameTextView, context)
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "${it.printStackTrace()} File upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "File upload Failed ${it.printStackTrace()}")
                }
            } catch (e: Exception) {
                clearFields(ebookTitleEditText, null, spinner, pickedFileNameTextView, context)
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "${e.printStackTrace()} File upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "File upload Failed ${e.printStackTrace()}")
            }
        } else {
            clearFields(ebookTitleEditText, null, spinner, pickedFileNameTextView, context)
            progressDialog.dismiss()
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(settingIntent)
                (context as Activity).finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
        }

    }

    fun uploadFacultyDataToFireBaseStorage(
        context: Context,
        fileUri: Uri,
        facultyData: FacultyData,
        mode: String
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        val pdMsg: String = if (mode == "Update") {
            "Updating....."
        } else {
            "Uploading....."
        }
        progressDialog.setMessage(pdMsg)
        progressDialog.setCancelable(false)
        progressDialog.show()
        firebaseDatabaseManager = FirebaseDatabaseManager()

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                fileName = facultyData.emailId.trim().lowercase()
                val department = facultyData.department.trim()
                completeFilePathName = "Faculty/$department/$fileName"

                val uploadTask = storageRef.child(completeFilePathName).putFile(fileUri)

                uploadTask.addOnSuccessListener {
                    Log.e(TAG, "Image Upload Success")
                    val downloadURLTask = storageRef.child(completeFilePathName).downloadUrl
                    downloadURLTask.addOnSuccessListener {

                        Log.e(TAG, "Image Path Url : $it")
                        downloadUrl = it.toString().trim()
                        print(downloadUrl)
                        facultyData.profileImage = downloadUrl

                        if (mode == "Update") {
                            firebaseDatabaseManager.updateFacultyData(
                                context,
                                progressDialog,
                                facultyData
                            )
                        } else {
                            firebaseDatabaseManager.uploadFacultyData(
                                context,
                                progressDialog,
                                facultyData
                            )
                        }
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
                            "${it.printStackTrace()} : Image Url DOWNLOAD FAILED",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "${it.printStackTrace()} Image upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "Image upload Failed ${it.printStackTrace()}")
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "${e.printStackTrace()} Image upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "Image upload Failed ${e.printStackTrace()}")
            }
        } else {
            progressDialog.dismiss()
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(settingIntent)
                (context as Activity).finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
        }

    }

    fun deleteFacultyDataFromFireBaseStorage(
        context: Context,
        facultyData: FacultyData,
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Deleting.....")
        progressDialog.setCancelable(false)
        progressDialog.show()
        firebaseDatabaseManager = FirebaseDatabaseManager()

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                fileName = facultyData.emailId.trim().lowercase()
                val department = facultyData.department.trim()
                completeFilePathName = "Faculty/$department/$fileName"

                val deleteTask = storageRef.child(completeFilePathName).delete()

                deleteTask.addOnSuccessListener {
                    Log.e(TAG, "Image Deleted Successfully")
                    firebaseDatabaseManager.deleteFacultyData(
                        context,
                        progressDialog,
                        facultyData
                    )

                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "${it.printStackTrace()} Image Deletion Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "Image Deletion Failed ${it.printStackTrace()}")
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "${e.printStackTrace()} Image Deletion Failed",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "Image Deletion Failed ${e.printStackTrace()}")
            }
        } else {
            progressDialog.dismiss()
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(settingIntent)
                (context as Activity).finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    fun deleteNoticeDataFromFireBaseStorage(
        context: Context,
        noticeData: NoticeData,
    ) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Deleting.....")
        progressDialog.setCancelable(false)
        progressDialog.show()
        firebaseDatabaseManager = FirebaseDatabaseManager()

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                fileName = noticeData.title.trim()
                completeFilePathName = "Notice/$fileName ${noticeData.date} ${noticeData.time}"

                val deleteTask = storageRef.child(completeFilePathName).delete()

                deleteTask.addOnSuccessListener {
                    Log.e(TAG, "Image Deleted Successfully")
                    firebaseDatabaseManager.deleteNoticeData(
                        context,
                        progressDialog,
                        noticeData
                    )

                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "${it.printStackTrace()} Image Deletion Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "Image Deletion Failed ${it.printStackTrace()}")
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "${e.printStackTrace()} Image Deletion Failed",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "Image Deletion Failed ${e.printStackTrace()}")
            }
        } else {
            progressDialog.dismiss()
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(settingIntent)
                (context as Activity).finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun formatDate(): String {
        val dateTime = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formattedDate = dateTime.format(dateFormatter)
        return formattedDate.toString().trim()
    }

    private fun formatTime(): String {
        val dateTime = LocalDateTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("h:m a")
        val formattedTime = dateTime.format(timeFormatter)
        return formattedTime.toString().trim()
    }

    private fun clearFields(
        textField: EditText? = null,
        imageView: ImageFilterView? = null,
        spinner: Spinner? = null,
        textView: TextView? = null,
        context: Context? = null
    ) {
        textField?.text?.clear()
        imageView?.setImageResource(0)
        spinner?.setSelection(0)
        textView?.text = context?.getString(R.string.no_book_pdf_selected)
    }
}