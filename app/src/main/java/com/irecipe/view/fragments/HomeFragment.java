package com.irecipe.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.irecipe.R;
import com.irecipe.models.RecipeModel;
import com.irecipe.models.UserModel;
import com.irecipe.utils.Constants;
import com.irecipe.view.adapter.HomeRecipeAdapter;
import com.irecipe.viewModel.RecipeViewModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.irecipe.utils.Constants.LOGIN_USER;


public class HomeFragment extends BaseFragment implements HomeRecipeAdapter.ItemClickListener {

    DrawerLayout drawerLayout;
    LinearLayout navigation;
    RecipeViewModel recipeViewModel;
    ArrayList<RecipeModel> filterArrayList = new ArrayList<>();
    ArrayList<RecipeModel> mRecipeArrayList = new ArrayList<>();
    HomeRecipeAdapter homeRecipeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView homeMenuIcon = view.findViewById(R.id.open_menu);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigation = view.findViewById(R.id.navigaiton);
        CircleImageView userProfileImage = navigation.findViewById(R.id.user_image);
        UserModel userModel = Paper.book().read(LOGIN_USER);
        RecyclerView recipeRecycler = view.findViewById(R.id.recipe_recycler);

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);


        recipeViewModel.progressBar.observe(getViewLifecycleOwner(), show -> customProgressDialog(show, "Please Wait..."));
        recipeViewModel.alertMessage.observe(getViewLifecycleOwner(), this::showAlertMessage);

        recipeViewModel.recipeList.observe(getViewLifecycleOwner(), recipeList -> {
            if (!recipeList.isEmpty()) {
                ArrayList<RecipeModel> localList = Paper.book().read(Constants.FAVOURITE_LIST);

                if (localList != null) {
                    for (RecipeModel recipeModel : recipeList) {
                        for (RecipeModel recipe : localList) {
                            if (recipe.getDishDirection().equals(recipeModel.getDishDirection()) &&
                                    recipe.getDishIngredient().equals(recipeModel.getDishIngredient())
                            ) {
                                recipeModel.setFavouriteRecipe(true);
                            }
                        }

                    }
                }
                Constants.recipeModelArrayList = recipeList;
                mRecipeArrayList = recipeList;
                homeRecipeAdapter = new HomeRecipeAdapter(mRecipeArrayList, this, false);
                recipeRecycler.setAdapter(homeRecipeAdapter);
            }
        });


        ((EditText) view.findViewById(R.id.search_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterArrayList.clear();
                if (s.length() > 0) {
                    for (RecipeModel recipeModel : mRecipeArrayList) {
                        if (recipeModel.getDishName().toLowerCase().contains(s.toString().toLowerCase()) ||
                                recipeModel.getUploadedUserName().toLowerCase().contains(s.toString().toLowerCase()) ||
                                recipeModel.getDishIngredient().toLowerCase().contains(s.toString().toLowerCase())) {
                            filterArrayList.add(recipeModel);
                        }
                    }
                    homeRecipeAdapter = new HomeRecipeAdapter(filterArrayList, HomeFragment.this, false);
                } else {
                    homeRecipeAdapter = new HomeRecipeAdapter(mRecipeArrayList, HomeFragment.this, false);
                }
                recipeRecycler.setAdapter(homeRecipeAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (userModel.getUploadedImage() != null && !userModel.getUploadedImage().equals(""))
            Glide.with(requireContext()).load(userModel.getUploadedImage()).into(userProfileImage);
        else
            userProfileImage.setImageResource(R.drawable.background_image);


        if (userModel.getUserName() != null)
            ((TextView) navigation.findViewById(R.id.user_menu_name)).setText(userModel.getUserName());
        else
            ((TextView) navigation.findViewById(R.id.user_menu_name)).setText("Guest User");

        if (userModel.getUserName() != null) {
            navigation.findViewById(R.id.logout_txt).setVisibility(View.VISIBLE);
            navigation.findViewById(R.id.go_back_to_login_txt).setVisibility(View.GONE);
            navigation.findViewById(R.id.login_line).setVisibility(View.GONE);
        } else {
            navigation.findViewById(R.id.logout_txt).setVisibility(View.GONE);
            navigation.findViewById(R.id.go_back_to_login_txt).setVisibility(View.VISIBLE);
            navigation.findViewById(R.id.login_line).setVisibility(View.VISIBLE);
        }

        navigation.findViewById(R.id.profile_text).setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            if (userModel.getUserName() != null)
                new Handler(Looper.getMainLooper()).postDelayed(() -> Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.toUserProfileFragment().setUserEmailAddress(userModel.getUserEmailAddress())), 450);
            else
                showAlertMessage("Please login first!!!");

        });

        navigation.findViewById(R.id.logout_txt).setOnClickListener(v -> {
            Paper.book().write(LOGIN_USER, new UserModel());
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.toLoginFragment());
        });

        navigation.findViewById(R.id.go_back_to_login_txt).setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.toLoginFragment());
        });

        navigation.findViewById(R.id.upload_recipe_txt).setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            if (userModel.getUserName() != null)
                new Handler(Looper.getMainLooper()).postDelayed(() -> Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.toUploadRecipe(-1)), 450);
            else
                showAlertMessage("Please login first!!!");

        });

        navigation.findViewById(R.id.favorite_recipe_txt).setOnClickListener(v -> {
            drawerLayout.closeDrawers();

            new Handler(Looper.getMainLooper()).postDelayed(() -> Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.toFavouriteListFragment()), 450);
        });

        navigation.findViewById(R.id.my_recipe_txt).setOnClickListener(v -> {
            drawerLayout.closeDrawers();

            new Handler(Looper.getMainLooper()).postDelayed(() -> Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.toMyRecipeFrag()), 450);

        });


        homeMenuIcon.setOnClickListener(v -> {
            if (drawerLayout.isOpen())
                drawerLayout.closeDrawers();
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((EditText) requireView().findViewById(R.id.search_text)).setText("");
        recipeViewModel.getAllRecipes();

    }

    @Override
    public void onItemClick(int position) {
        Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.toRecipeDetails().setPosition(position));
    }

    @Override
    public void likedClicked(RecipeModel recipeModel, Boolean isFlag) {
        if (isFlag) {
            ArrayList<RecipeModel> localList = Paper.book().read(Constants.FAVOURITE_LIST);
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