package com.nitj.nitjadminapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.firebaseManager.FirebaseStorageManager
import com.nitj.nitjadminapp.models.NoticeData
import com.squareup.picasso.Picasso

class DeleteNoticeAdapter(
    private val context: Context,
    private val listItem: ArrayList<NoticeData>
) : RecyclerView.Adapter<DeleteNoticeAdapter.DeleteNoticeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteNoticeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tile_delete_notice, parent, false)
        return DeleteNoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeleteNoticeViewHolder, position: Int) {
        val noticeData = listItem[position]
        holder.txtDelNoticeMsg.text = noticeData.title

        try {
            Picasso.get().load(noticeData.image).error(R.drawable.logo)
                .into(holder.imgDelNoticeImage)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }

        holder.btnDelButton.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Delete Notice")
            dialog.setIcon(R.drawable.ic_alert)
            dialog.setMessage("Are you sure you want to delete this notice?")
            dialog.setPositiveButton("Yes") { text, listener ->
                val firebaseStorageManager = FirebaseStorageManager()
                firebaseStorageManager.deleteNoticeDataFromFireBaseStorage(
                    context,
                    noticeData
                )
            }
            dialog.setNegativeButton("No") { text, listener ->

            }
            dialog.create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class DeleteNoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnDelButton: MaterialButton = view.findViewById(R.id.btnDelButton)
        val txtDelNoticeMsg: TextView = view.findViewById(R.id.txtDelNoticeMsg)
        val imgDelNoticeImage: ImageView = view.findViewById(R.id.imgDelNoticeImage)
    }
}