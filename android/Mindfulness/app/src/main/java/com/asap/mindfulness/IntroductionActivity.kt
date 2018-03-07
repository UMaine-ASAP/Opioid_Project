package com.asap.mindfulness

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_introduction.*

class IntroductionActivity : AppCompatActivity() {

    private var page = 0
    private lateinit var pageTitles: Array<String>
    private lateinit var pageTexts: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        // Set the TextViewSwitcher to just hold a standard TextView
        intro_text.setFactory {
            TextView(ContextThemeWrapper(this@IntroductionActivity, R.style.AppTheme), null, 0)
        }

        // Set the page change animations
        intro_text.inAnimation =
                AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        intro_text.outAnimation =
                AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)

        // Set the button listeners to change pages
        intro_next.setOnClickListener { updatePage(page + 1) }
        intro_prev.setOnClickListener { updatePage(page - 1) }

        // Load in the pages
        pageTitles = resources.getStringArray(R.array.intro_page_titles)
        pageTexts = resources.getStringArray(R.array.intro_pages)

        // Scroll to the first page
        updatePage(0)
    }

    fun updatePage(pageNum: Int) {
        if (pageNum > -1 && pageNum < pageTitles.size) {
            page = pageNum
            title = pageTitles[page]

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                (intro_text.nextView as TextView).setText(Html.fromHtml(pageTexts[page], Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
            } else {
                (intro_text.nextView as TextView).setText(Html.fromHtml(pageTexts[page]), TextView.BufferType.SPANNABLE)
            }

            intro_text.showNext()

            intro_prev.visibility = if (page == 0) View.INVISIBLE else View.VISIBLE
            intro_next.text = if (page == pageTitles.size - 1) {
                getText(R.string.intro_finish)
            } else {
                getText(R.string.intro_next_page)
            }
            intro_next.setOnClickListener(
                if (page == pageTitles.size - 1) object: View.OnClickListener {
                    override fun onClick(v: View?) {
                        val main = Intent(this@IntroductionActivity, ParentActivity::class.java)
                        startActivity(main)
                    }
                } else object: View.OnClickListener {
                    override fun onClick(v: View?) {
                        updatePage(page + 1)
                    }
                })
        }

    }
}
