package com.example.webviewapp.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadUtils {
    public static void savePicture(Context context, Bitmap bitmap, SaveResultCallback saveResultCallback) {
        final File path = getSDPath();
        if (path == null) {
            Toast.makeText(context, "未开启存储权限或本地存储不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                File appPath = new File(path, "out_photo");
                if (!appPath.exists()) {
                    appPath.mkdir();
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String fileName = simpleDateFormat.format(new Date()) + ".png";
                File file = new File(appPath, fileName);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    saveResultCallback.onSavedSuccess();
                } catch (FileNotFoundException e) {
                    saveResultCallback.onSavedFailed();
                    e.printStackTrace();
                } catch (IOException e) {
                    saveResultCallback.onSavedFailed();
                    e.printStackTrace();
                }

                Uri uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }
        }).start();
    }

    /**
     * 获取存储卡路径
     *
     * @return
     */
    public static File getSDPath() {
        File path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory();
        }
        return path;
    }

    public interface SaveResultCallback {
        void onSavedSuccess();

        void onSavedFailed();
    }
}
