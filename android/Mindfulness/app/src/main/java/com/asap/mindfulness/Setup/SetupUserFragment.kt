package com.asap.mindfulness.Setup


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.asap.mindfulness.R
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import com.asap.mindfulness.R.id.view
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.fragment_setup_user.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [SetupUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupUserFragment : Fragment() {
    private var patientName: String = ""
        get() = rootView.patient_name.text.toString()
    private var patientPasswordEnabled = true
        get() = rootView.patient_password_switch.isChecked
    private var patientPassword: String = ""
        get() = rootView.patient_password.text.toString()
    lateinit var rootView: View
    private lateinit var mPrefs: SharedPreferences

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Initialize preferences
        mPrefs = context.getSharedPreferences(getString(R.string.sp_file_key), MODE_PRIVATE)
        // Inflate the layout for this fragment
        rootView = inflater!!.inflate(R.layout.fragment_setup_user, container, false)

        rootView.patient_name.setOnFocusChangeListener { v: View, hasFocus: Boolean ->
                if (hasFocus) {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
                } else {
                    if (!patientPasswordEnabled) {
                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                    mPrefs.edit()
                        .putString(getString(R.string.sp_name), patientName)
                        .apply()
                }

        }

        rootView.patient_password.setOnFocusChangeListener { v: View, hasFocus: Boolean ->
            if (hasFocus && patientPasswordEnabled) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            } else {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                mPrefs.edit()
                    .putString(getString(R.string.sp_password), patientPassword)
                    .apply()
            }

        }

        with(mPrefs.edit()) {
            putBoolean(getString(R.string.sp_password_enabled), false)
            commit()
        }

        rootView.patient_password_switch.setOnCheckedChangeListener { _, b ->
            mPrefs.edit()
                .putBoolean(getString(R.string.sp_password_enabled), b)
                .apply()

            rootView.patient_password.inputType = if (b) {
                81
            } else {
                0       // Disabled
            }

            if (rootView.patient_password.requestFocus()) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }

        }

        return rootView
    }

    companion object {
        fun newInstance() = SetupUserFragment()
    }

}// Required empty public constructor
