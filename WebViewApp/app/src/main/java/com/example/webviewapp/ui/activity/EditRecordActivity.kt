package com.example.webviewapp.ui.activity

import android.content.Intent.getIntent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.webviewapp.common.base.BaseActivity
import com.example.webviewapp.contract.EditRecordContract
import com.example.webviewapp.data.Record
import com.example.webviewapp.databinding.ActivityEditRecordBinding
import com.example.webviewapp.presenter.EditRecordPresenter

class EditRecordActivity : BaseActivity(), EditRecordContract.View {
    var viewBinding: ActivityEditRecordBinding? = null
    private var presenter: EditRecordContract.Presenter? = null
    private var record: Record? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar().hide()
        presenter = EditRecordPresenter(this)
        record = presenter.getData(getIntent().getLongExtra("primaryKey", 0))
        initButton()
        initEditView()
    }

    private fun initEditView() {
        viewBinding!!.title.setText(record!!.title)
        viewBinding!!.title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                presenter!!.checkContent(viewBinding!!.title.text.toString(), viewBinding!!.url.text.toString())
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter!!.checkContent(viewBinding!!.title.text.toString(), viewBinding!!.url.text.toString())
            }

            override fun afterTextChanged(s: Editable) {
                presenter!!.checkContent(viewBinding!!.title.text.toString(), viewBinding!!.url.text.toString())
            }
        })
        viewBinding!!.url.setText(record!!.url)
        viewBinding!!.url.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                presenter!!.checkContent(viewBinding!!.title.text.toString(), viewBinding!!.url.text.toString())
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                presenter!!.checkContent(viewBinding!!.title.text.toString(), viewBinding!!.url.text.toString())
            }

            override fun afterTextChanged(s: Editable) {
                presenter!!.checkContent(viewBinding!!.title.text.toString(), viewBinding!!.url.text.toString())
            }
        })
    }

    private fun initButton() {
        viewBinding!!.backButton.setOnClickListener { v: View? -> finish() }
        viewBinding!!.confirmButton.setOnClickListener { v: View? ->
            if (viewBinding!!.title.text.toString().isEmpty()) {
                Toast.makeText(this, "书签标题不能为空！", Toast.LENGTH_SHORT).show()
            } else if (viewBinding!!.url.text.toString().isEmpty()) {
                Toast.makeText(this, "书签详情不能为空！", Toast.LENGTH_SHORT).show()
            } else {
                record!!.title = viewBinding!!.title.text.toString()
                record!!.url = viewBinding!!.url.text.toString()
                presenter!!.updateRecord(record)
                finish()
            }
        }
    }

    override fun setConfirmButtonVisibility(isVisible: Boolean?) {
        if (isVisible!!) {
            viewBinding!!.confirmButton.visibility = View.VISIBLE
        } else {
            viewBinding!!.confirmButton.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val TAG = "EditRecordActivity"
    }
}