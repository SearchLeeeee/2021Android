package com.example.webviewapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webviewapp.R;
import com.example.webviewapp.common.adapters.RecordRecyclerViewAdapter;
import com.example.webviewapp.common.base.BaseFragment;
import com.example.webviewapp.contract.LabelContract;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.databinding.FragmentLabelBinding;
import com.example.webviewapp.presenter.LabelPresenter;
import com.example.webviewapp.ui.activity.EditRecordActivity;

import java.util.ArrayList;
import java.util.List;

public class LabelFragment extends BaseFragment implements LabelContract.View {
    private static final String TAG = "LabelFragment";
    public FragmentLabelBinding viewBinding;

    private RecordRecyclerViewAdapter adapter;
    private LabelContract.Presenter presenter;
    private List<Record> records;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        presenter = new LabelPresenter(this);
        initData();
        initButton();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        records = presenter.getData();
        initView(records);
    }

    private void initView(List<Record> re) {
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecordRecyclerViewAdapter(re, getActivity(), R.layout.record_item);
        adapter.setOnItemClickListener(new RecordRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                    //TODO:书签点击处理
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO：书签长按处理
                viewBinding.bottomBar.setVisibility(View.VISIBLE);
            }
        });
        viewBinding.recyclerView.setAdapter(adapter);
        viewBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                presenter.checkScrolled(dy);
            }
        });
    }

    private void initButton() {
        viewBinding.delete.setText("删除");
        viewBinding.selectAll.setText("全选");
        viewBinding.cancel.setText("取消");
        viewBinding.edit.setText("编辑");
        viewBinding.edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditRecordActivity.class);
            intent.putExtra("primaryKey", adapter.getSelectedPosition());
            startActivity(intent);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEditView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initEditView() {
        viewBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = viewBinding.editText.getText().toString();
                List<Record> output = new ArrayList<>();
                for (Record record : records) {
                    if (record.getTitle().contains(input) || record.getDetails().contains(input)) {
                        output.add(record);
                    }
                }
                initView(output);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void setEditTextVisibility(Boolean isVisible) {
        if (isVisible) {
            viewBinding.editText.setVisibility(View.VISIBLE);
        } else {
            viewBinding.editText.setVisibility(View.GONE);
        }
    }
}
