package com.irecipe.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import com.irecipe.R;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        navHostFragment = (NavHostFragment)getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment_id);
//        NavInflater inflater =  navHostFragment.getNavController().getNavInflater();
//


    }
}