package com.example.webviewapp.data;

import android.content.Context;

import com.example.webviewapp.common.utils.DataUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DataManager {
    private static final String DB_SPORT = "project.realm";//数据库名
    private static final String DB_KEY = "0123456789012345";//秘钥

    static DataManager instance;

    public static DataManager get() {
        return instance;
    }

    public static void init(Context context) {
        instance = new DataManager(context);
    }

    Realm examRealm;
    Context context;

    DataManager(Context context) {
        if (instance == null) instance = this;
        this.context = context;

        Realm.init(context);

        examRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(2)
                .name(DB_SPORT)
                .encryptionKey(DataUtils.getRealmKey(DB_KEY))
                .allowWritesOnUiThread(true)
                .build());
    }

    // 获取最大的PrimaryKey并加一，否则id不变，会覆盖已有记录
    private <T extends RealmObject> long generatePk(Class<T> clazz) {
        RealmResults<T> results = examRealm.where(clazz).findAll();
        if (results == null) return 1;
        return results.size() + 1;
    }

    // endregion
}
