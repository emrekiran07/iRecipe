package com.irecipe.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irecipe.R;
import com.irecipe.models.UserModel;

import io.paperdb.Paper;

import static com.irecipe.utils.Constants.LOGIN_USER;


public class SplashFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            UserModel userModel = Paper.book().read(LOGIN_USER);
            if (userModel != null && userModel.getUserName() != null && userModel.getRememberMe()) {
                Navigation.findNavController(requireView()).navigate(SplashFragmentDirections.toHomeFragment());
            } else {
                Paper.book().write(LOGIN_USER, new UserModel());
                Navigation.findNavController(requireView()).navigate(SplashFragmentDirections.toLoginFrag());
            }
        }, 3500);

    }
}