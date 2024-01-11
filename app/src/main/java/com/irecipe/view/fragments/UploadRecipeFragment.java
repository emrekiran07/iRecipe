package com.irecipe.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.irecipe.R;
import com.irecipe.models.RecipeModel;
import com.irecipe.models.UserModel;
import com.irecipe.utils.Constants;
import com.irecipe.viewModel.RecipeViewModel;

import java.io.File;

import io.paperdb.Paper;

import static com.irecipe.utils.Constants.LOGIN_USER;


public class UploadRecipeFragment extends BaseFragment {

    String filePath;
    Uri fileUri;
    ImageView dishImage;
    String mDocumentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dishImage = view.findViewById(R.id.dish_image);
        ImageView backScreen = view.findViewById(R.id.back_button);
        TextView uploadRecipeTxt = view.findViewById(R.id.upload_txt);
        EditText dishName = view.findViewById(R.id.dish_name);
        EditText dishIngredient = view.findViewById(R.id.dish_ingredient_txt);
        EditText dishDirection = view.findViewById(R.id.direction_txt);

        assert getArguments() != null;
        int position = (int) getArguments().get("position");

        if (position != -1) {
            RecipeModel existingRecipeModel = Constants.recipeModelArrayList.get(position);
            mDocumentId = existingRecipeModel.getDocumentId();
            uploadRecipeTxt.setText("Edit");
            dishName.setText(existingRecipeModel.getDishName());
            dishIngredient.setText(existingRecipeModel.getDishIngredient());
            dishDirection.setText(existingRecipeModel.getDishDirection());
            ((TextView)view.findViewById(R.id.title_top)).setText("Edit Recipe");

            if (!existingRecipeModel.getDishUploadedImage().isEmpty()){
                Glide.with(requireContext()).load(existingRecipeModel.getDishUploadedImage()).into(dishImage);
            }
        }

        backScreen.setOnClickListener(v -> requireActivity().onBackPressed());
        RecipeViewModel recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        recipeViewModel.progressBar.observe(getViewLifecycleOwner(), show -> customProgressDialog(show, "Please Wait..."));
        recipeViewModel.alertMessage.observe(getViewLifecycleOwner(), this::showAlertMessage);
        recipeViewModel.isRecipeUploaded.observe(getViewLifecycleOwner(), isUploaded -> {
            if (isUploaded)
                requireActivity().onBackPressed();
        });

        recipeViewModel.isEditRecipe.observe(getViewLifecycleOwner(), isEditSuccess -> {
            if (isEditSuccess){
                Navigation.findNavController(requireView()).navigate(UploadRecipeFragmentDirections.toHomeFrag());
            }
        });

        dishImage.setOnClickListener(v -> ImagePicker.Companion.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start());

        uploadRecipeTxt.setOnClickListener(v -> {
            if (dishName.getText().toString().isEmpty()) {
                dishName.setError("Please enter dish name...");
                return;
            }

            if (dishIngredient.getText().toString().isEmpty()) {
                dishIngredient.setError("Please enter ingredients...");
                return;
            }

            if (dishDirection.getText().toString().isEmpty()) {
                dishDirection.setError("Please enter direction...");
                return;
            }

            UserModel userModel = Paper.book().read(LOGIN_USER);

            RecipeModel recipeModel = new RecipeModel(
                    userModel.getUserName(),
                    dishName.getText().toString(),
                    dishIngredient.getText().toString(),
                    dishDirection.getText().toString(),
                    fileUri
            );

            recipeModel.setUploadUserEmailId(userModel.getUserEmailAddress());

            if (position == -1)
                recipeViewModel.updateRecipe(recipeModel);
            else {
                recipeModel.setDocumentId(mDocumentId);
                recipeViewModel.editRecipe(recipeModel);
            }

        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                fileUri = data.getData();
                dishImage.setImageURI(fileUri);
                File file = new File(fileUri.getPath());
                filePath = file.getPath();
                Log.e("filPath == > ", filePath);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), "Error Occur", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}