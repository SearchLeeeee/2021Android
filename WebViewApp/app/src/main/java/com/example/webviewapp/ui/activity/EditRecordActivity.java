package com.example.webviewapp.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.webviewapp.common.base.BaseActivity;
import com.example.webviewapp.contract.EditRecordContract;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.databinding.ActivityEditRecordBinding;
import com.example.webviewapp.presenter.EditRecordPresenter;

public class EditRecordActivity extends BaseActivity implements EditRecordContract.View {
    private static final String TAG = "EditRecordActivity";
    public ActivityEditRecordBinding viewBinding;
    private EditRecordContract.Presenter presenter;

    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        presenter = new EditRecordPresenter(this);
        record = presenter.getData(getIntent().getLongExtra("primaryKey", 0));

        initButton();
        initEditView();
    }

    private void initEditView() {
        viewBinding.title.setText(record.title);
        viewBinding.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.checkContent(viewBinding.title.getText().toString(), viewBinding.url.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.checkContent(viewBinding.title.getText().toString(), viewBinding.url.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.checkContent(viewBinding.title.getText().toString(), viewBinding.url.getText().toString());
            }
        });
        viewBinding.url.setText(record.url);
        viewBinding.url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.checkContent(viewBinding.title.getText().toString(), viewBinding.url.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.checkContent(viewBinding.title.getText().toString(), viewBinding.url.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.checkContent(viewBinding.title.getText().toString(), viewBinding.url.getText().toString());
            }
        });
    }

    private void initButton() {
        viewBinding.backButton.setOnClickListener(v -> finish());
        viewBinding.confirmButton.setOnClickListener(v -> {
            if (viewBinding.title.getText().toString().isEmpty()) {
                Toast.makeText(this, "书签标题不能为空！", Toast.LENGTH_SHORT).show();
            } else if (viewBinding.url.getText().toString().isEmpty()) {
                Toast.makeText(this, "书签详情不能为空！", Toast.LENGTH_SHORT).show();
            } else {
                record.title = viewBinding.title.getText().toString();
                record.url = viewBinding.url.getText().toString();
                presenter.updateRecord(record);
                finish();
            }
        });
    }

    @Override
    public void setConfirmButtonVisibility(Boolean isVisible) {
        if (isVisible) {
            viewBinding.confirmButton.setVisibility(View.VISIBLE);
        } else {
            viewBinding.confirmButton.setVisibility(View.INVISIBLE);
        }
    }
}