package com.irecipe.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.irecipe.models.UserModel;

import java.util.List;
import java.util.Objects;

public class LoginViewModel extends BaseViewModel {
    public MutableLiveData<UserModel> loginUser = new MutableLiveData();

    public void userLogin(String emailAddress, String password, Boolean rememberMe) {
        showProgressBar(true);

        dataBase.collection("Users").whereEqualTo("Email", emailAddress).get()
                .addOnCompleteListener(userLogin -> {

                    showProgressBar(false);

                    if (userLogin.isSuccessful()) {
                        showProgressBar(false);
                        List<DocumentSnapshot> documentSnapShot = Objects.requireNonNull(userLogin.getResult()).getDocuments();
                        if (!documentSnapShot.isEmpty()) {
                            for (int i = 0; i < documentSnapShot.size(); i++) {
                                DocumentSnapshot document = documentSnapShot.get(i);
                                if (Objects.equals(document.get("Password"), password)) {
                                    String userEmail = Objects.requireNonNull(document.get("Email")).toString();
                                    String userPassword = Objects.requireNonNull(document.get("Password")).toString();
                                    String userGender = Objects.requireNonNull(document.get("UserGender")).toString();
                                    String userName = Objects.requireNonNull(document.get("Username")).toString();
                                    String userAbout = Objects.requireNonNull(document.get("UserAbout")).toString();
                                    String userImage = "";
                                    if (document.get("UserImage") != null) {
                                        userImage = Objects.requireNonNull(document.get("UserImage")).toString();
                                    }
                                    String userPhoneNumber = Objects.requireNonNull(document.get("UserPhoneNumber")).toString();
                                    UserModel loginUserModel = new UserModel(userName, userEmail, userPhoneNumber, userPassword, userGender, userAbout, userImage, rememberMe);
                                    loginUser.setValue(loginUserModel);
                                 } else
                                    showAlertMessage("Your password is not correct");
                            }
                        } else {
                            showAlertMessage("Your email is not exist");
                        }

                    } else {
                        showProgressBar(false);
                        showAlertMessage(userLogin.getException().getMessage());
                    }

                });

    }

}
