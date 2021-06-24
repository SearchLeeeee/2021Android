package com.example.webviewapp.contract;

import java.util.List;

public interface MainContract {
    interface View {

    }

    interface Presenter {
        void addHistory(String url, String title);

        List<String> getHistory();

        void addLable(String url, String title);
    }
}
