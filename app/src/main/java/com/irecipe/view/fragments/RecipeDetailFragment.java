package com.irecipe.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.irecipe.R;
import com.irecipe.models.RecipeModel;
import com.irecipe.models.UserModel;
import com.irecipe.utils.Constants;
import com.irecipe.viewModel.RecipeViewModel;

import io.paperdb.Paper;

import static com.irecipe.utils.Constants.LOGIN_USER;


public class RecipeDetailFragment extends BaseFragment {

    RecipeModel recipeModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back_button).setOnClickListener(v -> requireActivity().onBackPressed());
        ImageView dishImage = view.findViewById(R.id.dish_image);
        TextView uploadedUserName = view.findViewById(R.id.uploaded_user_name);

        assert getArguments() != null;
        int position = (int) getArguments().get("position");
        boolean isFromFavourite = (boolean) getArguments().get("is_from_favourite");
        boolean isMyRecipe = (boolean) getArguments().get("is_my_recipes");


        if (!isFromFavourite)
            recipeModel = Constants.recipeModelArrayList.get(position);
        else
            recipeModel = Constants.favouriteRecipeModelArrayList.get(position);

        if (isMyRecipe)
            recipeModel = Constants.myRecipeList.get(position);

        UserModel userModel = Paper.book().read(LOGIN_USER);

        RecipeViewModel recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        recipeViewModel.progressBar.observe(getViewLifecycleOwner(), show -> customProgressDialog(show, "Please Wait..."));
        recipeViewModel.alertMessage.observe(getViewLifecycleOwner(), this::showAlertMessage);
        recipeViewModel.isDeletedRecipe.observe(getViewLifecycleOwner(), isDelete -> {
            if (isDelete) {
                requireActivity().onBackPressed();
            }
        });

        if (recipeModel.getDishUploadedImage().isEmpty())
            dishImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.background_image));
        else
            Glide.with(requireContext()).load(recipeModel.getDishUploadedImage()).into(dishImage);

        uploadedUserName.setText(getString(R.string.uploaded_by) + " " + recipeModel.getUploadedUserName());
        ((TextView) view.findViewById(R.id.dish_name)).setText(getString(R.string.dish_name) + ": " + recipeModel.getDishName());
        ((TextView) view.findViewById(R.id.ingredients_name)).setText(getString(R.string.ingredients) + ": \n" + recipeModel.getDishIngredient());
        ((TextView) view.findViewById(R.id.recipe_direction)).setText(getString(R.string.directions) + ": \n" + recipeModel.getDishDirection());
        if (recipeModel.getUploadUserEmailId().equalsIgnoreCase(userModel.getUserEmailAddress()))
            ((LinearLayout) view.findViewById(R.id.edit_del_layout)).setVisibility(View.VISIBLE);
        else
            ((LinearLayout) view.findViewById(R.id.edit_del_layout)).setVisibility(View.GONE);


        view.findViewById(R.id.delete_ic).setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    recipeViewModel.deleteRecipe(recipeModel);
                    dialogInterface.dismiss();
                }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show());

        view.findViewById(R.id.edit_ic).setOnClickListener(v->{
           Navigation.findNavController(requireView()).navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToUploadRecipeFragment(position));

        });
    }
}