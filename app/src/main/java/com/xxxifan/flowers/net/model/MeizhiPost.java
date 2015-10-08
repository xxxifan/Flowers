package com.xxxifan.flowers.net.model;

import java.util.Arrays;

/**
 * Created by xifan on 15-9-30.
 */
public class MeizhiPost {
    public String titile;
    public String postUrl;
    public String coverUrl;
    public String imgYear;
    public String imgMonth;
    public String imgId;
    public String[] tags;

    @Override
    public String toString() {
        return "MeizhiPost{" +
                "titile='" + titile + '\'' +
                ", postUrl='" + postUrl + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", imgYear='" + imgYear + '\'' +
                ", imgMonth='" + imgMonth + '\'' +
                ", imgId='" + imgId + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
