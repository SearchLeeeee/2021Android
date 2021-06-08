package com.example.webviewapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.webviewapp.R;
import com.example.webviewapp.common.adapters.RecordRecyclerViewAdapter;
import com.example.webviewapp.contract.LabelContract;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.databinding.FragmentLabelBinding;
import com.example.webviewapp.presenter.LabelPresenter;

import java.util.List;

public class LabelFragment extends Fragment implements LabelContract.View {
    FragmentLabelBinding viewBinding;

    private LabelContract.Presenter presenter;
    private List<Record> records;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        presenter = new LabelPresenter(this);
        viewBinding = FragmentLabelBinding.inflate(inflater, container, false);
        initView();
        return viewBinding.getRoot();
    }

    private void initView() {
        records = presenter.getData();
//        for (long i = 0; i < 10; i++) {
//            records.add(new Record(100,i,"test","test","test",1));
//        }
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewBinding.recyclerView.setAdapter(new RecordRecyclerViewAdapter(records, getActivity(), R.layout.record_item));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
