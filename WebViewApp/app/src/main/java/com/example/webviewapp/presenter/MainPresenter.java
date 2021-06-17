package com.example.webviewapp.presenter;

import com.example.webviewapp.contract.MainContract;

public class MainPresenter implements MainContract.Presenter {
    private final MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }
}
