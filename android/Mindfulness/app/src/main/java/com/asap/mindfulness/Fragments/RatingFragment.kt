package com.asap.mindfulness.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.asap.mindfulness.R
import com.asap.mindfulness.Retrofit.service
import com.asap.mindfulness.SQLite.SQLManager
import kotlinx.android.synthetic.main.fragment_rating.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import android.widget.TextView
import com.asap.mindfulness.Containers.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RatingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RatingFragment : DialogFragment(), View.OnClickListener {




    private lateinit var completionHandeler: CompletionHandeler

    // TODO: Rename and change types of parameters
    private var prompt: String = "How would you rate that exercise?"
    private var resourceId: Int = -1

    lateinit var mPrefs: SharedPreferences
    var deviceId = ""

    private var thiContext: Context? = null

    private var mListener: OnFragmentInteractionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //submitButton.setOnClickListener(this)


    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //promtTextView.text = prompt
        return inflater!!.inflate(R.layout.fragment_rating, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPrefs = this.activity.getSharedPreferences(getString(R.string.sp_file_key), android.content.Context.MODE_PRIVATE)
        deviceId =  mPrefs.getString(getString(R.string.sp_study_id), "None")

        submitButton.setOnClickListener(this)
        promptTextView.text = prompt
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        //submitButton.setOnClickListener(this)
//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(view: View?) {
        if(view == submitButton) {
            Log.d("Clicked", "button")
            //submit survey to server, if good submit using retrofit else submit to local sql lite
            val survey = Survey(deviceId, resourceId,ratingBar.rating.toInt(), Date())
            addSurvey(survey)
            dismiss()
            completionHandeler.complete()
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(p: String, r: Int, completionHandeler: CompletionHandeler, context: Context): RatingFragment {
            val fragment = RatingFragment()
            fragment.prompt = p
            fragment.resourceId = r
            fragment.completionHandeler = completionHandeler
            fragment.thiContext = context
            return fragment
        }
    }

    fun addSurvey(survey: Survey){
        val call = service.postSurvey(survey)

        call.enqueue(object : Callback<Success> {

            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {

                if (response == null || response.code() >= 300 || response.body()?.error == true) {
                    Log.d("onResponse", "response is null")
                    addSurveyToDatabase(survey)
                    return
                }
                // was successfull
            }

            override fun onFailure(call: Call<Success>?, t: Throwable?) {
                Log.d("onResponse", "Error")
                addSurveyToDatabase(survey)
            }
        })
    }

    fun addSurveyToDatabase(survey: Survey){

        Log.d(" Note ", "Adding survey")

        val db = SQLManager(thiContext)
        db.registerDatabase("Updatables")

        db.insertRow("Updatables", "Survey_History", "resource_id, rating, creation_date, server_pushed",
                String.format("%d,%d,%d,%d",
                        survey.resource_id,
                        survey.rating,
                        survey.creation_date.time,
                        0
                )
        )

    }

}// Required empty public constructor
