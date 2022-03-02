package com.example.webviewapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.webviewapp.R
import com.example.webviewapp.common.adapters.RecordRecyclerViewAdapter
import com.example.webviewapp.common.base.BaseFragment
import com.example.webviewapp.contract.LabelContract
import com.example.webviewapp.data.Record
import com.example.webviewapp.databinding.FragmentLabelBinding
import com.example.webviewapp.presenter.LabelPresenter
import com.example.webviewapp.ui.activity.EditRecordActivity
import com.example.webviewapp.ui.activity.MainActivity
import java.util.*

class LabelFragment : BaseFragment(), LabelContract.View {
    var viewBinding: FragmentLabelBinding? = null
    private var adapter: RecordRecyclerViewAdapter? = null
    private var presenter: LabelContract.Presenter? = null
    private var records: List<Record?>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = FragmentLabelBinding.inflate(getLayoutInflater())
        presenter = LabelPresenter(this)
        initData()
        initEditView()
        initButton()
        return viewBinding!!.root
    }

    private fun initData() {
        records = presenter!!.data
        initView(records)
    }

    private fun initView(re: List<Record?>?) {
        for (i in re!!.indices) {
            Log.d(TAG, "initView: " + re[i]!!.title + i)
        }
        viewBinding!!.recyclerView.setLayoutManager(LinearLayoutManager(getActivity()))
        adapter = RecordRecyclerViewAdapter(re, getActivity(), R.layout.record_item)
        adapter!!.setOnItemClickListener(object : RecordRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                //TODO:书签点击处理
                val intent = Intent(getActivity(), MainActivity::class.java)
                intent.putExtra("url", re[position]!!.url)
                startActivity(intent)
            }

            override fun onItemLongClick(view: View, position: Int) {
                //TODO：书签长按处理
                viewBinding!!.bottomBar.visibility = View.VISIBLE
                val layoutManager: LinearLayoutManager = (viewBinding!!.recyclerView.getLayoutManager() as LinearLayoutManager?)!!
                for (i in layoutManager.findFirstVisibleItemPosition()..layoutManager.findLastVisibleItemPosition()) {
                    val holder: RecyclerView.ViewHolder = viewBinding!!.recyclerView.findViewHolderForAdapterPosition(i)
                    val checkBox: CheckBox = holder.itemView.findViewById(R.id.checkbox)
                    checkBox.visibility = View.VISIBLE
                }
            }
        })
        viewBinding!!.recyclerView.setAdapter(adapter)
        viewBinding!!.recyclerView.addOnScrollListener(object : OnScrollListener() {
            fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                presenter!!.checkScrolled(dy)
            }
        })
    }

    private fun initButton() {
        viewBinding!!.delete.setText("删除")
        viewBinding!!.cancel.setText("取消")
        viewBinding!!.edit.setText("编辑")
        viewBinding!!.delete.setImage(R.drawable.delete)
        viewBinding!!.edit.setImage(R.drawable.edit)
        viewBinding!!.cancel.setImage(R.drawable.clean)
        viewBinding!!.edit.setOnClickListener { v ->
            val intent = Intent(getActivity(), EditRecordActivity::class.java)
            if (adapter!!.selectedPosition == 0L) Toast.makeText(getActivity(), "无点击书签", Toast.LENGTH_SHORT).show() else {
                if (adapter!!.selectedPositions.size > 1) Toast.makeText(getActivity(), "只能选择一个书签", Toast.LENGTH_SHORT).show() else {
                    viewBinding!!.bottomBar.visibility = View.GONE
                    intent.putExtra("primaryKey", adapter!!.selectedPosition)
                    startActivity(intent)
                }
            }
        }
        viewBinding!!.cancel.setOnClickListener { v ->
            adapter!!.visible[0] = false
            viewBinding!!.bottomBar.visibility = View.GONE
            val layoutManager: LinearLayoutManager = (viewBinding!!.recyclerView.getLayoutManager() as LinearLayoutManager?)!!
            for (i in layoutManager.findFirstVisibleItemPosition()..layoutManager.findLastVisibleItemPosition()) {
                val holder: RecyclerView.ViewHolder = viewBinding!!.recyclerView.findViewHolderForAdapterPosition(i)
                val checkBox: CheckBox = holder.itemView.findViewById(R.id.checkbox)
                checkBox.visibility = View.INVISIBLE
            }
        }
        viewBinding!!.delete.setOnClickListener { v ->
            if (adapter!!.selectedPosition == 0L) Toast.makeText(getActivity(), "无点击书签", Toast.LENGTH_SHORT).show() else {
                val temp = adapter!!.selectedPositions
                presenter!!.deleteLabel(temp)
                records = presenter!!.refreshRecord()
                initView(records)
                viewBinding!!.bottomBar.visibility = View.GONE
            }
        }
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditView()
    }

    fun onResume() {
        super.onResume()
        records = presenter!!.refreshRecord()
        initView(records)
    }

    fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && adapter != null) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun initEditView() {
        viewBinding!!.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = viewBinding!!.editText.text.toString()
                val output: MutableList<Record?> = ArrayList()
                for (record in records!!) {
                    if (record!!.title!!.toLowerCase().contains(input.toLowerCase()) ||
                            record.details!!.toLowerCase().contains(input.toLowerCase())) {
                        output.add(record)
                    }
                }
                initView(output)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun setEditTextVisibility(isVisible: Boolean?) {
        if (isVisible!!) {
            viewBinding!!.editText.visibility = View.VISIBLE
        } else {
            viewBinding!!.editText.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "LabelFragment"
    }
}