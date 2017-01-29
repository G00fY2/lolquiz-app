package com.g00fy2.lolquiz.riotapi;

import android.os.AsyncTask;

import com.g00fy2.lolquiz.exception.ApiException;
import com.g00fy2.lolquiz.exception.IllegalApiParameterException;
import com.g00fy2.lolquiz.riotapi.staticdata.FetchAndStoreResult;
import com.g00fy2.lolquiz.util.ApiUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public abstract class RiotApi extends AsyncTask<FetchAndStoreResult,Void,FetchAndStoreResult> {

    protected String apiKey;
    protected String urlHost;
    protected String urlPath;
    protected String urlQuery;
    protected String region;
    protected String apiVersion;
    protected String apiCategory;

    public RiotApi(final Map<String, String> apiValues) throws ApiException {
        apiKey = apiValues.get("apiKey");
        urlHost = apiValues.get("urlHost");
        urlPath = apiValues.get("urlPath");
        urlQuery = "api_key=" + apiKey;
        setRegion(apiValues.get("region"));
    }

    public URL buildfullUrl (String urlPath, String urlQuery) throws ApiException{

        try
        {
            URI uri;
            if (urlPath.contains("static-data")) {
                uri = new URI("https", null, "global" + urlHost, -1, urlPath , urlQuery, null);
            }
            else {
                uri = new URI("https", null, region + urlHost, -1, urlPath , urlQuery, null);
            }

            return uri.toURL();
        }
        catch (URISyntaxException e)
        {
            throw new ApiException("Invalid URI Syntax.");
        }
        catch (MalformedURLException e)
        {
            throw new ApiException("Malformed URL.");
        }
    }

    protected String buildUrlPath(){
        return      urlPath
                +   region
                +   apiVersion
                +   apiCategory;
    }

    protected String buildStaticDataUrlPath(){
        return urlPath + "static-data/" + region + apiVersion + apiCategory;
    }

    public void setRegion(String region) throws IllegalApiParameterException {
        this.region = ApiUtils.validRegion(region);
    }

    public String getRegion(){
        return region;
    }

    protected String getUrlQuery(){
        return urlQuery;
    }

    public String buildStaticDataBaseUrl() throws ApiException{
        try
        {
            URI uri = new URI("https", null, "global" + urlHost, -1, null , null, null);

            return uri.toURL().toString();
        }
        catch (URISyntaxException e)
        {
            throw new ApiException("Invalid URI Syntax.");
        }
        catch (MalformedURLException e)
        {
            throw new ApiException("Malformed URL.");
        }
    }

}
