package com.example.webviewapp.data;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.webviewapp.common.utils.DataFormatUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DataManager(Context context) {
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
                .encryptionKey(DataFormatUtils.getRealmKey(DB_KEY))
                .allowWritesOnUiThread(true)
                .build());

        //TODO:测试数据
        for (long i = 0; i < 10; i++) {
            addRecord(new Record(100, i, "test", "IS_HISTORY", "test", IS_HISTORY));
            addRecord(new Record(100, i, "test", "IS_Lable", "test", IS_LABEL));
            addUser(new User(100, 100));
        }
        loadHistories();
        loadLabels();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadHistories() {
        RealmResults<Record> res = realm.where(Record.class)
                .equalTo("isHistory", IS_HISTORY)
                .findAll();
        historyList = realm.copyFromRealm(res);
        historyList.sort(new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return (int) (o1.getTime() - o2.getTime());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadLabels() {
        RealmResults<Record> res = realm.where(Record.class)
                .equalTo("isHistory", IS_LABEL)
                .findAll();
        labelList = realm.copyFromRealm(res);
        labelList.sort(new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return (int) (o1.getTime() - o2.getTime());
            }
        });
    }

    ////////////////////////记录相关/////////////////////////

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
    public Record queryRecordByTitleDetails(String title, String details, int isHistory) {
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
        realm.executeTransaction(realm1 -> {
            record.setPrimaryKey(generatePrimaryKey(Record.class));
            realm.copyToRealmOrUpdate(record);
        });
    }

    public void updateRecord(Record record) {
        realm.executeTransaction(realm1 -> {
            realm.copyToRealmOrUpdate(record);
        });
    }

    /**
     * 根据uid查询书签/历史记录
     *
     * @param uid
     * @return
     */
    public Record queryRecordByUid(long uid) {
        Record res = realm.where(Record.class)
                .equalTo("uid", uid)
                .findFirst();
        return realm.copyFromRealm(res);
    }

    /**
     * 根据主键查询记录
     *
     * @param primaryKey
     * @return
     */
    public Record queryRecordByPrimaryKey(long primaryKey) {
        Record res = realm.where(Record.class)
                .equalTo("primaryKey", primaryKey)
                .findFirst();
        assert res != null;
        return realm.copyFromRealm(res);
    }

    /////////////////////////////记录相关//////////////////////////

    //////////////////////////////////权限相关/////////////////////////////////

    /**
     * 查询已存储的被拒绝权限
     *
     * @param permission
     * @return
     */
    public Permissions queryDeniedPermission(String permission) {
        if (permission == null) {
            return null;
        }
        Permissions res = realm.where(Permissions.class)
                .equalTo("permission", permission)
                .findFirst();
        if (res == null) {
            return null;
        }
        return realm.copyFromRealm(res);
    }

    /**
     * 增加被拒绝权限，时间戳为当下
     *
     * @param permission
     */
    public void addDeniedPermission(String permission) {
        Permissions p = new Permissions(permission, new Date().getTime());
        realm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(p);
        });
    }

    /**
     * 更新被拒绝权限时间戳为当下
     *
     * @param permission
     */
    public void updateDeniedPermission(String permission) {
        addDeniedPermission(permission);
    }

    /**
     * 该权限已获得，从记录中删除
     *
     * @param permission
     */
    public void deleteDeniedPermission(String permission) {
        RealmResults<Permissions> res = realm
                .where(Permissions.class)
                .equalTo("permission", permission)
                .findAll();
        realm.executeTransaction(realm -> {
            res.deleteAllFromRealm();
        });
    }

    //////////////////////////////////权限相关/////////////////////////////////


    //////////////////////////////////用户相关/////////////////////////////////

    /**
     * 根据id查询用户密码
     *
     * @param Uid
     * @return long
     */

    public long queryUserByUid(long Uid) {
        User res = realm.where(User.class).equalTo("uid", Uid).findFirst();
        assert res != null;
        return res.getPassword();
    }

    /**
     * 往数据库写入数据：
     *
     * @param user
     */
    public void addUser(User user) {
        realm.executeTransaction(realm1 -> {
            user.setPrimaryKey(generatePrimaryKey(User.class));
            realm.copyToRealmOrUpdate(user);
        });
    }


    //////////////////////////////////用户相关/////////////////////////////////

}
