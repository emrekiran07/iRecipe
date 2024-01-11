package com.irecipe.models;

import android.net.Uri;

public class UserModel {
    private String userName;
    private String userEmailAddress;
    private String userPhoneNumber;
    private String userPassword;
    private String userGender;
    private Uri userImage;
    private String uploadedImage;
    private Boolean rememberMe;
    private String aboutUs;

    public UserModel(){

    }
    public UserModel(String userName, String userEmailAddress, String userPhoneNumber, String userPassword,String userGender, String aboutUs, Uri userImage) {
        this.userName = userName;
        this.userEmailAddress = userEmailAddress;
        this.userPhoneNumber = userPhoneNumber;
        this.userPassword = userPassword;
        this.userGender = userGender;
        this.aboutUs = aboutUs;
        this.userImage = userImage;
    }

    public UserModel(String userName, String userEmailAddress, String userPhoneNumber, String userPassword,String userGender, String aboutUs, String userImage) {
        this.userName = userName;
        this.userEmailAddress = userEmailAddress;
        this.userPhoneNumber = userPhoneNumber;
        this.userPassword = userPassword;
        this.userGender = userGender;
        this.aboutUs = aboutUs;
        this.uploadedImage = userImage;
    }

    public UserModel(String userName, String userEmailAddress, String userPhoneNumber, String userPassword,String userGender, String aboutUs) {
        this.userName = userName;
        this.userEmailAddress = userEmailAddress;
        this.userPhoneNumber = userPhoneNumber;
        this.userPassword = userPassword;
        this.userGender = userGender;
        this.aboutUs = aboutUs;
    }

    public UserModel(String userName, String userEmailAddress, String userPhoneNumber, String userPassword,String userGender, String aboutUs, String userImage, Boolean rememberMe) {
        this.userName = userName;
        this.userEmailAddress = userEmailAddress;
        this.userPhoneNumber = userPhoneNumber;
        this.userPassword = userPassword;
        this.userGender = userGender;
        this.aboutUs = aboutUs;
        this.uploadedImage = userImage;
        this.rememberMe = rememberMe;
    }

    public UserModel(String userName, String userEmailAddress, String userPhoneNumber,String userGender, String aboutUs,Uri userImage) {
        this.userName = userName;
        this.userEmailAddress = userEmailAddress;
        this.userPhoneNumber = userPhoneNumber;
        this.userImage = userImage;
        this.userGender = userGender;
        this.aboutUs = aboutUs;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmailAddress() {
        return userEmailAddress;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Uri getUserImage() {
        return userImage;
    }

    public void setUserImage(Uri userImage) {
        this.userImage = userImage;
    }

    public String getUploadedImage() {
        return uploadedImage;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public void setUploadedImage(String uploadedImage) {
        this.uploadedImage = uploadedImage;
    }
}
