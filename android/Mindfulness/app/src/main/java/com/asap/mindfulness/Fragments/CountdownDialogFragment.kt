package com.asap.mindfulness.Fragments

import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.asap.mindfulness.R
import kotlinx.android.synthetic.main.fragment_countdown_dialog.view.*
import java.util.concurrent.TimeUnit


class CountdownDialogFragment : DialogFragment() {
    lateinit var mListener: CountdownListener
    private lateinit var cd: CountDownTimer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_countdown_dialog, container, false)

        v.resumeButton.setOnClickListener {
            dismiss()
        }

        // counter for the media player 300000 = 5 min
        cd = object: CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                v.textCountDown.text = getString(R.string.media_popup_text,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60)

            }
            override fun onFinish() {
                mListener.onCountdownFinished()
                dismiss()
            }
        }.start()

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CountdownListener) mListener = context
    }

    override fun onDismiss(dialog: DialogInterface?) {
        cd.cancel()
        mListener.onDismissed()
    }

    companion object {

        internal fun newInstance(listener: CountdownListener): CountdownDialogFragment {
            val frag = CountdownDialogFragment()
            frag.mListener = listener

            return frag
        }
    }

    interface CountdownListener {
        fun onDismissed()
        fun onCountdownFinished()
    }
}
