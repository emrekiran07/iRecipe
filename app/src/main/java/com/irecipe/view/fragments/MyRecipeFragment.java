package com.irecipe.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.irecipe.R;
import com.irecipe.models.RecipeModel;
import com.irecipe.models.UserModel;
import com.irecipe.utils.Constants;
import com.irecipe.view.adapter.HomeRecipeAdapter;

import java.util.ArrayList;

import io.paperdb.Paper;

import static com.irecipe.utils.Constants.LOGIN_USER;


public class MyRecipeFragment extends BaseFragment implements HomeRecipeAdapter.ItemClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserModel userModel = Paper.book().read(LOGIN_USER);

        RecyclerView myRecipesRecycler = view.findViewById(R.id.my_recipes_recycler);


        ArrayList<RecipeModel> myRecipeList = new ArrayList<>();

        for (RecipeModel recipeModel : Constants.recipeModelArrayList){
            if (recipeModel.getUploadUserEmailId().equalsIgnoreCase(userModel.getUserEmailAddress())){
                myRecipeList.add(recipeModel);
            }
        }
        Constants.myRecipeList = myRecipeList;
        HomeRecipeAdapter homeRecipeAdapter = new HomeRecipeAdapter(myRecipeList, this, false);
        myRecipesRecycler.setAdapter(homeRecipeAdapter);

        view.findViewById(R.id.back_button).setOnClickListener(v->{
            requireActivity().onBackPressed();
        });

    }

    @Override
    public void onItemClick(int position) {
        Navigation.findNavController(requireView()).navigate(MyRecipeFragmentDirections.toRecipeDetails().setPosition(position).setIsMyRecipes(true));
    }

    @Override
    public void likedClicked(RecipeModel recipeModel, Boolean isFlag) {
        if (isFlag) {
            ArrayList<RecipeModel> localList = Paper.book().read(Constants.FAVOURITE_LIST); // favori listeye read ediyorum
            if (localList != null && !localList.isEmpty()) {

                Boolean myFlag = false;
                for (RecipeModel mRecipe : localList) {
                    if (mRecipe.getDishIngredient().equals(recipeModel.getDishIngredient()) && mRecipe.getDishDirection().equals(recipeModel.getDishDirection())) {
                        myFlag = true;
                    }
                }
                if (myFlag) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setMessage("This record already save in Database")
                            .setPositiveButton("Ok", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .show();
                } else {

                    localList.add(recipeModel);
                    Paper.book().write(Constants.FAVOURITE_LIST, localList);

                }
            } else {
                if (localList == null || localList.size() == 0) {
                    ArrayList<RecipeModel> newRecipeList = new ArrayList<>();
                    newRecipeList.add(recipeModel);
                    localList = newRecipeList;
                } else
                    localList.add(recipeModel);
                Paper.book().write(Constants.FAVOURITE_LIST, localList);
            }

            Log.e("Like List Size == > ", String.valueOf(localList.size()));

        } else {

            Log.e("isUnlike == > ", "true");
            ArrayList<RecipeModel> localList = Paper.book().read(Constants.FAVOURITE_LIST);
            ArrayList<RecipeModel> newRecipeList = new ArrayList<>();

            for (RecipeModel mRecipe : localList) {
                if (mRecipe.getDishIngredient().equals(recipeModel.getDishIngredient()) && mRecipe.getDishDirection().equals(recipeModel.getDishDirection())) {
                    Log.e("contains === > ", " true ");
                } else {
                    newRecipeList.add(mRecipe);
                }
            }

            localList = newRecipeList;
            Log.e("newListSize == > ", String.valueOf(localList.size()));
            Paper.book().write(Constants.FAVOURITE_LIST, localList);
            Log.e("listSize == > ", String.valueOf(localList.size()));
        }
    }
}