package com.nitj.nitjadminapp.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.adapters.DeleteNoticeAdapter
import com.nitj.nitjadminapp.adapters.FacultyAdapter
import com.nitj.nitjadminapp.models.FacultyData
import com.nitj.nitjadminapp.models.NoticeData
import com.nitj.nitjadminapp.util.ConnectionManager

class DeleteNotice : AppCompatActivity() {

    private lateinit var noData: CardView
    private lateinit var noDataText: TextView
    lateinit var deleteNoticeRecycler: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: DeleteNoticeAdapter
    private var noticeList = arrayListOf<NoticeData>()
    private lateinit var progressBar: ProgressBar
    private var databaseRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_notice)
        findView()
        getNotice()
    }

    @SuppressLint("SetTextI18n")
    private fun getNotice() {
        if (ConnectionManager().checkConnectivity(this)) {
            try {
                databaseRef = databaseRef.ref.child("Notice")
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            deleteNoticeRecycler.visibility = View.GONE
                            noData.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        } else {
                            noticeList.clear()
                            for (data in dataSnapshot.children) {
                                val noticeData = data.getValue(NoticeData::class.java)
                                noticeList.add(noticeData!!)
                            }
                            deleteNoticeRecycler.visibility = View.VISIBLE
                            noData.visibility = View.GONE
                            layoutManager = LinearLayoutManager(this@DeleteNotice)
                            recyclerAdapter = DeleteNoticeAdapter(this@DeleteNotice, noticeList)
                            deleteNoticeRecycler.layoutManager = layoutManager
                            deleteNoticeRecycler.adapter = recyclerAdapter
                            progressBar.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        deleteNoticeRecycler.visibility = View.GONE
                        noData.visibility = View.VISIBLE
                        noDataText.text = databaseError.toException().toString()
                        progressBar.visibility = View.GONE
                        Log.w(
                            "Database Get Notice",
                            "Load Notice:onCancelled",
                            databaseError.toException()
                        )
                    }
                })

            } catch (e: Exception) {
                deleteNoticeRecycler.visibility = View.GONE
                noData.visibility = View.VISIBLE
                noDataText.text = e.toString()
                progressBar.visibility = View.GONE
                Log.w(
                    "Something Went Wrong!!",
                    "Load Notice :onCancelled",
                    e
                )
            }
        } else {
            deleteNoticeRecycler.visibility = View.GONE
            noData.visibility = View.VISIBLE
            noDataText.text = "Internet Connection Not Found"
            progressBar.visibility = View.GONE
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
        deleteNoticeRecycler = findViewById(R.id.deleteNoticeRecycler)
        noData = findViewById(R.id.noData)
        noDataText = findViewById(R.id.noDataText)
        progressBar = findViewById(R.id.progressBar)
        title = "Delete Notice"
    }
}