package kr.hs.emirim.jiwonan.retrofittest;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RedayService {
    @POST("users") // requestparam == Query
    Call<User> createUser(@Query("username") String username, @Query("password") String password, @Query("email") String email);

    @POST("users/login")
    Call<LoginResponse> loginUser(@Query("email") String email, @Query("password") String password);

    @Multipart
    @POST("{username}/articles")
    Call<String> createArticle(@Path("username") String username, @Query("contents") String contents, @Part MultipartBody.Part file);

    @GET("{email}/getusername")
    Call<String> getUsername(@Path("email") String email);

//    @POST("{username}/articles")
//    Call<String> test(@Path("username") String username);
}
