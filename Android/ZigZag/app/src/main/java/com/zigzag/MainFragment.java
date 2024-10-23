package com.zigzag;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zigzag.adapter.PostAdapter;
import com.zigzag.api.client.RetrofitClient;
import com.zigzag.api.model.Post;
import com.zigzag.util.LocationManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainFragment extends Fragment implements OnMapReadyCallback, LocationManager.LocationUpdateListener {

    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private double longitude;
    private double latitude;

    private int distance = 80;
    private short zoom = 18;

    private ImageButton nearestButton;
    private ImageButton closeButton;
    private ImageButton farButton;
    private ImageButton furthestButton;


    private GoogleMap mMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getView().setBackgroundColor(Color.TRANSPARENT);
            mapFragment.getMapAsync(this);
        }

        LocationManager.getInstance(getActivity()).startLocationUpdates(getActivity());
        LocationManager.getInstance(getActivity()).setLocationUpdateListener(this); // Set the listener

        nearestButton = view.findViewById(R.id.nearest_distance_button);
        closeButton = view.findViewById(R.id.close_distance_button);
        farButton = view.findViewById(R.id.far_distance_button);
        furthestButton = view.findViewById(R.id.furthest_distance_button);

        nearestButton.setOnClickListener(this::setDistance);
        closeButton.setOnClickListener(this::setDistance);
        farButton.setOnClickListener(this::setDistance);
        furthestButton.setOnClickListener(this::setDistance);


        FloatingActionButton floatingActionButton = view.findViewById(R.id.createPostFAB);

       floatingActionButton.setOnClickListener(v -> {
           Bundle bundle = new Bundle();
           bundle.putDouble("latitude", latitude);
           bundle.putDouble("longitude", longitude);
           NavController navController = Navigation.findNavController(v);
           navController.navigate(R.id.action_mainFragment_to_createPostFragment, bundle);
       });




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

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        LocationManager.getInstance(getActivity()).stopLocationUpdates();
    }


    private void loadPosts() {
        Call<List<Post>> call = RetrofitClient.getApiService().getPostsWithinDistance(longitude, latitude, distance);

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

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    public void setDistance(View view) {
        if (view.getId() == R.id.nearest_distance_button) {
            distance = 80;
            zoom = 18;
        } else if (view.getId() == R.id.close_distance_button) {
            distance = 820;
            zoom = 13;
        } else if (view.getId() == R.id.far_distance_button) {
            distance = 40000;
            zoom = 10;
        } else {
            distance = 800000;
            zoom = 5;
        }

        LatLng userLocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoom));

        loadPosts();
    }

    private void updateMap() {
        LatLng userLocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoom));
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(false);

        Location currentLocation = LocationManager.getInstance(getActivity()).getCurrentLocation();
        if (currentLocation != null) {
            LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoom));
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        loadPosts();
        if (mMap != null && location != null) {
            LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoom)); // Move and zoom camera
        }
    }
}