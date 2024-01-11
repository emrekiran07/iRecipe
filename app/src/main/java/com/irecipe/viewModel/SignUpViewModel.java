package com.irecipe.viewModel;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.irecipe.models.UserModel;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SignUpViewModel extends BaseViewModel {

    public MutableLiveData<UserModel> signUpUser = new MutableLiveData();

    public void createUser(UserModel userModel) {
        showProgressBar(true);

        HashMap<String, String> user = new HashMap();

        user.put("Email", userModel.getUserEmailAddress());
        user.put("Password", userModel.getUserPassword());
        user.put("Username", userModel.getUserName());
        user.put("UserPhoneNumber", userModel.getUserPhoneNumber());
        user.put("UserGender", userModel.getUserGender());
        user.put("UserAbout", userModel.getAboutUs());

        dataBase.collection("Users").whereEqualTo("Email", userModel.getUserEmailAddress()).get().addOnCompleteListener(getEmailTask -> {
            if (getEmailTask.isSuccessful()) {

                List<DocumentSnapshot> documentSnapShot = getEmailTask.getResult().getDocuments();
                if (documentSnapShot.isEmpty()) {
                    if (userModel.getUserImage() != null) {
                        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                        ref.putFile(userModel.getUserImage())
                                .addOnSuccessListener(taskSnapshot -> {
                                    Log.e("taskSnapShot == > ", taskSnapshot.toString());
                                    final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                                    firebaseUri.addOnSuccessListener(uri -> {
                                        String mDownloadUri = uri.toString();
                                        user.put("UserImage", mDownloadUri);
                                        dataBase.collection("Users").add(user)
                                                .addOnCompleteListener(login -> {
                                                    if (login.isSuccessful()) {
                                                        showProgressBar(false);
                                                        UserModel signUpSuccessfullyUser = new UserModel(userModel.getUserName(), userModel.getUserEmailAddress(), userModel.getUserPhoneNumber(), userModel.getUserPassword(),userModel.getUserGender(), userModel.getAboutUs(), mDownloadUri);
                                                        signUpUser.setValue(signUpSuccessfullyUser);
                                                    } else {
                                                        showProgressBar(false);
                                                        showAlertMessage(login.getException().getMessage());
                                                    }
                                                });
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    showProgressBar(false);
                                    showAlertMessage(e.getMessage());
                                })
                                .addOnProgressListener(taskSnapshot -> {

                                });
                    }else{
                        dataBase.collection("Users").add(user)
                                .addOnCompleteListener(login -> {
                                    if (login.isSuccessful()) {
                                        showProgressBar(false);
                                        UserModel signUpSuccessfullyUser = new UserModel(userModel.getUserName(), userModel.getUserEmailAddress(), userModel.getUserPhoneNumber(), userModel.getUserPassword(), userModel.getUserGender(), userModel.getAboutUs());
                                        signUpUser.setValue(signUpSuccessfullyUser);
                                    } else {
                                        showProgressBar(false);
                                        showAlertMessage(login.getException().getMessage());
                                    }
                                });
                    }


                } else {
                    showAlertMessage("This Email already exist!!!");
                    showProgressBar(false);

                }
            } else {
                showAlertMessage(getEmailTask.getException().getMessage());
                showProgressBar(false);
            }

        });


    }


}
