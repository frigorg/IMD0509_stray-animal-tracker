package com.example.strayanimaltracker.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.strayanimaltracker.R

class TrashDialog: DialogFragment() {

    private var listener: OnTarefaTrashSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Deseja apagar ?")
        builder.setPositiveButton("Sim") { dialog, which ->
            if (listener != null) {
                listener!!.onTarefaTrashSet(true)
            }

        }
        builder.setNegativeButton("Não") { dialog, which ->
            if (listener != null) {
                listener!!.onTarefaTrashSet(false)
            }

        }

        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_trash, null)

        builder.setView(view)

        return builder.create()
    }

    interface OnTarefaTrashSetListener  {

        fun onTarefaTrashSet(boo: Boolean)

    }

    companion object {
        fun show(fm: FragmentManager, listener: OnTarefaTrashSetListener) {
            val dialog = TrashDialog()
            dialog.listener = listener
            dialog.show(fm, "noteNameDialog")
        }
    }
}
