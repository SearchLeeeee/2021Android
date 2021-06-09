package com.example.webviewapp.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewapp.data.DataManager;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.databinding.ActivityEditRecordBinding;

public class EditRecordActivity extends AppCompatActivity {
    private static final String TAG = "EditRecordActivity";
    ActivityEditRecordBinding viewBinding;

    Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityEditRecordBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        getSupportActionBar().hide();

        record = DataManager.get().queryRecordByPrimaryKey(getIntent().getLongExtra("primaryKey", 0));

        initButton();
        initEditView();
    }

    private void initEditView() {
        viewBinding.title.setText(record.getTitle());
        viewBinding.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkContent();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkContent();
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkContent();
            }
        });
        viewBinding.details.setText(record.getDetails());
        viewBinding.details.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkContent();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkContent();
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkContent();
            }
        });
    }

    private void checkContent() {
        if (viewBinding.title.getText() != null && viewBinding.details.getText() != null) {
            viewBinding.confirmButton.setVisibility(View.VISIBLE);
        } else {
            viewBinding.confirmButton.setVisibility(View.INVISIBLE);
        }
    }

    private void initButton() {
        viewBinding.backButton.setOnClickListener(v -> finish());
        viewBinding.confirmButton.setOnClickListener(v -> {
            //TODO:把修改写回数据库
            if (viewBinding.title.getText().toString().isEmpty()) {
                Toast.makeText(this, "书签标题不能为空！", Toast.LENGTH_SHORT).show();
            } else if (viewBinding.details.getText().toString().isEmpty()) {
                Toast.makeText(this, "书签详情不能为空！", Toast.LENGTH_SHORT).show();
            } else {
                record.setTitle(viewBinding.title.getText().toString());
                record.setDetails(viewBinding.details.getText().toString());
                DataManager.get().updateRecord(record);
                finish();
            }
        });
    }
}