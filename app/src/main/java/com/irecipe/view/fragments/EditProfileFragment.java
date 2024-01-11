package com.irecipe.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.irecipe.R;
import com.irecipe.models.UserModel;
import com.irecipe.viewModel.ProfileViewModel;
import com.isapanah.awesomespinner.AwesomeSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.irecipe.utils.Constants.LOGIN_USER;


public class EditProfileFragment extends BaseFragment {

    CircleImageView circleImageView;
    String filePath;
    Uri fileUri;
    String userGender = "";
    String aboutUsTxt = "";
    Boolean isImageUpdated = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserModel userModel = Paper.book().read(LOGIN_USER);

        List<String> genderSpinnerList = new ArrayList<>();
        circleImageView = view.findViewById(R.id.user_image);

        ImageView backBtn = view.findViewById(R.id.back_button);
        EditText userName = view.findViewById(R.id.user_name);
        EditText userEmailAddress = view.findViewById(R.id.user_email_Address);
        EditText userPhoneNumber = view.findViewById(R.id.user_phone_number);
        EditText userAboutUs = view.findViewById(R.id.about_us_txt);
        TextView saveUserProfile = view.findViewById(R.id.save_txt);

        userGender = userModel.getUserGender();

        if (userModel.getUploadedImage() != null && !userModel.getUploadedImage().equals(""))
            Glide.with(requireContext()).load(userModel.getUploadedImage()).into(circleImageView);
        else
            circleImageView.setImageResource(R.drawable.background_image);

        userName.setText(userModel.getUserName());
        userEmailAddress.setText(userModel.getUserEmailAddress());
        userPhoneNumber.setText(userModel.getUserPhoneNumber());
        userAboutUs.setText(userModel.getAboutUs());

        genderSpinnerList.add("Male");  // aşağı doğru açılan list
        genderSpinnerList.add("Female");
        genderSpinnerList.add("Other");

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genderSpinnerList);
        AwesomeSpinner genderSpinner = view.findViewById(R.id.m_gender_spinner);
        genderSpinner.setAdapter(categoriesAdapter);
        genderSpinner.setDownArrowTintColor(ContextCompat.getColor(requireContext(), R.color.color_primary_dark));
        genderSpinner.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary_dark));
        genderSpinner.setSelectedItemHintColor(ContextCompat.getColor(requireContext(), R.color.color_primary_dark));

        genderSpinner.setSpinnerHint(userGender);

        backBtn.setOnClickListener(v-> requireActivity().onBackPressed());  // v android.view

        genderSpinner.setOnSpinnerItemClickListener((position, itemAtPosition) -> {
            Log.e("spinner == > ", itemAtPosition);
            userGender = itemAtPosition;
        });


        circleImageView.setOnClickListener(v -> {
            ImagePicker.Companion.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });


        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.alertMessage.observe(getViewLifecycleOwner(), message -> showAlertMessage(message));

        profileViewModel.progressBar.observe(getViewLifecycleOwner(), show -> customProgressDialog(show, "Please wait..."));

        profileViewModel.isUpdatedUser.observe(getViewLifecycleOwner(), isUpdatedUser->{
            if (isUpdatedUser != null) {
                isUpdatedUser.setRememberMe(userModel.getRememberMe());

                Paper.book().write(LOGIN_USER, isUpdatedUser);
                showAlertMessage("Your profile updated successfully....");
            }
        });


        saveUserProfile.setOnClickListener(v -> {
            if (userName.getText().toString().isEmpty()) {
                userName.setError("Please enter your name");
                return;
            }

            if (!isValidEmail(userEmailAddress.getText().toString())) {
                userEmailAddress.setError("Please enter valid email address");
                return;
            }

            if (userPhoneNumber.getText().toString().length() < 10) {
                userPhoneNumber.setError("Please enter valid phone number");
                return;
            }


            if (userGender.isEmpty()) {
                showAlertMessage("Please select gender");
                return;
            }

            aboutUsTxt = userAboutUs.getText().toString();
            UserModel updateUser = new UserModel(
                    userName.getText().toString(),
                    userEmailAddress.getText().toString(),
                    userPhoneNumber.getText().toString(),
                    userModel.getUserPassword(),
                    userGender,
                    aboutUsTxt,
                    fileUri
            );

            profileViewModel.updateUser(updateUser, isImageUpdated);

        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                fileUri = data.getData();
                circleImageView.setImageURI(fileUri);
                File file = new File(fileUri.getPath());
                filePath = file.getPath();
                isImageUpdated = true;
                Log.e("filPath == > ", filePath);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), "Error Occur", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}