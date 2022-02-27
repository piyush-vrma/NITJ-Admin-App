package com.nitj.nitjadminapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nitj.nitjadminapp.R
import com.nitj.nitjadminapp.models.DepartmentData
import com.nitj.nitjadminapp.screens.Faculty
import com.squareup.picasso.Picasso

class AllBranchAdapter(
    private val context: Context,
    private val listItem: ArrayList<DepartmentData>
) : RecyclerView.Adapter<AllBranchAdapter.AllBranchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllBranchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tile_allbranch, parent, false)
        return AllBranchViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: AllBranchViewHolder, position: Int) {
        val departmentData = listItem[position]
        holder.departmentName.text = departmentData.departmentName
        holder.totalFaculty.text = "Total Faculty : ${departmentData.noOfFaculty}"
        holder.hodMail.text = " : ${departmentData.hodMail}"
        holder.officeMail.text = " : ${departmentData.officeMail}"


        var str = SpannableString(holder.hod.text)
        str.setSpan(UnderlineSpan(), 0, holder.hod.text.length, 0)
        holder.hod.text = str

        str = SpannableString(holder.office.text)
        str.setSpan(UnderlineSpan(), 0, holder.office.text.length, 0)
        holder.office.text = str

        holder.hodMail.setOnClickListener {
            val arrayOfString = arrayOf(departmentData.hodMail.trim())
            openEmail(arrayOfString)
        }
        holder.officeMail.setOnClickListener {
            val arrayOfString = arrayOf(departmentData.officeMail.trim())
            openEmail(arrayOfString)
        }

        Picasso.get().load(departmentData.departmentImage).error(R.drawable.logo)
            .into(holder.departmentImage)

        holder.llContext.setOnClickListener {
            val intent = Intent(context, Faculty::class.java)
            intent.putExtra("departmentName", departmentData.departmentName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class AllBranchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val departmentImage: ImageView = view.findViewById(R.id.imgDepartmentLogo)
        val departmentName: TextView = view.findViewById(R.id.txtDepartmentName)
        val totalFaculty: TextView = view.findViewById(R.id.txtNoOfFaculty)
        val hod: TextView = view.findViewById(R.id.hod)
        val hodMail: TextView = view.findViewById(R.id.hodMail)
        val office: TextView = view.findViewById(R.id.office)
        val officeMail: TextView = view.findViewById(R.id.officeMail)
        val llContext: CardView = view.findViewById(R.id.llContent)
    }


    private fun openEmail(mail: Array<String>) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL,mail)
        }

        if (emailIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(emailIntent)
        } else {
            Toast.makeText(context, "Required App is not installed!!", Toast.LENGTH_LONG).show()
        }
    }


}