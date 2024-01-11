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
import java.util.Objects;
import java.util.UUID;

public class ProfileViewModel extends BaseViewModel {

    public MutableLiveData<UserModel> userProfile = new MutableLiveData();

    public MutableLiveData<UserModel> isUpdatedUser = new MutableLiveData<>();

    public void getUserProfile(String emailAddress) {
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
                                UserModel loginUserModel = new UserModel(userName, userEmail, userPhoneNumber, userPassword, userGender, userAbout, userImage);
                                userProfile.setValue(loginUserModel);
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


    public void updateUser(UserModel userModel, Boolean isProfileImageUpdated){
        showProgressBar(true);

        HashMap<String, Object> user = new HashMap();

        user.put("Email", userModel.getUserEmailAddress());
        user.put("Password", userModel.getUserPassword());
        user.put("Username", userModel.getUserName());
        user.put("UserPhoneNumber", userModel.getUserPhoneNumber());
        user.put("UserGender", userModel.getUserGender());
        user.put("UserAbout", userModel.getAboutUs());

        dataBase.collection("Users").whereEqualTo("Email", userModel.getUserEmailAddress()).get().addOnCompleteListener(getEmailTask -> {
            if (getEmailTask.isSuccessful()) {
                List<DocumentSnapshot> documentSnapShot = getEmailTask.getResult().getDocuments();
                if (isProfileImageUpdated){
                    StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                    ref.putFile(userModel.getUserImage())
                            .addOnSuccessListener(taskSnapshot -> {
                                Log.e("taskSnapShot == > ", taskSnapshot.toString());
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                                firebaseUri.addOnSuccessListener(uri -> {
                                    String mDownloadUri = uri.toString();
                                    user.put("UserImage", mDownloadUri);
                                    for (DocumentSnapshot document : documentSnapShot) {
                                        String id = document.getId();
                                        dataBase.collection("Users").document(id).update(user).addOnCompleteListener(updatedUser->{
                                            if (updatedUser.isSuccessful()){
                                                showProgressBar(false);
                                                UserModel updatedProfileUser = new UserModel(userModel.getUserName(), userModel.getUserEmailAddress(), userModel.getUserPhoneNumber(), userModel.getUserPassword(),userModel.getUserGender(), userModel.getAboutUs(), mDownloadUri);

                                                isUpdatedUser.setValue(updatedProfileUser);
                                            }
                                        });
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                showProgressBar(false);
                                showAlertMessage(e.getMessage());
                            })
                            .addOnProgressListener(taskSnapshot -> {

                            });
                }else{
                    for (DocumentSnapshot document : documentSnapShot) {
                        String id = document.getId();
                        dataBase.collection("Users").document(id).update(user).addOnCompleteListener(updatedUser->{
                            if (updatedUser.isSuccessful()){
                                showProgressBar(false);

                                UserModel updatedProfileUser = new UserModel(userModel.getUserName(), userModel.getUserEmailAddress(), userModel.getUserPhoneNumber(), userModel.getUserPassword(), userModel.getUserGender(), userModel.getAboutUs());

                                isUpdatedUser.setValue(updatedProfileUser);
                            }
                        });
                    }
                }
            }
        });
    }
}
