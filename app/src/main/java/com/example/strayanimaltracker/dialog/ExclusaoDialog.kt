package com.example.strayanimaltracker.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.strayanimaltracker.R

class ExclusaoDialog : DialogFragment() {

    private var listener: OnExclusaoSetListener? = null

    interface OnExclusaoSetListener {
        fun excluirPost()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Deseja apagar ?")
        builder.setPositiveButton("Sim") { dialog, which ->
            if (listener != null) {
                listener!!.excluirPost()
            }

        }
        builder.setNegativeButton("Não") { dialog, which ->

        }

        builder.setView(view)
        return builder.create()
    }

    companion object {
        fun show(fm: FragmentManager, listener: OnExclusaoSetListener) {

            val dialog = ExclusaoDialog()
            dialog.listener = listener
            dialog.show(fm, "ExclusaoDialog")
        }
    }
}
