package com.accenture.openweatheracntest.model;

import java.util.List;

/**
 * Created by geoffry.heredia on 23/04/2018.
 */

public class WeatherGroup {

    private Integer cnt;
    private List<WeatherResponse> list = null;

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public WeatherGroup withCnt(Integer cnt) {
        this.cnt = cnt;
        return this;
    }

    public List<WeatherResponse> getList() {
        return list;
    }

    public void setList(List<WeatherResponse> list) {
        this.list = list;
    }

    public WeatherGroup withList(List<WeatherResponse> list) {
        this.list = list;
        return this;
    }

}
