package de.gamedots.mindlr.mindlrfrontend.util;

import java.util.Map;

import de.gamedots.mindlr.mindlrfrontend.model.ImageResponse;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ImgurApi {

    String BASE_URL = "https://api.imgur.com/3/";
    String AUTH_DATA = "Client-ID " + "2a6b5d228e54f2f";
    String IMAGE_KEY = "image";

    @FormUrlEncoded
    @POST("image/")
    Call<ImageResponse> postImage(@Header("Authorization") String clientId,
                                  @FieldMap Map<String, String> imageParams);
}
