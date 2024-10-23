package com.zigzag;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import com.zigzag.adapter.PostAdapter;
import com.zigzag.api.client.RetrofitClient;
import com.zigzag.api.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class SearchFragment extends Fragment {

    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private double lastLongitude; // Replace with actual longitude value
    private double lastLatitude; // Replace with actual latitude value
    private String hashtag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if (getArguments() != null) {
            hashtag = getArguments().getString("hashtag");
        }

        // Set up the toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        // Enable the back button
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


       ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(hashtag);

        // Handle toolbar back button press
        toolbar.setNavigationOnClickListener(v -> {
            // Navigate back when the back button is pressed
            NavController navController = Navigation.findNavController(view);
            navController.navigateUp();
        });

        // Initialize RecyclerView and SwipeRefreshLayout
        recyclerView = view.findViewById(R.id.posts_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter();
        recyclerView.setAdapter(postAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadPosts();
            }
        });

        // Load posts
        loadPosts();

        return view;
    }

    private void loadPosts() {
        Call<List<Post>> call = RetrofitClient.getApiService().getPostsWithHashtag(lastLongitude, lastLatitude, hashtag);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull retrofit2.Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postAdapter.updatePosts(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                // Handle error
            }
        });
    }
}

