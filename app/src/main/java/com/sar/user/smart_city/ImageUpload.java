package com.sar.user.smart_city;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface ImageUpload {
    @POST("api/test")
    Call<ResponseData> uploadImage(@Body String image);
}
