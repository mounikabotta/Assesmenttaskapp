package com.mouni.assesmenttaskapp.Ui;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.mouni.assesmenttaskapp.Data.User;
import com.mouni.assesmenttaskapp.R;
import com.mouni.assesmenttaskapp.viewModel.UserViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements UserAdapter.OnImageClickListener {

    private UserViewModel userViewModel;
    private UserAdapter userAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static final int REQUEST_IMAGE_UPLOAD = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.location_text_view);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, null, this);
        recyclerView.setAdapter(userAdapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userAdapter.setUserList(users);
            }
        });

        userViewModel.fetchUsers(2); // Fetch users from API, replace `2` with the desired page number

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchUserLocation();
    }

    private void fetchUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        updateLocationUI(location);
                    }
                }
            });
        }
    }

    private void updateLocationUI(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = String.format("Latitude: %f\nLongitude: %f\nAddress: %s",
                        location.getLatitude(), location.getLongitude(), address.getAddressLine(0));
                locationTextView.setText(addressText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation();
            }
        }
    }


    @Override
    public void onImageClick(User user) {
        // Handle image upload here (e.g., open camera/gallery)
        Intent intent = new Intent(this, ImageUploadActivity.class);
        intent.putExtra("user_id", user.getId());
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK && data != null) {
            int userId = data.getIntExtra("user_id", -1);
            String localImagePath = data.getStringExtra("local_image_path");
            if (userId != -1 && localImagePath != null) {
                userAdapter.updateUserImage(userId, localImagePath);
            }
        }
    }

    private List<User> getUserList() {
        // Retrieve user list from your data source (API, database, etc.)
        return new ArrayList<>();
    }

}


