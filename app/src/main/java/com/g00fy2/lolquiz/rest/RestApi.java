package com.g00fy2.lolquiz.rest;

import com.g00fy2.lolquiz.riotapi.staticdata.ChampionListDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApi {
    @GET("/api/lol/static-data/euw/v1.2/champion")
    Call<ChampionListDto> getChampionList(@Query("dataById") String dataById,
                                          @Query("champData") String champData,
                                          @Query("api_key") String apiKey);

}
