package com.irecipe.view.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.regex.Pattern;

public abstract class BaseFragment extends Fragment {

    KProgressHUD kProgressHUD ;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kProgressHUD = new KProgressHUD(requireContext());
    }

    public void customProgressDialog(Boolean show, String message) {

        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(message).setCancellable(false).setAnimationSpeed(1).setDimAmount(0.7f);

        if (show) {
            kProgressHUD.show();
        } else {
            kProgressHUD.dismiss();
        }
    }


    public void showAlertMessage(String message){
        new MaterialAlertDialogBuilder(requireContext())
                .setMessage(message)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }



    public Boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

}
