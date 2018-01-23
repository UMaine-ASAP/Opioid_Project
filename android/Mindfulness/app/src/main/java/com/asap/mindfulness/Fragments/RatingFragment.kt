package com.asap.mindfulness.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asap.mindfulness.Containers.Success
import com.asap.mindfulness.Containers.Survey

import com.asap.mindfulness.R
import com.asap.mindfulness.Retrofit.service
import kotlinx.android.synthetic.main.fragment_rating.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RatingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RatingFragment : DialogFragment(), View.OnClickListener {



    // TODO: Rename and change types of parameters
    private var prompt: String = "How are you feeling?"
    private var resourceId: Int = -1

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        submitButton.setOnClickListener(this)
        promtTextView.text = prompt
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_rating, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
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
            //submit survey to server, if good submit using retrofit else submit to local sql lite
            val deviceId = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)
            val survey = Survey(deviceId, resourceId,ratingBar.numStars, Calendar.getInstance().getTime())
            addSurvey(survey)
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
        fun newInstance(): RatingFragment {
            val fragment = RatingFragment()
            return fragment
        }
    }

    fun addSurvey(survey: Survey){
        val call = service.postSurvey(survey)

        call.enqueue(object : Callback<Success> {

            override fun onResponse(call: Call<Success>?, response: Response<Success>?) {

                if (response == null) {
                    Log.d("onResponse", "response is null")
                    addSurveyToDatabase(survey)
                    return
                }

                if(response.code() >= 300){
                    Log.d("onResponse", response.body().toString())
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
        //todo add survey to database
    }

}// Required empty public constructor
