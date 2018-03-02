package com.asap.mindfulness

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_introduction.*

class IntroductionActivity : AppCompatActivity() {

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        updatePage(0)
    }

    fun updatePage(pageNum: Int) {
        page = pageNum
        title = when (page) {
            0 -> getString(R.string.intro_page_one_title)
            else -> "Mindfulness"
        }
        intro_text.text = when(page) {
            0 -> getString(R.string.intro_page_one_text)
            else -> "Invalid page?"
        }
    }
}
