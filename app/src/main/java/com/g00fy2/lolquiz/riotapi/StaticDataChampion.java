package com.g00fy2.lolquiz.riotapi;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.g00fy2.lolquiz.exception.ApiException;
import com.g00fy2.lolquiz.exception.ResponseErrorException;
import com.g00fy2.lolquiz.rest.RestApi;
import com.g00fy2.lolquiz.rest.RetroClient;
import com.g00fy2.lolquiz.riotapi.staticdata.ChampionListDto;
import com.g00fy2.lolquiz.riotapi.staticdata.FetchAndStoreResult;
import com.g00fy2.lolquiz.sqlite.ChampionsDataSource;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class StaticDataChampion extends RiotApi {

    private FetchAndStoreCallbackInterface callbackInterface;
    private Context appContext;
    private Boolean catchImages;
    private String latestVersion;

    public StaticDataChampion(final Map<String, String> apiValues, String latestVersion, FetchAndStoreCallbackInterface callbackInterface, Boolean catchImages, Context appContext) throws ApiException {
        super(apiValues);
        this.appContext = appContext;
        this.catchImages = catchImages;
        this.latestVersion = latestVersion;
        this.callbackInterface = callbackInterface;
    }

    // Override AsyncTask method from superclass
    @Override
    protected FetchAndStoreResult doInBackground(FetchAndStoreResult... args) {
        FetchAndStoreResult result = new FetchAndStoreResult();
        result.setVersionResult(false);
        try {
            RestApi restApi = RetroClient.getRestApi(buildStaticDataBaseUrl());
            Call<ChampionListDto> call = restApi.getChampionList(latestVersion, "true", "stats", apiKey);
            Response response = call.execute();

            if (response.isSuccessful()) {
                ChampionListDto champListDto = (ChampionListDto) response.body();
                result.setVersion(champListDto.version);
                ChampionsDataSource storedata = new ChampionsDataSource(appContext);
                storedata.openWriteable();
                result.setDataCount(storedata.insertChampionsTransaction(champListDto));
                storedata.close();

                // If champions successfully written to db add image url to it
                if (result.getDataCount() > 0) {
                    Call<ChampionListDto> imgCall = restApi.getChampionList(latestVersion, "true", "image", apiKey);
                    Response imgResponse = imgCall.execute();
                    if (imgResponse.isSuccessful()) {
                        ChampionListDto imgChampListDto = (ChampionListDto) imgResponse.body();
                        storedata.openWriteable();
                        ArrayList<String> imgURLs = storedata.updateChampionImgTransaction(imgChampListDto);
                        result.setImgDateCount(imgURLs.size());
                        storedata.close();
                        if (catchImages) {
                            for (String url : imgURLs) {
                                // loading all images with picasso to utilize its caching
                                // seems like there is no better solution atm
                                Picasso.with(appContext).load(url).get();
                            }
                        }
                    } else {
                        ResponseErrorException error = new ResponseErrorException(imgResponse.code(), imgCall.request().url().toString());
                        result.setImgResponseError(error);
                        Log.e(this.getClass().getCanonicalName(), imgCall.request().url() + " => " + imgResponse.message());
                    }
                }

            } else {
                ResponseErrorException error = new ResponseErrorException(response.code(), call.request().url().toString());
                result.setImgResponseError(error);
                Log.e(this.getClass().getCanonicalName(), call.request().url() + " => " + response.message());
            }
        } catch (ApiException | IOException | SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(FetchAndStoreResult result) {
        super.onPostExecute(result);
        // pass the result to the callback function
        this.callbackInterface.fetchAndStoreCallback(result);
    }

}
