package com.nitj.nitjadminapp.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.adapters.AllBranchAdapter
import com.nitj.nitjadminapp.adapters.FacultyAdapter
import com.nitj.nitjadminapp.models.DepartmentData
import com.nitj.nitjadminapp.models.FacultyData
import com.nitj.nitjadminapp.util.ConnectionManager
import java.lang.Exception

class AllBranches : AppCompatActivity() {

    private lateinit var allBranchRecyclerView: RecyclerView
    private lateinit var noDepartmentData: CardView
    private lateinit var noDepartmentDataText: TextView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: AllBranchAdapter
    private var departmentList = arrayListOf<DepartmentData>()
    private var departmentName = arrayListOf<String>()
    private var departmentImage = arrayListOf<String>()
    private var departmentHodMail = arrayListOf<String>()
    private var departmentOfficeMail = arrayListOf<String>()
    private var facultyCount = IntArray(19)
    private lateinit var progressDialog: ProgressDialog
    private var databaseRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_all_branches)
        makeList()
    }

    @SuppressLint("SetTextI18n")
    private fun getFacultyCount() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading.....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if (ConnectionManager().checkConnectivity(this)) {

            try {
                databaseRef = databaseRef.ref.child("Faculty")
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val children = dataSnapshot.children
                        if (dataSnapshot.exists()) {
                            children.forEach {
                                Log.e("TAG", it.childrenCount.toString())
                                Log.e("TAG", it.key.toString())
                                val branch = it.key.toString()
                                for (i in departmentName.indices) {
                                    if (branch == departmentName[i]) {
                                        facultyCount[i] = it.childrenCount.toInt()
                                    }
                                }
                            }
                        } else {
                            progressDialog.dismiss()
                            Log.e("TAG", children.toString())
                        }

                        try {
                            inflateAdapter()
                        } catch (e: Exception) {
                            progressDialog.dismiss()
                            Toast.makeText(this@AllBranches, e.toString(), Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@AllBranches,
                            databaseError.toException().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.w(
                            "Database Faculty Count",
                            "Count Faculty:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(this@AllBranches, e.toString(), Toast.LENGTH_LONG).show()
            }
        } else {
            allBranchRecyclerView.visibility = View.GONE
            noDepartmentData.visibility = View.VISIBLE
            noDepartmentDataText.text = "Internet Connection Not Found"
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


    private fun makeList() {
        allBranchRecyclerView = findViewById(R.id.allBranchRecycler)
        noDepartmentData = findViewById(R.id.noDepartmentData)
        noDepartmentDataText = findViewById(R.id.noDepartmentDataText)
        departmentName =
            arrayListOf(
                "Computer Science & Engineering",
                "Information Technology",
                "Electronics & Communication Engineering",
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

        departmentOfficeMail = arrayListOf(
            "ocs@nitj.ac.in",
            "oit@nitj.ac.in",
            "oec@nitj.ac.in",
            "oee@nitj.ac.in",
            "oic@nitj.ac.in",
            "och@nitj.ac.in",
            "ome@nitj.ac.in",
            "oie@nitj.ac.in",
            "otx@nitj.ac.in",
            "oce@nitj.ac.in",
            "obt@nitj.ac.in",
            "ohm@nitj.ac.in",
            "oph@nitj.ac.in",
            "ocy@nitj.ac.in",
            "oma@nitj.ac.in",
            "occe@nitj.ac.in",
            "ocee@nitj.ac.in",
            "ocai@nitj.ac.in"
        )

        departmentHodMail = arrayListOf(
            "hcs@nitj.ac.in",
            "hit@nitj.ac.in",
            "hec@nitj.ac.in",
            "hee@nitj.ac.in",
            "hic@nitj.ac.in",
            "hch@nitj.ac.in",
            "hme@nitj.ac.in",
            "hip@nitj.ac.in",
            "hodtextile@nitj.ac.in",
            "hce@nitj.ac.in",
            "hbt@nitj.ac.in",
            "hhm@nitj.ac.in",
            "hph@nitj.ac.in",
            "hcy@nitj.ac.in",
            "hma@nitj.ac.in",
            "hcce@nitj.ac.in",
            "hcee@nitj.ac.in",
            "hcai@nitj.ac.in"
        )

        departmentImage =
            arrayListOf(
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fcse.png?alt=media&token=a14fb043-a766-4ed6-ad49-e83ecdc931de",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fit.png?alt=media&token=c60fb2d5-ab3f-4f04-af3b-2992adb11715",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fece.png?alt=media&token=5d1003aa-747d-45b8-bedb-ca179b2e0eb0",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fee.png?alt=media&token=63fb7d5c-4125-4a3f-a46b-422e610395ad",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fice.png?alt=media&token=a5725c91-0792-45e6-bae5-3831ea6e35a0",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fche.png?alt=media&token=001079a4-d5ac-4ef8-befb-853cd2bf8ce6",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fmech.png?alt=media&token=4bd58108-743b-4cea-948e-076496e662a9",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fipe.png?alt=media&token=6dfddfca-0412-4a4e-a173-75b7d6455dd4",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Ftt.png?alt=media&token=abe902a1-2549-4ca9-83f9-3c60e5c5cd61",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2FCIVIL.png?alt=media&token=372afabd-2e8a-449a-a677-65136da9ae67",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fbt2.png?alt=media&token=4b573aa3-e7c6-468c-9f88-2769f63ccc4f",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fhumanities_and_management.png?alt=media&token=27617bb8-e9f0-4974-8d46-0e4f752ed31b",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fphysics.png?alt=media&token=4d6bca73-2b05-4326-882f-0e915164de2b",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fchemistry.png?alt=media&token=2bc0731e-fd44-4c65-87ad-fc39c66e26eb",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fmathematics.png?alt=media&token=9deacf22-fbd0-48b3-b8f0-de9586ca0722",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Feducation.png?alt=media&token=5f7c7e8a-8632-4b51-b5ee-e4a0e3aef1df",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fenvironment_and_energy.png?alt=media&token=71669758-865b-4558-a58a-d2d37c140d93",
                "https://firebasestorage.googleapis.com/v0/b/nitj-app.appspot.com/o/ImageAssets%2Fai.png?alt=media&token=8e832efc-1f13-44c9-8d63-9594185b94e1"
            )
        getFacultyCount()
    }

    fun inflateAdapter() {

        departmentList.clear()
        for (i in departmentImage.indices) {
            val deptData = DepartmentData(
                departmentName = departmentName[i],
                departmentImage = departmentImage[i],
                noOfFaculty = facultyCount[i].toString(),
                hodMail = departmentHodMail[i].trim(),
                officeMail = departmentOfficeMail[i].trim()
            )
            departmentList.add(deptData)
        }


        layoutManager = LinearLayoutManager(this)
        recyclerAdapter = AllBranchAdapter(this, departmentList)
        allBranchRecyclerView.layoutManager = layoutManager
        allBranchRecyclerView.adapter = recyclerAdapter
        progressDialog.dismiss()
    }

}