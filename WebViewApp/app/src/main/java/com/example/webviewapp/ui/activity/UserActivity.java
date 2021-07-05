package com.example.webviewapp.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.webviewapp.R;
import com.example.webviewapp.databinding.ActivityUserBinding;

public class UserActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.
                findNavController(this, R.id.nav_host_fragment_content_user);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        getSupportActionBar().show();

        binding.toolbar.setNavigationIcon(R.drawable.arrow);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
//        binding.fab.setOnClickListener(view ->
//                Snackbar.make(view, "没有收到讯息", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}