package com.nitj.nitjadminapp.util

import android.app.AlertDialog
import android.content.Context
import com.nitj.nitjadminapp.R

class CustomDialog {
    fun getDialog(
        context: Context, title: String, msg: String, positiveFun:() -> Unit,
        negativeFun:()->Unit,
        posButtonName:String,
        negButtonName:String) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setIcon(R.drawable.ic_alert)
        dialog.setMessage(msg)
        dialog.setPositiveButton(posButtonName) { text, listener ->
            positiveFun()
        }
        dialog.setNegativeButton(negButtonName) { text, listener ->
            negativeFun()
        }
        dialog.create()
        dialog.show()
    }
}
