package com.nitj.nitjadminapp.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.models.FacultyData
import com.nitj.nitjadminapp.screens.AddUpdateFaculty
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception


class FacultyAdapter(
    private val context: Context,
    private val listItem: ArrayList<FacultyData>
) : RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tile_faculty, parent, false)
        return FacultyViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        val facultyData = listItem[position]

        holder.txtFacultyName.text = facultyData.name
        holder.txtDesignation.text = facultyData.designation
        holder.txtFacultyEmailId.text = facultyData.emailId

        val str = SpannableString(holder.researchTitle.text)
        str.setSpan(UnderlineSpan(), 0, holder.researchTitle.text.length, 0)

        holder.researchTitle.text = str
        holder.txtFacultyResearchInterest.text = facultyData.researchInterests

        try {
            Picasso.get().load(facultyData.profileImage).error(R.drawable.logo)
                .into(holder.imgFacultyProfileImage)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }


        holder.facultyTile.setOnClickListener {
            val intent = Intent(context, AddUpdateFaculty::class.java)
            intent.putExtra("Mode", "Update")
            intent.putExtra("facName", facultyData.name)
            intent.putExtra("facDesignation", facultyData.designation)
            intent.putExtra("departmentName", facultyData.department)
            intent.putExtra("facQ1", facultyData.qualification1)
            intent.putExtra("facQ2", facultyData.qualification2)
            intent.putExtra("facQ3", facultyData.qualification3)
            intent.putExtra("facEmail", facultyData.emailId)
            intent.putExtra("facResearch", facultyData.researchInterests)
            intent.putExtra("facFax", facultyData.fax)
            intent.putExtra("facImage", facultyData.profileImage)
            intent.putExtra("facKey", facultyData.key)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class FacultyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFacultyProfileImage: CircleImageView = view.findViewById(R.id.imgFacultyProfileImage)
        val txtFacultyName: TextView = view.findViewById(R.id.txtFacultyName)
        val txtDesignation: TextView = view.findViewById(R.id.txtDesignation)
        val txtFacultyEmailId: TextView = view.findViewById(R.id.txtFacultyEmailId)
        val txtFacultyResearchInterest: TextView =
            view.findViewById(R.id.txtFacultyResearchInterest)
        val researchTitle: TextView = view.findViewById(R.id.researchTitle)
        val facultyTile: CardView = view.findViewById(R.id.facultyTile)
    }
}