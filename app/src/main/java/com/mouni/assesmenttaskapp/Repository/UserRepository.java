package com.mouni.assesmenttaskapp.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mouni.assesmenttaskapp.Data.User;
import com.mouni.assesmenttaskapp.Data.UserDao;
import com.mouni.assesmenttaskapp.Data.UserDatabase;
import com.mouni.assesmenttaskapp.Data.UserResponse;
import com.mouni.assesmenttaskapp.Network.RetrofitApiService;
import com.mouni.assesmenttaskapp.Network.RetrofitClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserRepository {
    private UserDao userDao;
    private LiveData<List<User>> allUsers;
    private RetrofitApiService apiService;
    private ExecutorService executorService;

    public UserRepository(Application application) {
        UserDatabase database = UserDatabase.getInstance(application);
        userDao = database.userDao();
        allUsers = userDao.getAllUsers();
        apiService = RetrofitClient.getClient().create(RetrofitApiService.class);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void fetchUsersFromApi(int page) {
        apiService.getUsers(page).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        for (User user : response.body().getData()) {
                            userDao.insert(user);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Handle API failure
            }
        });
    }
}

