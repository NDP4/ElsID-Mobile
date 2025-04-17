// ApiService.java
package com.mobile2.uts_elsid.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("user_elsid.php")
    Call<LoginResponse> login(
            @Field("action") String action,
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("user_elsid.php")
    Call<UserResponse> getUser();

    @FormUrlEncoded
    @POST("user_elsid.php")
    Call<RegisterResponse> register(
            @Field("action") String action,
            @Field("fullname") String fullname,
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("product_elsid.php")
    Call<ProductResponse> getProducts();

    @GET("banner_elsid.php")
    Call<BannerResponse> getBanners();

//    @GET("product_elsid.php")
//    Call<ProductDetailResponse> getProductDetail(@Query("id") String productId);
    @GET("product_elsid.php")
    Call<ProductDetailResponse> getProductDetail(@Query("id") String productId);

}