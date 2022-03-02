package com.example.webviewapp.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.webviewapp.R

class RecordEditButton(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var image: ImageView? = null
    private var text: TextView? = null
    private fun initViews(context: Context, attrs: AttributeSet?) {
        val root = LayoutInflater.from(context).inflate(R.layout.record_edit_button, this)
        image = root.findViewById(R.id.image)
        text = root.findViewById(R.id.text)
    }

    fun setImage(src: Int) {
        image!!.setImageResource(src)
    }

    fun setText(text: String?) {
        this.text!!.text = text
    }

    init {
        initViews(context, attrs)
    }
}