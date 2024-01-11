package com.irecipe.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.irecipe.R;
import com.irecipe.viewModel.LoginViewModel;

import io.paperdb.Paper;

import static com.irecipe.utils.Constants.LOGIN_USER;

public class LoginFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText userLoginEmail = view.findViewById(R.id.user_login_email_Address);
        EditText userPassword = view.findViewById(R.id.user_login_password);
        Button userLoginBtn = view.findViewById(R.id.login_button);
        CheckBox rememberMeChecked = view.findViewById(R.id.remember_me_check_box);
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.alertMessage.observe(getViewLifecycleOwner(), message-> showAlertMessage(message));

        loginViewModel.progressBar.observe(getViewLifecycleOwner(), show -> customProgressDialog(show, "Please wait..."));

        loginViewModel.loginUser.observe(getViewLifecycleOwner(), user -> {

            Paper.book().write(LOGIN_USER, user);
            Navigation.findNavController(requireView()).navigate(LoginFragmentDirections.toHomeFragment());

        });

        userLoginBtn.setOnClickListener( v -> {
            if (!isValidEmail(userLoginEmail.getText().toString())){
                userLoginEmail.setError("Please Enter valid email");
                return;
            }

            if (userPassword.getText().toString().length() < 7){
                userPassword.setError("Please enter minimum six digit characters");
                return;
            }

            loginViewModel.userLogin(userLoginEmail.getText().toString(), userPassword.getText().toString(), rememberMeChecked.isChecked());
        });

        view.findViewById(R.id.skip_txt).setOnClickListener(v-> Navigation.findNavController(requireView()).navigate(LoginFragmentDirections.toHomeFragment()));
        view.findViewById(R.id.go_to_sign_up).setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(LoginFragmentDirections.toSignUpFragment()));
    }
}