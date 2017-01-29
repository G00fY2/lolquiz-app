package com.g00fy2.lolquiz.riotapi;

import com.g00fy2.lolquiz.exception.ApiException;
import com.g00fy2.lolquiz.rest.RestApi;
import com.g00fy2.lolquiz.rest.RetroClient;
import com.g00fy2.lolquiz.riotapi.staticdata.FetchAndStoreResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class StaticDataVersion extends RiotApi {
    private FetchAndStoreCallbackInterface callbackInterface;

    public StaticDataVersion(final Map<String, String> apiValues, FetchAndStoreCallbackInterface callbackInterface) throws ApiException {
        super(apiValues);
        this.callbackInterface = callbackInterface;
    }

    @Override
    protected FetchAndStoreResult doInBackground(FetchAndStoreResult... integers) {
        FetchAndStoreResult result = new FetchAndStoreResult();
        result.setVersionResult(true);

        try {
            RestApi restApi = RetroClient.getRestApi(buildStaticDataBaseUrl());
            Call< List<String>> call = restApi.getVersionList(apiKey);
            Response response = call.execute();

            if (response.isSuccessful()) {
                List<String> versionList = (List<String>) response.body();
                result.setVersion(versionList.get(0));
            }
        } catch (ApiException | IOException e) {
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
