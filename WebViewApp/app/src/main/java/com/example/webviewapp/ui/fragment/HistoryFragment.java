package com.example.webviewapp.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.webviewapp.R;
import com.example.webviewapp.common.adapters.RecordRecyclerViewAdapter;
import com.example.webviewapp.contract.HistoryContract;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.databinding.FragmentHistoryBinding;
import com.example.webviewapp.presenter.HistoryPresenter;

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
        initView();
        initButton();
        return viewBinding.getRoot();
    }

    private void initButton() {
        viewBinding.delete.setText("删除");
        viewBinding.selectAll.setText("全选");
        viewBinding.deleteAll.setText("清空");
    }

    private void initView() {
        records = presenter.getData();
        for (int i = 0; i < records.size(); i++) {
            Log.d(TAG, "initView: " + records.get(i).getTitle() + i);
        }
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewBinding.recyclerView.setAdapter(new RecordRecyclerViewAdapter(records, getActivity(), R.layout.record_item));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
