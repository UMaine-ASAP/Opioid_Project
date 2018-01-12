package com.asap.mindfulness.Fragments

/**
 * @author Spencer Ward
 * @created November 15, 2017
 *
 * A callback interface to handle navigation requests between fragments
 *
 */

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 *
 *
 * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
 */
interface OnNavigationRequestListener {
    fun onPageRequested(page: Int): Boolean
    fun onSurveyRequested(url: String): Boolean
}