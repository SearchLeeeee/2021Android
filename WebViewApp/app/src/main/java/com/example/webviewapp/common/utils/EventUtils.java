package com.example.webviewapp.common.utils;

import org.greenrobot.eventbus.EventBus;

/**
 * 简化注册注销以及发送事件操作
 * 存放事件类
 */
public class EventUtils {

    public static <T> void register(T object) {
        EventBus.getDefault().register(object);
    }

    public static <T> void unregister(T object) {
        EventBus.getDefault().unregister(object);
    }

    public static <T> void post(T event) {
        EventBus.getDefault().post(event);
    }

    public static class NewsDataChangeEvent {
    }

    public static class TimeEvent {
    }

    public static class UserEvent {
        public String email;
        public int avatarId;

        public UserEvent(String email, int avatarId) {
            this.email = email;
            this.avatarId = avatarId;
        }
    }
}
