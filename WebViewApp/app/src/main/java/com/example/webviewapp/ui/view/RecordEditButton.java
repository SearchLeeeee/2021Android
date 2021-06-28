package com.example.webviewapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.webviewapp.R;

public class RecordEditButton extends ConstraintLayout {

    private ImageView image;
    private TextView text;

    public RecordEditButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        View root = LayoutInflater.from(context).inflate(R.layout.record_edit_button, this);
        image = root.findViewById(R.id.image);
        text = root.findViewById(R.id.text);
    }

    public void setImage(int src) {
        this.image.setImageResource(src);
    }

    public void setText(String text) {
        this.text.setText(text);
    }
}
