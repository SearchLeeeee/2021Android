package com.example.webviewapp.data.cloud;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.webviewapp.common.base.BaseActivity;
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

import java.nio.charset.StandardCharsets;

public class CloudActivity extends BaseActivity {
    private static final String TAG = "CloudActivity";
    private final String bucket = "web-view-1301940023"; //存储桶，格式：BucketName-APPID
    public ActivityCloudBinding vb;
    CosXmlService cosXmlService;
    private Context context;
    private String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String secretId = "AKIDOYSBfwJzmNhlZ8dSGBqHTPneFfzPpU8L"; // 密钥id SecretId
        String secretKey = "GF4dQ5YmpeBXcRzFF4rU4LQQBgv02tX8"; // 密钥key SecretKey
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

//        transferUploadBytes();
        transferDownloadObject();
    }

    private void transferUploadBytes() {
        //.cssg-snippet-body-start:[transfer-upload-bytes]
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        TransferManager transferManager = new TransferManager(cosXmlService,
                transferConfig);

        String cosPath = "exampleobject"; //对象在存储桶中的位置标识符，即称对象键

        // 上传字节数组
        byte[] bytes = "this is a test".getBytes(StandardCharsets.UTF_8);
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucket, cosPath,
                bytes);

        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult =
                        (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                Log.d(TAG, "onSuccess: up");
            }

            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                Log.d(TAG, "onFail: up");
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });
        //.cssg-snippet-body-end
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

        String cosPath = "share.svg"; //对象在存储桶中的位置标识符，即称对象键
        //本地目录路径
        String savePathDir = context.getExternalCacheDir().toString();
        //本地保存的文件名，若不填（null），则与 COS 上的文件名一样
        String savedFileName = "exampleobject";

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
                Log.d(TAG, "onSuccess: down" + result.toString());
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