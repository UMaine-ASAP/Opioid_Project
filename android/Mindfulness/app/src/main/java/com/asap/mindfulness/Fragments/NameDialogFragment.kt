package com.asap.mindfulness.Fragments

import android.app.DialogFragment
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.fragment_name_dialog.view.*


class NameDialogFragment : DialogFragment() {
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mListener: OnNameChangeListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                     savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_name_dialog, container, false)

        mPrefs = inflater.context.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

        v.user_name_edit.setText(mPrefs.getString(getString(R.string.sp_name), "New Name"))

        v.user_name_set.setOnClickListener {
            val newName = v.user_name_edit.text.toString()
            with (mPrefs.edit()) {
                putString(getString(R.string.sp_name), newName)
                apply()
            }
            mListener.nameChanged(newName)
            dismiss()
        }

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnNameChangeListener) mListener = context
    }

    companion object {

        internal fun newInstance(): NameDialogFragment {
            return NameDialogFragment()
        }
    }

    interface OnNameChangeListener {
        fun nameChanged(newName: String)
    }
}
