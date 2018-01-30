package com.asap.mindfulness.Setup


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.fragment_setup_patient.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [SetupPatientFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetupPatientFragment : Fragment() {
    private var patientId: String = ""
        get() = rootView.patient_id.text.toString()
    private lateinit var rootView: View
    private lateinit var mPrefs: SharedPreferences

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Initialize preferences
        mPrefs = context.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE)

        // Inflate the layout for this fragment
        rootView = inflater!!.inflate(R.layout.fragment_setup_patient, container, false)

        if (rootView.patient_id.requestFocus()) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(rootView, InputMethodManager.SHOW_IMPLICIT)
        }

        rootView.patient_id.setOnFocusChangeListener { v: View, hasFocus: Boolean ->
            if (hasFocus) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            } else {
                if (patientId != "") {
                    val patientIdInt = Integer.parseInt(patientId)
                    mPrefs.edit()
                            .putInt(getString(com.asap.mindfulness.R.string.sp_study_id), patientIdInt)
                            .apply()
                }

                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

            }

        }

        return rootView
    }

    companion object {
        fun newInstance() = SetupPatientFragment()
    }

}// Required empty public constructor
