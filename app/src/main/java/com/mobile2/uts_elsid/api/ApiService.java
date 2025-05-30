// ApiService.java
package com.mobile2.uts_elsid.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

//    @FormUrlEncoded
//    @POST("user_elsid.php")
//    Call<UserResponse> updateProfile(
//            @Field("action") String action,
//            @Field("id") String id,
//            @Field("fullname") String fullname,
//            @Field("phone") String phone,
//            @Field("address") String address,
//            @Field("city") String city,
//            @Field("province") String province,
//            @Field("postal_code") String postalCode
//    );
    @FormUrlEncoded
    @POST("user_elsid.php")
    @Headers("Accept: application/json")
    Call<ResponseBody> updateProfile(
            @Field("action") String action,
            @Field("id") String id,
            @Field("fullname") String fullname,
            @Field("phone") String phone,
            @Field("address") String address,
            @Field("city") String city,
            @Field("province") String province,
            @Field("postal_code") String postalCode
    );

    @Multipart
    @POST("user_elsid.php")
    Call<ResponseBody> updateAvatar(
            @Part("action") RequestBody action,
            @Part("id") RequestBody id,
            @Part MultipartBody.Part avatar
    );

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

    @GET("product_elsid.php")
    Call<ProductResponse> getProduct(@Query("id") int id);

    // Di ApiService.java
    @GET("product_elsid.php")
    Call<List<ProductResponse>> getProductsByIds(@Query("ids") List<Integer> ids);

}