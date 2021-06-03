package com.example.webviewapp.common.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.example.webviewapp.common.utils.UIUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public abstract class BaseFragment extends Fragment {

    public View root;

    // 内部变量
    LayoutInflater inflater;
    ViewGroup container;

    protected boolean attachToParent() {
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;

        setupViews();
        return root;
    }

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
        Class<? extends BaseFragment> clazz = getClass();
        Class<? extends ViewBinding> vbClazz = ViewBinding.class;
        Field[] fields = clazz.getFields();

        for (Field field : fields) {
            Class<?> fClazz = field.getType();
            // 如果是 ViewBinding 子类
            if (vbClazz.isAssignableFrom(fClazz)) {
                Method inflate = fClazz.getMethod("inflate",
                        LayoutInflater.class, ViewGroup.class, boolean.class);
                ViewBinding vb = (ViewBinding) inflate.invoke(null,
                        inflater, container, attachToParent());
                root = vb.getRoot();
                field.set(this, vb);
            }
        }
    }

    protected Intent registerActivitySwitch(View view, Class<? extends Activity> clazz) {
        return UIUtils.registerActivitySwitch(getActivity(), view, clazz);
    }
}