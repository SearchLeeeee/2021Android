package com.example.webviewapp.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webviewapp.R;
import com.example.webviewapp.common.adapters.RecordRecyclerViewAdapter;
import com.example.webviewapp.contract.HistoryContract;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.databinding.FragmentHistoryBinding;
import com.example.webviewapp.presenter.HistoryPresenter;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryContract.View {
    private static final String TAG = "HistoryFragment";
    FragmentHistoryBinding viewBinding;

    private HistoryContract.Presenter presenter;
    private List<Record> records;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        presenter = new HistoryPresenter(this);
        viewBinding = FragmentHistoryBinding.inflate(inflater, container, false);
        initData();
        initButton();
        return viewBinding.getRoot();
    }

    private void initData() {
        records = presenter.getData();
        initView(records);
    }

    private void initButton() {
        viewBinding.delete.setText("删除");
        viewBinding.selectAll.setText("全选");
        viewBinding.deleteAll.setText("清空");
    }

    private void initView(List<Record> re) {
        for (int i = 0; i < re.size(); i++) {
            Log.d(TAG, "initView: " + re.get(i).getTitle() + i);
        }
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecordRecyclerViewAdapter adapter = new RecordRecyclerViewAdapter(re, getActivity(), R.layout.record_item);
        adapter.setOnItemClickListener(new RecordRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //TODO:历史点击处理
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //TODO：历史长按处理
            }
        });
        viewBinding.recyclerView.setAdapter(adapter);
        viewBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20) {
                    viewBinding.editText.setVisibility(View.GONE);
                }
                if (dy < 0) {
                    viewBinding.editText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEditView();
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
}
