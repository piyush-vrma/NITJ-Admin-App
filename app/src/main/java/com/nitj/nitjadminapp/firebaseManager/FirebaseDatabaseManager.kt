package com.nitj.nitjadminapp.firebaseManager

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.app.ActivityCompat
import com.google.firebase.database.FirebaseDatabase
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseNotificationJava.Constant.TOPIC
import com.nitj.nitjadminapp.firebaseNotificationJava.NotificationData
import com.nitj.nitjadminapp.firebaseNotificationJava.NotificationFunctions
import com.nitj.nitjadminapp.firebaseNotificationJava.PushNotification
import com.nitj.nitjadminapp.models.CommonData
import com.nitj.nitjadminapp.models.FacultyData
import com.nitj.nitjadminapp.models.NoticeData
import com.nitj.nitjadminapp.util.ConnectionManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


//const val TOPIC = "/topic/myTopic"

class FirebaseDatabaseManager {

    private val TAG = "FirebaseDatabaseManager"
    private var databaseRef = FirebaseDatabase.getInstance().reference
    private lateinit var progressDialog: ProgressDialog

    // notification send function
//    private fun sendNotification(context:Context,notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
//        try{
//            val response = RetrofitInstance.api.postNotification(notification)
//            if(response.isSuccessful){
//                print("Successful ${Gson().toJson(response)}")
//                Log.d(TAG,"Positive Response:${Gson().toJson(response)}")
//            }else{
//                Log.e(TAG,response.errorBody().toString())
//            }
//        }catch (e:Exception){
//            Log.e(TAG,e.toString())
//            Toast.makeText(
//                context,
//                "$e\nSomething went wrong : Notification Failed",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

