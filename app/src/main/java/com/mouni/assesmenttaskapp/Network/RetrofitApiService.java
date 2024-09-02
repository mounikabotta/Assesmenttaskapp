package com.mouni.assesmenttaskapp.Network;



import com.mouni.assesmenttaskapp.Data.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApiService {

    @GET("users")
    Call<UserResponse> getUsers(@Query("page") int page);
}
