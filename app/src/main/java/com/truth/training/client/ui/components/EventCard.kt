package com.truth.training.client.ui.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.truth.training.client.data.network.dto.TruthEvent

class EventCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val scoreText: TextView = TextView(context).apply { textSize = 12f }
    private val scoreBar: ProgressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
        max = 100
        visibility = View.GONE
    }

    init {
        orientation = VERTICAL
        val pad = (8 * resources.displayMetrics.density).toInt()
        setPadding(pad, pad, pad, pad)
        addView(scoreText, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(scoreBar, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        visibility = View.GONE
    }

    fun bind(event: TruthEvent?) {
        val score = event?.collectiveScore
        if (score == null) {
            visibility = View.GONE
            return
        }
        val percentage = (score * 100).toInt().coerceIn(0, 100)
        scoreText.text = "Collective Truth: ${percentage}%"
        scoreText.setTextColor(if (score > 0.5) Color.parseColor("#4CAF50") else Color.parseColor("#F44336"))
        scoreBar.progress = percentage
        scoreBar.visibility = View.VISIBLE
        visibility = View.VISIBLE
    }
}