    fun uploadNoticeData(
        context: Context,
        progressDialog: ProgressDialog,
        noticeData: NoticeData,
        textField: EditText,
        imageView: ImageFilterView
    ) {

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                databaseRef = databaseRef.child("Notice")
                val uniqueKey = databaseRef.push().key.toString().trim()

                noticeData.key = uniqueKey

                databaseRef.child(uniqueKey).setValue(noticeData).addOnSuccessListener {
                    Log.e(TAG, "Notice Upload success")
                    Toast.makeText(context, "Notice Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                    clearFields(textField, imageView, null, null, null)

                    // Sending Notice Upload Notification

                    val notification = PushNotification(
                        NotificationData("New Notice Uploaded", noticeData.title, noticeData.image),
                        TOPIC
                    )

                    NotificationFunctions.sendNotification(notification, context)

                    // This code is for the kotlin sendNotification method
                    // Implement using retrofit in kotlin class But it did not worked therefore we are using the
                    // above java class

//                    PushNotification(NotificationData("New Notice Uploaded",noticeData.title), TOPIC).also {
//                        sendNotification(context,it)
//                    }
                }.addOnFailureListener {
                    clearFields(textField, imageView, null, null, null)
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "Something went wrong : Notice Upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                clearFields(textField, imageView, null, null, null)
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : Notice Upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            clearFields(textField, imageView, null, null, null)
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

    fun uploadSliderImage(
        context: Context,
        progressDialog: ProgressDialog,
        url: String,
        textField: EditText,
        imageView: ImageFilterView
    ) {

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                databaseRef = databaseRef.child("Slider Image")
                val uniqueKey = databaseRef.push().key.toString().trim()
                val data = CommonData(title = textField.text.toString().trim(), url = url.trim())

                databaseRef.child(uniqueKey).setValue(data).addOnSuccessListener {
                    Log.e(TAG, "Slider Image Upload success")
                    Toast.makeText(
                        context,
                        "Slider Image Uploaded Successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    progressDialog.dismiss()
                    clearFields(textField, imageView, null, null, null)

                }.addOnFailureListener {
                    clearFields(textField, imageView, null, null, null)
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "Something went wrong : Slider Image Upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                clearFields(textField, imageView, null, null, null)
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : Slider Image Upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            clearFields(textField, imageView, null, null, null)
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

    fun uploadCommonData(
        context: Context,
        textFieldTitle: EditText,
        textFieldUrl: EditText,
        folder: String,
        subFolder: String
    ) {

        if (ConnectionManager().checkConnectivity(context)) {

            progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("Uploading.....")
            progressDialog.setCancelable(false)
            progressDialog.show()

            try {
                val completeFilePathName = "$folder/$subFolder"
                val newRef = databaseRef.child(completeFilePathName)
                val uniqueKey = newRef.push().key.toString().trim()
                val data = CommonData(
                    title = textFieldTitle.text.toString().trim(),
                    url = textFieldUrl.text.toString().trim()
                )

                newRef.child(uniqueKey).setValue(data).addOnSuccessListener {
                    Log.e(TAG, "$subFolder Upload success")
                    Toast.makeText(context, "$subFolder Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                    clearFields(textFieldTitle, null, null, null, null)
                    clearFields(textFieldUrl, null, null, null, null)

                }.addOnFailureListener {
                    clearFields(textFieldTitle, null, null, null, null)
                    clearFields(textFieldUrl, null, null, null, null)
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "Something went wrong : $subFolder Upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                clearFields(textFieldTitle, null, null, null, null)
                clearFields(textFieldUrl, null, null, null, null)
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : $subFolder Upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            clearFields(textFieldTitle, null, null, null, null)
            clearFields(textFieldUrl, null, null, null, null)
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadGalleryData(
        context: Context,
        progressDialog: ProgressDialog,
        downloadURL: String,
        storageSubFolderName: String,
        spinner: Spinner,
        imageView: ImageFilterView,
        editText: EditText
    ) {

        if (ConnectionManager().checkConnectivity(context)) {
            try {
                databaseRef = databaseRef.child("Gallery").child(storageSubFolderName)
                val uniqueKey = databaseRef.push().key

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["imageUrl"] = downloadURL.trim()
                hashMap["date"] = formatDate()
                hashMap["time"] = formatTime()

                databaseRef.child(uniqueKey!!).setValue(hashMap).addOnSuccessListener {
                    Log.e(TAG, "Gallery Image Upload success")
                    Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                    clearFields(editText, imageView, spinner, null, null)
                    editText.visibility = View.GONE
                    spinner.visibility = View.VISIBLE
                }.addOnFailureListener {
                    clearFields(editText, imageView, spinner, null, null)
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "Something went wrong : Image Upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                clearFields(editText, imageView, spinner, null, null)
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : Image Upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            clearFields(editText, imageView, spinner, null, null)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadEbookData(
        context: Context,
        progressDialog: ProgressDialog,
        department: String,
        spinner: Spinner,
        ebookTitleEditText: EditText,
        pickedFileNameTextView: TextView,
        downloadURL: String,
    ) {

        if (ConnectionManager().checkConnectivity(context)) {
            try {
                databaseRef = databaseRef.child("Ebook").child(department)
                val uniqueKey = databaseRef.push().key

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["ebookTitle"] = ebookTitleEditText.text.toString().trim()
                hashMap["ebookUrl"] = downloadURL.trim()
                hashMap["pickedFileName"] = pickedFileNameTextView.text.toString().trim()
                hashMap["date"] = formatDate()
                hashMap["time"] = formatTime()

                databaseRef.child(uniqueKey!!).setValue(hashMap).addOnSuccessListener {
                    Log.e(TAG, "File Upload success")
                    Toast.makeText(context, "File Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    clearFields(ebookTitleEditText, null, spinner, pickedFileNameTextView, context)
                }.addOnFailureListener {
                    clearFields(ebookTitleEditText, null, spinner, pickedFileNameTextView, context)
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "Something went wrong : File Upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                clearFields(ebookTitleEditText, null, spinner, pickedFileNameTextView, context)
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : File Upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadFacultyData(
        context: Context,
        progressDialog: ProgressDialog,
        facultyData: FacultyData
    ) {

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                val department = facultyData.department.trim()

                databaseRef = databaseRef.child("Faculty").child(department)
                val uniqueKey = databaseRef.push().key.toString().trim()

                facultyData.key = uniqueKey
                facultyData.date = formatDate()
                facultyData.time = formatTime()

                databaseRef.child(uniqueKey).setValue(facultyData).addOnSuccessListener {
                    Log.e(TAG, "Faculty Upload success")
                    Toast.makeText(context, "Faculty Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                    (context as Activity).finish()
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "Something went wrong : Faculty Upload Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : Faculty Upload Failed",
                    Toast.LENGTH_SHORT
                ).show()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateFacultyData(
        context: Context,
        progressDialog: ProgressDialog,
        facultyData: FacultyData
    ) {

        if (ConnectionManager().checkConnectivity(context)) {

            try {
                val department = facultyData.department.trim()
                val uniqueKey = facultyData.key.trim()

                val hashMap: HashMap<String, String> = HashMap()
                hashMap["name"] = facultyData.name
                hashMap["designation"] = facultyData.designation
                hashMap["department"] = facultyData.department
                hashMap["qualification1"] = facultyData.qualification1
                hashMap["qualification2"] = facultyData.qualification2
                hashMap["qualification3"] = facultyData.qualification3
                hashMap["emailId"] = facultyData.emailId
                hashMap["researchInterests"] = facultyData.researchInterests
                hashMap["fax"] = facultyData.fax
                hashMap["profileImage"] = facultyData.profileImage
                hashMap["date"] = formatDate()
                hashMap["time"] = formatTime()


                databaseRef = databaseRef.child("Faculty").child(department)
                databaseRef.child(uniqueKey).updateChildren(hashMap as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.e(TAG, "Faculty Updated success")
                        Toast.makeText(context, "Faculty Updated Successfully", Toast.LENGTH_SHORT)
                            .show()
                        progressDialog.dismiss()
                        (context as Activity).finish()
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
                            "Something went wrong : Faculty Update Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : Faculty Update Failed",
                    Toast.LENGTH_SHORT
                ).show()
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


    fun deleteFacultyData(
        context: Context,
        progressDialog: ProgressDialog,
        facultyData: FacultyData
    ) {

        if (ConnectionManager().checkConnectivity(context)) {

            try {

                val department = facultyData.department.trim()
                val uniqueKey = facultyData.key.trim()

                databaseRef = databaseRef.child("Faculty").child(department)
                databaseRef.child(uniqueKey).removeValue()
                    .addOnSuccessListener {
                        Log.e(TAG, "Faculty Deletion success")
                        Toast.makeText(context, "Faculty Deleted Successfully", Toast.LENGTH_SHORT)
                            .show()
                        progressDialog.dismiss()
                        (context as Activity).finish()
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
                            "Something went wrong : Faculty Deletion Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : Faculty Deletion Failed",
                    Toast.LENGTH_SHORT
                ).show()
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

    fun deleteNoticeData(
        context: Context,
        progressDialog: ProgressDialog,
        noticeData: NoticeData
    ) {

        if (ConnectionManager().checkConnectivity(context)) {
            try {
                val uniqueKey = noticeData.key.trim()

                databaseRef = databaseRef.child("Notice")
                databaseRef.child(uniqueKey).removeValue()
                    .addOnSuccessListener {
                        Log.e(TAG, "Notice Deletion success")
                        Toast.makeText(context, "Notice Deleted Successfully", Toast.LENGTH_SHORT)
                            .show()
                        progressDialog.dismiss()
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
                            "Something went wrong : Notice Deletion Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    context,
                    "$e\nSomething went wrong : Notice Deletion Failed",
                    Toast.LENGTH_SHORT
                ).show()
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDate(): String {
        val dateTime = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formattedDate = dateTime.format(dateFormatter)
        return formattedDate.toString().trim()
    }

    @RequiresApi(Build.VERSION_CODES.O)
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