package com.example.webviewapp.common.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.example.webviewapp.common.utils.UIUtils;
import com.example.webviewapp.data.DataManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 继承后可以简化ViewBinding的使用，只需要在实现类中声明即可
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        getSupportActionBar().hide();
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

    /**
     * 配置View绑定
     * @throws Exception
     */
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

    /**
     * 封装好点击一个view跳转到一个activity的动作
     *
     * @param view  被点击的view
     * @param clazz 目标activity
     * @return
     */
    protected Intent registerActivitySwitch(View view, Class<? extends Activity> clazz) {
        return UIUtils.registerActivitySwitch(this, view, clazz);
    }

    public static final int OPEN_SET_REQUEST_CODE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == OPEN_SET_REQUEST_CODE)
            for (int i = 0; i < permissions.length; i++)
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 申请成功
                    successRequestPermission(permissions[i]);
                } else {
                    // 申请失败
                    failRequestPermission(permissions[i]);
                }
    }

    protected void failRequestPermission(String permission) {
        Log.d(TAG, "checkPermission: " + "[权限]" + permission + " 申请失败");
        Toast.makeText(this, "[权限]" + permission + " 申请失败", Toast.LENGTH_SHORT).show();
        DataManager.get().addDeniedPermission(permission);
    }

    protected void successRequestPermission(String permission) {
        Log.d(TAG, "checkPermission: " + "[权限]" + permission + " 申请成功");
        DataManager.get().deleteDeniedPermission(permission);
    }
}
