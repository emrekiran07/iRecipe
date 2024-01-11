package com.irecipe.models;

import android.net.Uri;

public class RecipeModel {

    private String dishName;
    private String dishIngredient;
    private String dishDirection;
    private String dishUploadedImage;
    private Uri dishUploadImage;
    private String uploadedUserName;
    private Boolean isFavouriteRecipe = false;
    private String documentId;
    private String uploadUserEmailId;


    public RecipeModel(String uploadedUserName, String dishName, String dishIngredient, String dishDirection, String dishImage) {
        this.uploadedUserName = uploadedUserName;
        this.dishName = dishName;
        this.dishIngredient = dishIngredient;
        this.dishDirection = dishDirection;
        this.dishUploadedImage = dishImage;
    }

    public RecipeModel(String uploadedUserName, String dishName, String dishIngredient, String dishDirection, Uri dishUploadImage) {
        this.uploadedUserName = uploadedUserName;
        this.dishName = dishName;
        this.dishIngredient = dishIngredient;
        this.dishDirection = dishDirection;
        this.dishUploadImage = dishUploadImage;
    }

    public RecipeModel(String uploadedUserName, String dishName, String dishIngredient, String dishDirection) {
        this.uploadedUserName = uploadedUserName;
        this.dishName = dishName;
        this.dishIngredient = dishIngredient;
        this.dishDirection = dishDirection;
    }

    public String getDishName() {
        return dishName;
    }


    public String getDishIngredient() {
        return dishIngredient;
    }


    public String getDishDirection() {
        return dishDirection;
    }


    public String getDishUploadedImage() {
        return dishUploadedImage;
    }


    public Uri getDishUploadImage() {
        return dishUploadImage;
    }

    public String getUploadedUserName() {
        return uploadedUserName;
    }

    public Boolean getFavouriteRecipe() {
        return isFavouriteRecipe;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUploadUserEmailId() {
        return uploadUserEmailId;
    }

    public void setUploadUserEmailId(String uploadUserEmailId) {
        this.uploadUserEmailId = uploadUserEmailId;
    }

    public void setFavouriteRecipe(Boolean favouriteRecipe) {
        isFavouriteRecipe = favouriteRecipe;
    }
}
