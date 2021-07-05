package com.example.webviewapp.common.utils.Cloud;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.webviewapp.common.base.BaseApplication;
import com.example.webviewapp.common.utils.DataFormatUtils;
import com.example.webviewapp.common.utils.EventUtils;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.data.User;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLDownloadTask;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CloudUser {
    private static final String TAG = "CloudAddUser";
    private volatile static CloudUser instance;

    private final String bucket = "webview-1306366413"; //存储桶，格式：BucketName-APPID
    private final String SECRET_ID = ""; // 密钥id SecretId
    private final String SECRET_KEY = ""; // 密钥key SecretKey
    private final String REGION = "ap-guangzhou";
    private final Context context;
    private String savePathDir;
    private TransferManager transferManager;

    public CloudUser(Context context) {
        this.context = context;
        initCloud();
    }

    /**
     * @return 云存储对象的单例
     */
    public static CloudUser get() {
        if (instance == null) {
            synchronized (CloudUser.class) {
                if (instance == null) {
                    instance = new CloudUser(BaseApplication.getInstance());
                }
            }
        }
        return instance;
    }

    private void initCloud() {
        savePathDir = context.getExternalCacheDir().toString();
        QCloudCredentialProvider myCredentialProvider =
                new ShortTimeCredentialProvider(SECRET_ID, SECRET_KEY, 300);
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(REGION)
                .isHttps(true) // 使用 HTTPS 请求, 默认为 HTTP 请求
                .builder();
        CosXmlService cosXmlService = new CosXmlService(context, serviceConfig, myCredentialProvider);
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        transferManager = new TransferManager(cosXmlService, transferConfig);
    }

    /**
     * 读取本地数据写入本地的json文件中，以供上传到云端
     * 这里有设计上的疏忽，多了从本地数据库写到本地json这一步
     *
     * @param fileName
     * @param object
     * @param <T>
     */
    private <T> void writeObject2JSON(String fileName, T object) {
        try {
            FileWriter fileWriter = new FileWriter(context.getExternalCacheDir().toString() +
                    "/" + fileName + ".json");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.write(JSON.toJSONString(object));
            fileWriter.close();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传用户、更新用户信息
     *
     * @param uid
     * @param user
     */
    public void uploadUser(String uid, User user) {
        String fileName = uid + "user";
        writeObject2JSON(fileName, user);

        String srcPath = new File(context.getExternalCacheDir(), fileName + ".json")
                .toString();
        String uploadId = null;
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, fileName, srcPath, uploadId);

        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
            }

            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据uid获取User对象
     */
    public User getUserCloud(String uid) {
        String fileName = uid + "user.json";
        User user = new User();
        String cosPath = uid + "user";

        COSXMLDownloadTask cosxmlDownloadTask =
                transferManager.download(context, bucket, cosPath, savePathDir, fileName);

        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                String uri = savePathDir + "/" + fileName;
                String json = DataFormatUtils.readJsonFile(uri);
                JSONObject object = JSON.parseObject(json);
                user.setEmail(object.getString("email"));
                user.setAvatarId(object.getInteger("avatarId"));
                EventUtils.post(new EventUtils.UserEvent(user.getEmail(), user.getAvatarId()));
            }

            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });
        return user;
    }

    /**
     * 上传用户历史书签记录
     *
     * @param uid     用户uid
     * @param records 对应用户的记书签历史记录
     */
    public void uploadRecord(String uid, List<Record> records) {
        String fileName = uid + "records";
        writeObject2JSON(fileName, records);

        String srcPath = new File(context.getExternalCacheDir(), fileName + ".json")
                .toString();
        String uploadId = null;
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, fileName, srcPath, uploadId);

        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
            }

            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });
    }

    /**
     * @param uid 用户uid
     * @return 对应用户的记书签历史记录
     */
    public List<Record> getRecordsCloud(String uid) {
        String fileName = uid + "records.json";
        List<Record> records = new ArrayList<>();
        String cosPath = uid + "user";

        COSXMLDownloadTask cosxmlDownloadTask =
                transferManager.download(context, bucket, cosPath, savePathDir, fileName);

        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                String uri = savePathDir + "/" + fileName;
                String json = DataFormatUtils.readJsonFile(uri);
                JSONArray array = JSON.parseArray(json);
                Record record = new Record();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    record.setUrl(jsonObject.getString("url"));
                    record.setUid(jsonObject.getLong("uid"));
                    record.setPrimaryKey(jsonObject.getLong("primaryKay"));
                    record.setTime(jsonObject.getLong("time"));
                    record.setTitle(jsonObject.getString("title"));
                    record.setIsHistory(jsonObject.getInteger("isHistory"));
                    record.setDetails(jsonObject.getString("details"));
                    records.add(record);
                }
            }

            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });
        return records;
    }
}
