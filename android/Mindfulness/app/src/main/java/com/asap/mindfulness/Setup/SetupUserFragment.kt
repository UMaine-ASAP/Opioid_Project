package com.asap.mindfulness.Setup


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.asap.mindfulness.R
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    mPrefs.edit()
                        .putString(getString(R.string.sp_name), patientName)
                        .apply()
                }

        }

        return rootView
    }

    companion object {
        fun newInstance() = SetupUserFragment()
    }


}// Required empty public constructor
