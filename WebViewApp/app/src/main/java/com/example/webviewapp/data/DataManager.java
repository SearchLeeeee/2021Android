package com.example.webviewapp.data;

import android.content.Context;

import com.example.webviewapp.common.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DataManager {
    private static final String DB_SPORT = "project.realm";//数据库名
    private static final String DB_KEY = "0123456789012345";//秘钥
    public static final int IS_HISTORY = 1;
    public static final int IS_LABEL = 2;

    private volatile static DataManager instance;

    public static DataManager get() {
        return instance;
    }

    public static void init(Context context) {
        instance = new DataManager(context);
    }

    Realm realm;
    Context context;
    public List<Record> historyList = new ArrayList<>();
    public List<Record> labelList = new ArrayList<>();

    DataManager(Context context) {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = this;
                }
            }
        }
        this.context = context;

        Realm.init(context);

        realm = Realm.getInstance(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(2)
                .name(DB_SPORT)
                .encryptionKey(DataUtils.getRealmKey(DB_KEY))
                .allowWritesOnUiThread(true)
                .build());

        //TODO:测试数据
        for (long i = 0; i < 10; i++) {
            addRecord(new Record(100, i, "test", "test", "test", IS_HISTORY));
            addRecord(new Record(100, i, "test", "test", "test", IS_LABEL));
        }
        loadHistories();
        loadLabels();
    }

    private void loadHistories() {
        RealmResults<Record> res = realm.where(Record.class)
                .equalTo("isHistory", IS_HISTORY)
                .findAll();
        historyList = realm.copyFromRealm(res);
    }

    private void loadLabels() {
        RealmResults<Record> res = realm.where(Record.class)
                .equalTo("isHistory", IS_LABEL)
                .findAll();
        labelList = realm.copyFromRealm(res);
    }

    /**
     * 获取最大的PrimaryKey并加一，否则id不变，会覆盖已有记录
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private <T extends RealmObject> long generatePrimaryKey(Class<T> clazz) {
        RealmResults<T> results = realm.where(clazz).findAll();
        if (results == null) return 1;
        return results.size() + 1;
    }

    // endregion

    /**
     * 删掉所有的书签/历史记录
     *
     * @param isHistory
     */
    public void deleteAllRecords(int isHistory) {
        RealmResults<Record> res = realm.where(Record.class)
                .equalTo("isHistory", isHistory)
                .findAll();
        realm.executeTransaction(realm1 -> {
            res.deleteAllFromRealm();
        });
    }

    /**
     * 根据标题、详情查询书签/历史记录
     *
     * @param title
     * @param details
     * @param isHistory
     * @return
     */
    public Record queryRecord(String title, String details, int isHistory) {
        Record res = realm.where(Record.class)
                .equalTo("title", title)
                .equalTo("details", details)
                .equalTo("isHistory", isHistory)
                .findFirst();
        return realm.copyFromRealm(res);
    }

    /**
     * 往数据库中添加记录
     *
     * @param record
     */
    public void addRecord(Record record) {
        realm.copyToRealmOrUpdate(record);
    }

    public Record queryRecordByUid(long uid) {
        Record res = realm.where(Record.class)
                .equalTo("uid", uid)
                .findFirst();
        return realm.copyFromRealm(res);
    }
}
