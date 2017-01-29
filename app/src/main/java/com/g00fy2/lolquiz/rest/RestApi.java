package com.g00fy2.lolquiz.rest;

import com.g00fy2.lolquiz.riotapi.staticdata.ChampionListDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApi {
    @GET("/api/lol/static-data/euw/v1.2/champion")
    Call<ChampionListDto> getChampionList(@Query("version") String version,
                                          @Query("dataById") String dataById,
                                          @Query("champData") String champData,
                                          @Query("api_key") String apiKey);


    @GET("/api/lol/static-data/euw/v1.2/versions")
    Call<List<String>> getVersionList(@Query("api_key") String apiKey);

}
