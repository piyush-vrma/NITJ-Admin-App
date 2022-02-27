package com.nitj.nitjadminapp.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.adapters.AllBranchAdapter
import com.nitj.nitjadminapp.adapters.FacultyAdapter
import com.nitj.nitjadminapp.models.FacultyData
import com.nitj.nitjadminapp.util.ConnectionManager

class Faculty : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var noFacultyData: CardView
    private lateinit var noFacultyDataText: TextView
    private var departmentName: String = ""
    lateinit var facultyRecycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: FacultyAdapter
    private var facultyList = arrayListOf<FacultyData>()
    private lateinit var progressDialog: ProgressDialog
    private var databaseRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty)
        findView()
        getFaculty()

        fab.setOnClickListener {
            val addUpdateTeacherIntent = Intent(this, AddUpdateFaculty::class.java)
            addUpdateTeacherIntent.putExtra("Mode", "Add")
            addUpdateTeacherIntent.putExtra("departmentName", departmentName)
            startActivity(addUpdateTeacherIntent)
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun getFaculty() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading.....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if (ConnectionManager().checkConnectivity(this)) {

            try {
                databaseRef = databaseRef.ref.child("Faculty").child(departmentName)
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            facultyRecycler.visibility = View.GONE
                            noFacultyData.visibility = View.VISIBLE
                            progressDialog.dismiss()
                        } else {
                            facultyList.clear()
                            for (data in dataSnapshot.children) {
                                val facultyData = data.getValue(FacultyData::class.java)
                                facultyList.add(facultyData!!)
                            }
                            facultyRecycler.visibility = View.VISIBLE
                            noFacultyData.visibility = View.GONE
                            layoutManager = LinearLayoutManager(this@Faculty)
                            recyclerAdapter = FacultyAdapter(this@Faculty, facultyList)
                            facultyRecycler.layoutManager = layoutManager
                            facultyRecycler.adapter = recyclerAdapter
                            progressDialog.dismiss()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        progressDialog.dismiss()
                        facultyRecycler.visibility = View.GONE
                        noFacultyData.visibility = View.VISIBLE
                        noFacultyDataText.text = databaseError.toException().toString()
                        Log.w(
                            "Database Get Faculty",
                            "loadFaculty:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                progressDialog.dismiss()
                facultyRecycler.visibility = View.GONE
                noFacultyData.visibility = View.VISIBLE
                noFacultyDataText.text = e.toString()
                Log.w(
                    "Database Get Faculty",
                    "loadFaculty:onCancelled",
                    e
                )
            }
        } else {
            progressDialog.dismiss()
            facultyRecycler.visibility = View.GONE
            noFacultyData.visibility = View.VISIBLE
            noFacultyDataText.text = "Internet Connection Not Found"
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

    private fun findView() {
        departmentName = intent?.getStringExtra("departmentName").toString().trim()
        fab = findViewById(R.id.fab)
        facultyRecycler = findViewById(R.id.facultyRecycler)
        noFacultyData = findViewById(R.id.noFacultyData)
        noFacultyDataText = findViewById(R.id.noFacultyDataText)
        title = "Faculty $departmentName"
    }
}