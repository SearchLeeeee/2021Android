package com.example.webviewapp.common.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.example.webviewapp.common.utils.UIUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    // region 初始化

    /**
     * 初始化
     */
    protected void setupViews() {
        try {
            setupViewBinding();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 配置View绑定
    void setupViewBinding() throws Exception {
        Class<? extends BaseActivity> clazz = getClass();
        Class<? extends ViewBinding> vbClazz = ViewBinding.class;
        Field[] fields = clazz.getFields();

        for (Field field : fields) {
            Class<?> fClazz = field.getType();
            // 如果是 ViewBinding 子类
            if (vbClazz.isAssignableFrom(fClazz)) {
                Method inflate = fClazz.getMethod("inflate", LayoutInflater.class);
                ViewBinding vb = (ViewBinding) inflate.invoke(null, getLayoutInflater());
                setContentView(vb.getRoot());
                field.set(this, vb);
            }
        }
    }

    // endregion

    protected Intent registerActivitySwitch(View view, Class<? extends Activity> clazz) {
        return UIUtils.registerActivitySwitch(this, view, clazz);
    }
}
