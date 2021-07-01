package com.example.webviewapp.common.utils;

import org.greenrobot.eventbus.EventBus;

public class EventUtils {

    public static void register(Object object) {
        EventBus.getDefault().register(object);
    }

    public static void unregister(Object object) {
        EventBus.getDefault().unregister(object);
    }

    public static <T> void post(T event) {
        EventBus.getDefault().post(event);
    }

    public static class NewsDataChangeEvent {
    }
}
