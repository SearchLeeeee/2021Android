package com.example.webviewapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.webviewapp.contract.HistoryContract
import com.example.webviewapp.data.Record
import com.example.webviewapp.databinding.FragmentHistoryBinding
import com.example.webviewapp.presenter.HistoryPresenter
import com.example.webviewapp.ui.activity.MainActivity
import java.util.*

class HistoryFragment : BaseFragment(), HistoryContract.View {
    var viewBinding: FragmentHistoryBinding? = null
    private var presenter: HistoryContract.Presenter? = null
    private var records: List<Record?>? = null
    private var adapter: RecordRecyclerViewAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = HistoryPresenter(this)
        viewBinding = FragmentHistoryBinding.inflate(getLayoutInflater())
        initData()
        initEditView()
        initButton()
        return viewBinding!!.root
    }

    private fun initData() {
        records = presenter!!.data
        initView(records)
    }

    private fun initButton() {
        viewBinding!!.delete.setText("删除")
        viewBinding!!.selectAll.setText("取消")
        viewBinding!!.deleteAll.setText("清空")
        viewBinding!!.deleteAll.setImage(R.drawable.delete)
        viewBinding!!.selectAll.setImage(R.drawable.close)
        viewBinding!!.delete.setImage(R.drawable.delete)
        viewBinding!!.deleteAll.setOnClickListener { v ->
            presenter!!.deleteAllHistory()
            records = presenter!!.refreshRecord()
            initView(records)
        }
        viewBinding!!.selectAll.setOnClickListener { v ->
            adapter!!.visible[0] = false
            viewBinding!!.deleteAll.setVisibility(View.VISIBLE)
            viewBinding!!.selectAll.setVisibility(View.GONE)
            viewBinding!!.delete.setVisibility(View.GONE)
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
                presenter!!.deleteHistoryByUrl(temp)
                records = presenter!!.refreshRecord()
                initView(records)
                viewBinding!!.deleteAll.setVisibility(View.VISIBLE)
                viewBinding!!.selectAll.setVisibility(View.GONE)
                viewBinding!!.delete.setVisibility(View.GONE)
            }
        }
    }

    private fun initView(re: List<Record?>?) {
        viewBinding!!.recyclerView.setLayoutManager(LinearLayoutManager(getActivity()))
        adapter = RecordRecyclerViewAdapter(re, getActivity(), R.layout.record_item)
        adapter!!.setOnItemClickListener(object : RecordRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                //TODO:历史点击处理
                val intent = Intent(getActivity(), MainActivity::class.java)
                intent.putExtra("url", re!![position]!!.url)
                startActivity(intent)
            }

            override fun onItemLongClick(view: View, position: Int) {
                //TODO：历史长按处理
                val layoutManager: LinearLayoutManager = (viewBinding!!.recyclerView.getLayoutManager() as LinearLayoutManager?)!!
                for (i in layoutManager.findFirstVisibleItemPosition()..layoutManager.findLastVisibleItemPosition()) {
                    val holder: RecyclerView.ViewHolder = viewBinding!!.recyclerView.findViewHolderForAdapterPosition(i)
                    val checkBox: CheckBox = holder.itemView.findViewById(R.id.checkbox)
                    checkBox.visibility = View.VISIBLE
                }
                viewBinding!!.deleteAll.setVisibility(View.GONE)
                viewBinding!!.selectAll.setVisibility(View.VISIBLE)
                viewBinding!!.delete.setVisibility(View.VISIBLE)
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

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditView()
    }

    fun onResume() {
        super.onResume()
        records = presenter!!.refreshRecord()
        initView(records)
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
        private const val TAG = "HistoryFragment"
    }
}