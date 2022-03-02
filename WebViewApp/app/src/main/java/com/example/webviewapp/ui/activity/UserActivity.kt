package com.example.webviewapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.webviewapp.R
import com.example.webviewapp.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private var appBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityUserBinding? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(getLayoutInflater())
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user)
        appBarConfiguration = Builder(navController.getGraph()).build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        getSupportActionBar().show()
        binding!!.toolbar.setNavigationIcon(R.drawable.arrow)
        binding!!.toolbar.setNavigationOnClickListener { v -> finish() }
        //        binding.fab.setOnClickListener(view ->
//                Snackbar.make(view, "没有收到讯息", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show());
    }

    fun onSupportNavigateUp(): Boolean {
        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user)
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}