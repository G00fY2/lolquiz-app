package com.g00fy2.lolquiz.riotapi;

import com.g00fy2.lolquiz.exception.ApiException;
import com.g00fy2.lolquiz.riotapi.staticdata.FetchAndStoreResult;

import java.net.URL;
import java.util.Map;

public class StaticDataVersion extends RiotApi {

    final static String apiVersion = "/v1.2";
    final static String apiCategory = "/versions";

    public StaticDataVersion(final Map<String, String> apiValues) throws ApiException {
        super(apiValues, apiVersion, apiCategory);
    }

    public String getVersionData() throws ApiException {
        //Type typeOf = new TypeToken<List<String>>(){}.getType();
        String urlPath = buildStaticDataUrlPath();
        String urlQuery = getUrlQuery();

        URL newUrl = buildfullUrl(urlPath, urlQuery);

        return newUrl.toString();
    }

    @Override
    protected FetchAndStoreResult doInBackground(FetchAndStoreResult... integers) {
        return null;
    }
}
