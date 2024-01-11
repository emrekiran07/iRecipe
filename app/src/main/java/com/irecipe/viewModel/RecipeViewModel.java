package com.irecipe.viewModel;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.irecipe.models.RecipeModel;
import com.irecipe.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.paperdb.Paper;

public class RecipeViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> isRecipeUploaded = new MutableLiveData();
    public MutableLiveData<Boolean> isEditRecipe = new MutableLiveData();
    public MutableLiveData<ArrayList<RecipeModel>> recipeList = new MutableLiveData();
    public MutableLiveData<Boolean> isDeletedRecipe = new MutableLiveData<>();

    public void updateRecipe(RecipeModel recipeModel) {
        showProgressBar(true);

        HashMap<String, String> recipeMap = new  HashMap();
        recipeMap.put("DishName", recipeModel.getDishName());
        recipeMap.put("DishIngredients", recipeModel.getDishIngredient());
        recipeMap.put("DishDirection", recipeModel.getDishDirection());
        recipeMap.put("RecipeUserName", recipeModel.getUploadedUserName());
        recipeMap.put("UploadedUserEmailAddress", recipeModel.getUploadUserEmailId());


        if (recipeModel.getDishUploadImage() != null){
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(recipeModel.getDishUploadImage())
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.e("taskSnapShot == > ", taskSnapshot.toString());
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                        firebaseUri.addOnSuccessListener(uri -> {
                            String mDownloadUri = uri.toString();
                            recipeMap.put("RecipeImage", mDownloadUri);
                            dataBase.collection("Recipes").add(recipeMap)
                                    .addOnCompleteListener(recipe -> {
                                        if (recipe.isSuccessful()) {
                                            showProgressBar(false);
                                            isRecipeUploaded.setValue(true);
                                        } else {
                                            showProgressBar(false);
                                            showAlertMessage(Objects.requireNonNull(recipe.getException()).getMessage());
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
            recipeMap.put("RecipeImage", "");
            dataBase.collection("Recipes").add(recipeMap)
                    .addOnCompleteListener(recipe -> {
                        if (recipe.isSuccessful()) {
                            showProgressBar(false);
                            isRecipeUploaded.setValue(true);
                        } else {
                            showProgressBar(false);
                            showAlertMessage(Objects.requireNonNull(recipe.getException()).getMessage());
                        }
                    });
        }


    }

    public void editRecipe(RecipeModel recipeModel){
        showProgressBar(true);
        HashMap<String, Object> recipeMap = new  HashMap();
        recipeMap.put("DishName", recipeModel.getDishName());
        recipeMap.put("DishIngredients", recipeModel.getDishIngredient());
        recipeMap.put("DishDirection", recipeModel.getDishDirection());
        recipeMap.put("RecipeUserName", recipeModel.getUploadedUserName());
        recipeMap.put("UploadedUserEmailAddress", recipeModel.getUploadUserEmailId());


        if (recipeModel.getDishUploadImage() != null){
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(recipeModel.getDishUploadImage())
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.e("taskSnapShot == > ", taskSnapshot.toString());
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                        firebaseUri.addOnSuccessListener(uri -> {
                            String mDownloadUri = uri.toString();
                            recipeMap.put("RecipeImage", mDownloadUri);
                            dataBase.collection("Recipes").document(recipeModel.getDocumentId()).update(recipeMap)
                                    .addOnCompleteListener(recipe -> {
                                        if (recipe.isSuccessful()) {
                                            showProgressBar(false);
                                            isEditRecipe.setValue(true);
                                        } else {
                                            showProgressBar(false);
                                            showAlertMessage(Objects.requireNonNull(recipe.getException()).getMessage());
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
            dataBase.collection("Recipes").document(recipeModel.getDocumentId()).update(recipeMap)
                    .addOnCompleteListener(recipe -> {
                        if (recipe.isSuccessful()) {
                            showProgressBar(false);
                            isEditRecipe.setValue(true);
                        } else {
                            showProgressBar(false);
                            showAlertMessage(Objects.requireNonNull(recipe.getException()).getMessage());
                        }
                    });
        }

    }

    public void getAllRecipes(){
        showProgressBar(true);
        dataBase.collection("Recipes").get()
                .addOnCompleteListener(recipe -> {
                    if (recipe.isSuccessful()) {
                        List<DocumentSnapshot> documentSnapShotList =  Objects.requireNonNull(recipe.getResult()).getDocuments();
                        ArrayList<RecipeModel> localRecipeList = new ArrayList<>();
                        for (DocumentSnapshot document : documentSnapShotList){
                            String documentId = document.getId();
                            String dishName = document.getString("DishName");
                            String dishIngredient = document.getString("DishIngredients");
                            String dishDirection = document.getString("DishDirection");
                            String dishImage = document.getString("RecipeImage");
                            String userName = document.getString("RecipeUserName");
                            String userEmailAddress = document.getString("UploadedUserEmailAddress");

                            RecipeModel newRecipeModel = new RecipeModel(userName, dishName, dishIngredient, dishDirection, dishImage);
                            newRecipeModel.setDocumentId(documentId);
                            newRecipeModel.setUploadUserEmailId(userEmailAddress);
                            localRecipeList.add(newRecipeModel);
                        }
                        showProgressBar(false);
                        recipeList.setValue(localRecipeList);
                    } else {
                        showProgressBar(false);
                        showAlertMessage(Objects.requireNonNull(recipe.getException()).getMessage());
                    }
                });
    }
    RecipeModel mFavoriteModel;

    public void deleteRecipe(RecipeModel recipeModel){
        showProgressBar(true);

        dataBase.collection("Recipes").document(recipeModel.getDocumentId()).delete().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               ArrayList<RecipeModel> localList = Paper.book().read(Constants.FAVOURITE_LIST);
               for (RecipeModel favoriteModel : localList){
                   if (favoriteModel.getDocumentId().equalsIgnoreCase(recipeModel.getDocumentId())){
                       mFavoriteModel = favoriteModel;
                   }
               }
               if (mFavoriteModel != null){
                   localList.remove(mFavoriteModel);
                   Paper.book().write(Constants.FAVOURITE_LIST, localList);
               }
               showProgressBar(false);
               isDeletedRecipe.setValue(true);
           }else {
               showProgressBar(false);
               showAlertMessage(Objects.requireNonNull(task.getException()).getMessage());
           }
        });
    }
}
