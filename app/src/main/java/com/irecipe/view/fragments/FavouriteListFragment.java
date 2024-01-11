package com.irecipe.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.irecipe.R;
import com.irecipe.models.RecipeModel;
import com.irecipe.utils.Constants;
import com.irecipe.view.adapter.HomeRecipeAdapter;

import java.util.ArrayList;

import io.paperdb.Paper;


public class FavouriteListFragment extends BaseFragment implements HomeRecipeAdapter.ItemClickListener {

    ArrayList<RecipeModel> newFavouriteList;
    HomeRecipeAdapter favouriteRecipeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        ArrayList<RecipeModel> localList = Paper.book().read(Constants.FAVOURITE_LIST);

        newFavouriteList = new ArrayList<>();
        if (localList != null)
            newFavouriteList = localList;
        favouriteRecipeAdapter = new HomeRecipeAdapter(newFavouriteList, this, true);
        Constants.favouriteRecipeModelArrayList = newFavouriteList;
        ((RecyclerView) view.findViewById(R.id.favourite_recycler)).setAdapter(favouriteRecipeAdapter);

    }

    @Override
    public void onItemClick(int position) {
        Navigation.findNavController(requireView()).navigate(FavouriteListFragmentDirections.toRecipeDetailFragment().setPosition(position).setIsFromFavourite(true));
    }

    @Override
    public void likedClicked(RecipeModel recipeModel, Boolean isFlag) {
        if (!isFlag) {
            newFavouriteList.remove(recipeModel);
            favouriteRecipeAdapter.notifyDataSetChanged();

            ArrayList<RecipeModel> localList = Paper.book().read(Constants.FAVOURITE_LIST);
            ArrayList<RecipeModel> newRecipeList = new ArrayList<>();

            for (RecipeModel mRecipe : localList) {
                if (mRecipe.getDishIngredient().equals(recipeModel.getDishIngredient()) && mRecipe.getDishDirection().equals(recipeModel.getDishDirection())) {
                } else {
                    newRecipeList.add(mRecipe);
                }
            }

            localList = newRecipeList;
            Constants.favouriteRecipeModelArrayList = newFavouriteList;

            Paper.book().write(Constants.FAVOURITE_LIST, localList);
        }
    }
}