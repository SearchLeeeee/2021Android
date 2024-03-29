package com.example.webviewapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

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
import com.example.webviewapp.ui.activity.MainActivity;

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
        viewBinding = FragmentLabelBinding.inflate(getLayoutInflater());
        presenter = new LabelPresenter(this);
        initData();
        initEditView();
        initButton();
        return viewBinding.getRoot();
    }

    private void initData() {
        records = presenter.getData();
        initView(records);
    }

    private void initView(List<Record> re) {
        for (int i = 0; i < re.size(); i++) {
            Log.d(TAG, "initView: " + re.get(i).getTitle() + i);
        }
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecordRecyclerViewAdapter(re, getActivity(), R.layout.record_item);
        adapter.setOnItemClickListener(new RecordRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //TODO:书签点击处理
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("url", re.get(position).getUrl());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO：书签长按处理
                viewBinding.bottomBar.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = (LinearLayoutManager) viewBinding.recyclerView.getLayoutManager();
                assert layoutManager != null;
                for (int i = layoutManager.findFirstVisibleItemPosition(); i <= layoutManager.findLastVisibleItemPosition(); i++) {
                    RecyclerView.ViewHolder holder = viewBinding.recyclerView.findViewHolderForAdapterPosition(i);
                    CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);
                    checkBox.setVisibility(View.VISIBLE);
                }
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
        viewBinding.cancel.setText("取消");
        viewBinding.edit.setText("编辑");

        viewBinding.delete.setImage(R.drawable.delete);
        viewBinding.edit.setImage(R.drawable.edit);
        viewBinding.cancel.setImage(R.drawable.clean);

        viewBinding.edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditRecordActivity.class);
            if (adapter.getSelectedPosition() == 0)
                Toast.makeText(getActivity(), "无点击书签", Toast.LENGTH_SHORT).show();
            else {
                if (adapter.getSelectedPositions().size() > 1)
                    Toast.makeText(getActivity(), "只能选择一个书签", Toast.LENGTH_SHORT).show();
                else {
                    viewBinding.bottomBar.setVisibility(View.GONE);
                    intent.putExtra("primaryKey", adapter.getSelectedPosition());
                    startActivity(intent);
                }
            }
        });
        viewBinding.cancel.setOnClickListener(v -> {
            adapter.visible[0] = false;
            viewBinding.bottomBar.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = (LinearLayoutManager) viewBinding.recyclerView.getLayoutManager();
            assert layoutManager != null;
            for (int i = layoutManager.findFirstVisibleItemPosition(); i <= layoutManager.findLastVisibleItemPosition(); i++) {
                RecyclerView.ViewHolder holder = viewBinding.recyclerView.findViewHolderForAdapterPosition(i);
                CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);
                checkBox.setVisibility(View.INVISIBLE);
            }
        });

        viewBinding.delete.setOnClickListener(v -> {
            if (adapter.getSelectedPosition() == 0)
                Toast.makeText(getActivity(), "无点击书签", Toast.LENGTH_SHORT).show();
            else {
                List<String> temp = adapter.getSelectedPositions();
                presenter.deleteLabel(temp);
                records = presenter.refreshRecord();
                initView(records);
                viewBinding.bottomBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEditView();
    }

    public void onResume() {
        super.onResume();
        records = presenter.refreshRecord();
        initView(records);
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
                    if (record.getTitle().toLowerCase().contains(input.toLowerCase()) ||
                            record.getDetails().toLowerCase().contains(input.toLowerCase())) {
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
