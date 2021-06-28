package com.example.webviewapp.data.cloud;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.webviewapp.common.base.BaseActivity;
import com.example.webviewapp.data.Record;
import com.example.webviewapp.databinding.ActivityCloudBinding;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLDownloadTask;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static com.example.webviewapp.data.DataManager.IS_HISTORY;

public class CloudActivity extends BaseActivity {
    private static final String TAG = "CloudActivity";
    private final String bucket = "web-view-1301940023"; //存储桶，格式：BucketName-APPID
    public ActivityCloudBinding vb;
    CosXmlService cosXmlService;
    private Context context;
    private String region;

    /**
     * 从文件路径读取字符流
     *
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initCloud();
        initData();
//        transferUploadBytes();
        transferDownloadObject();
    }

    private void initData() {
        try {
            // 写入一个记录对象
            Record record = new Record(100, 1, "test", "IS_HISTORY", "test", IS_HISTORY);
            FileWriter fileWriter = new FileWriter(context.getExternalCacheDir().toString() + "/testLocal.json");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.write(JSON.toJSONString(record));
            fileWriter.close();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCloud() {
        String secretId = "AKID76tTpytd9UZQ3TfeQ1QVQ3RbZR0LRK7d"; // 密钥id SecretId
        String secretKey = "MTM1SffS2NZTM3dz94jDXeXEhwxkJekb"; // 密钥key SecretKey
        QCloudCredentialProvider myCredentialProvider =
                new ShortTimeCredentialProvider(secretId, secretKey, 300);
        region = "ap-guangzhou";
        // 创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        context = CloudActivity.this;
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                .isHttps(true) // 使用 HTTPS 请求, 默认为 HTTP 请求
                .builder();
        // 初始化 COS Service，获取实例
        cosXmlService = new CosXmlService(context, serviceConfig, myCredentialProvider);
    }

    private void transferUploadBytes() {
        // 初始化 TransferConfig，这里使用默认配置，如果需要定制，请参考 SDK 接口文档
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        // 初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService,
                transferConfig);

        String cosPath = "test.json"; //对象在存储桶中的位置标识符，即称对象键
        String srcPath = new File(context.getExternalCacheDir(), "test1Local.json")
                .toString(); //本地文件的绝对路径
        //若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
        String uploadId = null;
        // 上传文件
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath,
                srcPath, uploadId);
        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                // todo Do something to update progress...
            }
        });
        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult =
                        (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                Log.d(TAG, "onSuccess: upload");
            }

            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                Log.d(TAG, "onFail: upload");
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });
        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                // todo notify transfer state
            }
        });
    }

    /**
     * 高级接口下载对象
     */
    private void transferDownloadObject() {
        //.cssg-snippet-body-start:[transfer-download-object]
        // 高级下载接口支持断点续传，所以会在下载前先发起 HEAD 请求获取文件信息。
        // 如果您使用的是临时密钥或者使用子账号访问，请确保权限列表中包含 HeadObject 的权限。

        // 初始化 TransferConfig，这里使用默认配置，如果需要定制，请参考 SDK 接口文档
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService,
                transferConfig);

        String cosPath = "test.json"; //对象在存储桶中的位置标识符，即称对象键
        //本地目录路径
        String savePathDir = context.getExternalCacheDir().toString();
        //本地保存的文件名，若不填（null），则与 COS 上的文件名一样
        String savedFileName = "testFromDownload.json";

        Context applicationContext = context.getApplicationContext(); // application
        // context
        COSXMLDownloadTask cosxmlDownloadTask =
                transferManager.download(applicationContext,
                        bucket, cosPath, savePathDir, savedFileName);

        //设置下载进度回调
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                // todo Do something to update progress...
            }
        });
        //设置返回结果回调
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLDownloadTask.COSXMLDownloadTaskResult downloadTaskResult =
                        (COSXMLDownloadTask.COSXMLDownloadTaskResult) result;
                // 绝对路径读取文件，这些操作可以抽象
                String uri = savePathDir + "/testFromDownload.json";
                String json = readJsonFile(uri);
                // fastjson过程
                JSONObject object = JSON.parseObject(json);
                Record record = new Record();
                record.setDetails(object.getString("details"));
                record.setTitle(object.getString("title"));
                record.setTime(object.getLong("time"));
                record.setPrimaryKey(object.getLong("primaryKey"));
                record.setUid(object.getLong("uid"));
                record.setUrl(object.getString("url"));
                // 更新必须在ui线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vb.text.setText(record.getDetails());
                    }
                });
            }

            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                Log.d(TAG, "onFail: down");
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });
        //设置任务状态回调，可以查看任务过程
        cosxmlDownloadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                // todo notify transfer state
            }
        });
        //.cssg-snippet-body-end
    }

}