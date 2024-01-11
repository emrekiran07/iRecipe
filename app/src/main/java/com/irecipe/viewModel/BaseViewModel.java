package com.irecipe.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BaseViewModel extends ViewModel {
    public MutableLiveData<Boolean> progressBar = new MutableLiveData();
    public MutableLiveData<String> alertMessage = new MutableLiveData();
    protected FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
    protected FirebaseStorage storage = FirebaseStorage.getInstance();
    protected StorageReference storageReference = storage.getReference();

    protected void showProgressBar(Boolean show) {
        progressBar.setValue(show);
    }

    protected void showAlertMessage(String message) {
        alertMessage.setValue(message);
    }
}
