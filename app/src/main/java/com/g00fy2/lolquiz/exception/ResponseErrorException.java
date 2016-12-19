package com.g00fy2.lolquiz.exception;

import com.g00fy2.lolquiz.util.ApiUtils;

public class ResponseErrorException extends ApiException {
    private int statusCode;
    private String badUrl;

    public ResponseErrorException(int responseCode, String url)
    {
        super(ApiUtils.getHttpError(responseCode));
        statusCode = responseCode;
        badUrl = url;
    }

    public int getStatus(){
        return statusCode;
    }

    public String getBadUrl() {
        return badUrl;
    }
}
