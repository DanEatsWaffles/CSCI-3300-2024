package com.zigzag;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zigzag.api.client.RetrofitClient;
import com.zigzag.api.model.NewPost;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostFragment extends Fragment {

    private EditText postContentEditText;

    private double longitude;
    private double latitude;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        if (getArguments() != null) {
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");
        }
        postContentEditText = view.findViewById(R.id.post_content_text);
        Button createPostButton = view.findViewById(R.id.create_post_button);
        createPostButton.setOnClickListener(this::createPost);
        return view;
    }

    private void createPost(View view) {
        NewPost newPost = new NewPost(postContentEditText.getText().toString(), "300313", null, latitude, longitude);
        Call<NewPost> call = RetrofitClient.getApiService().createPost(newPost, longitude, latitude);

        call.enqueue(new Callback<NewPost>() {
            @Override
            public void onResponse(@NonNull Call<NewPost> call, @NonNull Response<NewPost> response) {
                // Go back to previous page
                NavController navController = Navigation.findNavController(view);
                navController.navigateUp();
            }

            @Override
            public void onFailure(@NonNull Call<NewPost> call, @NonNull Throwable throwable) {

            }
        });
    }
}