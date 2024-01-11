package com.irecipe.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavArgument;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.irecipe.R;
import com.irecipe.models.UserModel;
import com.irecipe.viewModel.LoginViewModel;
import com.irecipe.viewModel.ProfileViewModel;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.irecipe.utils.Constants.LOGIN_USER;

public class UserProfileFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView backImage = view.findViewById(R.id.back_button);
        CircleImageView userCircleImage = view.findViewById(R.id.user_image);
        LinearLayout userView = view.findViewById(R.id.user_view);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userEmailAddress = view.findViewById(R.id.user_email_Address);
        TextView userAboutTxt = view.findViewById(R.id.user_about_txt);
        TextView userGender = view.findViewById(R.id.user_gender);
        TextView userProfileEdit = view.findViewById(R.id.edit_txt);
        UserModel userModel = Paper.book().read(LOGIN_USER);


        assert getArguments() != null;
        String userEmailId = (String) getArguments().get("user_email_address");


        if (userModel.getUserEmailAddress().equals(userEmailId)) {
            userProfileEdit.setVisibility(View.VISIBLE);
        } else
            userProfileEdit.setVisibility(View.GONE);

        userProfileEdit.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(UserProfileFragmentDirections.toEditProfileFrag());
        });

        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // if (profileViewModel.userProfile.getValue() == null)
        profileViewModel.getUserProfile(userEmailId);

        profileViewModel.alertMessage.observe(getViewLifecycleOwner(), message -> showAlertMessage(message));

        profileViewModel.progressBar.observe(getViewLifecycleOwner(), show -> customProgressDialog(show, "Please wait..."));

        profileViewModel.userProfile.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                if (userModel.getUserEmailAddress().equals(userEmailId)) {
                    user.setRememberMe(userModel.getRememberMe());
                    Paper.book().write(LOGIN_USER, user);
                }
                userView.setVisibility(View.VISIBLE);
                if (user.getUploadedImage() != null && !user.getUploadedImage().equals(""))
                    Glide.with(requireContext()).load(user.getUploadedImage()).into(userCircleImage);
                else
                    userCircleImage.setImageResource(R.drawable.background_image);

//                Glide.with(requireContext()).load(user.getUploadedImage()).into(userCircleImage);
                userName.setText(user.getUserName());
                userEmailAddress.setText(user.getUserEmailAddress());
                userAboutTxt.setText(user.getAboutUs());
                userGender.setText(user.getUserGender());

            }
        });

        backImage.setOnClickListener(v -> {
            requireActivity().onBackPressed();

        });
    }
}