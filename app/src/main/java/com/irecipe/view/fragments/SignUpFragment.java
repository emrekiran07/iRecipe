package com.irecipe.view.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.irecipe.R;
import com.irecipe.models.UserModel;
import com.irecipe.viewModel.SignUpViewModel;
import com.isapanah.awesomespinner.AwesomeSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpFragment extends BaseFragment {

    CircleImageView circleImageView;
    String filePath;
    Uri fileUri;
    String userGender = "";
    String aboutUsTxt = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> genderSpinnerList = new ArrayList<>();
        circleImageView = view.findViewById(R.id.profile_image);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userEmailAddress = view.findViewById(R.id.user_email_Address);
        TextView userPassword = view.findViewById(R.id.user_password);
        TextView userPhoneNumber = view.findViewById(R.id.user_phone_number);
        TextView userAboutUs = view.findViewById(R.id.about_us_txt);

        genderSpinnerList.add("Male");
        genderSpinnerList.add("Female");
        genderSpinnerList.add("Other");
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genderSpinnerList);
        AwesomeSpinner genderSpinner = view.findViewById(R.id.gender_spinner);
        genderSpinner.setAdapter(categoriesAdapter);
        genderSpinner.setDownArrowTintColor(ContextCompat.getColor(requireContext(), R.color.color_primary_dark));
        genderSpinner.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary_dark));
        genderSpinner.setSelectedItemHintColor(ContextCompat.getColor(requireContext(), R.color.color_primary_dark));

        genderSpinner.setOnSpinnerItemClickListener((position, itemAtPosition) -> {
            Log.e("spinner == > ", itemAtPosition);
            userGender = itemAtPosition;
        });

        SignUpViewModel signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        signUpViewModel.progressBar.observe(getViewLifecycleOwner(), show -> customProgressDialog(show, "Please Wait..."));
        signUpViewModel.alertMessage.observe(getViewLifecycleOwner(), message-> showAlertMessage(message));
        signUpViewModel.signUpUser.observe(getViewLifecycleOwner(), userModel -> getActivity().onBackPressed());


        loginButton.setOnClickListener(v -> {
            if (userName.getText().toString().isEmpty()){
                userName.setError("Please enter your name");
                return;
            }

            if (!isValidEmail(userEmailAddress.getText().toString())){
                userEmailAddress.setError("Please enter valid email address");
                return;
            }

            if (userPassword.getText().toString().length() < 7){
                userPassword.setError("Please enter minimum six digit characters");
                return;
            }

            if (userPhoneNumber.getText().toString().length() < 10){
                userPhoneNumber.setError("Please enter valid phone number");
                return;
            }



            if (userGender.isEmpty()){
                showAlertMessage("Please select gender");
                return;
            }
            aboutUsTxt = userAboutUs.getText().toString();
            UserModel newUser = new UserModel(
                    userName.getText().toString(),
                    userEmailAddress.getText().toString(),
                    userPhoneNumber.getText().toString(),
                    userPassword.getText().toString(),
                    userGender,
                    aboutUsTxt,
                    fileUri
            );

            signUpViewModel.createUser(newUser);
        });


        circleImageView.setOnClickListener(v -> {
            ImagePicker.Companion.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
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
                Log.e("filPath == > ", filePath);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), "Error Occur", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}