package com.binary6.transchato;

/**
 * Created by api on 19/2/16.
 */

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface APICalls {


    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/translate")
    Call<TranslateResponse> translate(@Field("key") String key, @Field("text") String text, @Field("lang") String lang);


}
