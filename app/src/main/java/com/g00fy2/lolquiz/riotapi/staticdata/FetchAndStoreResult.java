package com.g00fy2.lolquiz.riotapi.staticdata;

import com.g00fy2.lolquiz.exception.ResponseErrorException;

/**
 * Created by thoma on 29.10.2016.
 */

public class FetchAndStoreResult {

    private String version;
    private int dataCount;
    private int imgDateCount;
    private ResponseErrorException responseError;
    private ResponseErrorException imgResponseError;

    public FetchAndStoreResult() {
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setImgDateCount(int imgDateCount) {
        this.imgDateCount = imgDateCount;
    }

    public void setResponseError(ResponseErrorException responseError) {
        this.responseError = responseError;
    }

    public void setImgResponseError(ResponseErrorException imgResponseError) {
        this.imgResponseError = imgResponseError;
    }

    public int getDataCount() {
        return dataCount;
    }

    public String getVersion() {
        return version;
    }

    public int getImgDateCount() {
        return imgDateCount;
    }

    public ResponseErrorException getImgResponseError() {
        return imgResponseError;
    }

    public ResponseErrorException getResponseError() {
        return responseError;
    }
}
