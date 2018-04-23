package com.accenture.openweatheracntest.common.api;

import com.accenture.openweatheracntest.model.WeatherGroup;
import com.accenture.openweatheracntest.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface OWService {

    @GET("/data/2.5/weather/")
    Call<WeatherResponse> weather(@Query("q") String query,
                               @Query("appid") String appId);

    @GET("/data/2.5/group")
    Call<WeatherGroup> weatherList(@Query(value = "id", encoded = true) String idList,
                                   @Query("appid") String appId);

}
