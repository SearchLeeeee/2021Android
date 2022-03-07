package com.example.webviewapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.webviewapp.contract.HistoryContract;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.databinding.FragmentHistoryBinding;
import com.example.webviewapp.presenter.HistoryPresenter;
import com.example.webviewapp.ui.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseFragment implements HistoryContract.View {
    private static final String TAG = "HistoryFragment";
    public FragmentHistoryBinding viewBinding;

    private HistoryContract.Presenter presenter;
    private List<Record> records;
    private RecordRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        presenter = new HistoryPresenter(this);
        viewBinding = FragmentHistoryBinding.inflate(getLayoutInflater());
        initData();
        initEditView();
        initButton();
        return viewBinding.getRoot();
    }

    private void initData() {
        records = presenter.data;
        initView(records);
    }

    private void initButton() {
        viewBinding.delete.setText("删除");
        viewBinding.selectAll.setText("取消");
        viewBinding.deleteAll.setText("清空");
        viewBinding.deleteAll.setImage(R.drawable.delete);
        viewBinding.selectAll.setImage(R.drawable.close);
        viewBinding.delete.setImage(R.drawable.delete);
        viewBinding.deleteAll.setOnClickListener(v -> {
            presenter.deleteAllHistory();
            records = presenter.refreshRecord();
            initView(records);
        });
        viewBinding.selectAll.setOnClickListener(v -> {
            adapter.visible[0] = false;
            viewBinding.deleteAll.setVisibility(View.VISIBLE);
            viewBinding.selectAll.setVisibility(View.GONE);
            viewBinding.delete.setVisibility(View.GONE);
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
            else{
                List<String> temp = adapter.getSelectedPositions();
                presenter.deleteHistoryByUrl(temp);
                records = presenter.refreshRecord();
                initView(records);
                viewBinding.deleteAll.setVisibility(View.VISIBLE);
                viewBinding.selectAll.setVisibility(View.GONE);
                viewBinding.delete.setVisibility(View.GONE);
            }
        });
    }

    private void initView(List<Record> re) {
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecordRecyclerViewAdapter(re, getActivity(), R.layout.record_item);
        adapter.setOnItemClickListener(new RecordRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //TODO:历史点击处理
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("url", re.get(position).url);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO：历史长按处理
                LinearLayoutManager layoutManager = (LinearLayoutManager) viewBinding.recyclerView.getLayoutManager();
                assert layoutManager != null;
                for (int i = layoutManager.findFirstVisibleItemPosition(); i <= layoutManager.findLastVisibleItemPosition(); i++) {
                    RecyclerView.ViewHolder holder = viewBinding.recyclerView.findViewHolderForAdapterPosition(i);
                    CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);
                    checkBox.setVisibility(View.VISIBLE);
                }
                viewBinding.deleteAll.setVisibility(View.GONE);
                viewBinding.selectAll.setVisibility(View.VISIBLE);
                viewBinding.delete.setVisibility(View.VISIBLE);
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
                    if (record.title.toLowerCase().contains(input.toLowerCase()) ||
                            record.details.toLowerCase().contains(input.toLowerCase())) {
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
