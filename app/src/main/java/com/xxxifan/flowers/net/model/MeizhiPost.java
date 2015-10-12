package com.xxxifan.flowers.net.model;

import java.util.Arrays;

/**
 * Created by xifan on 15-9-30.
 */
public class MeizhiPost {

    private static final int ERROR_ID = -1;

    public int likeNum;
    public int unlikeNum;
    public String title;
    public String postUrl;
    public String coverUrl;
    public String imgYear;
    public String imgMonth;
    public String imgId;
    public String[] tags;
    private int postId;

    public MeizhiPost() {
    }

    public MeizhiPost(int likeNum, int unlikeNum, String title, String postUrl, String coverUrl,
                      String imgYear, String imgMonth, String imgId, String[] tags) {
        this.likeNum = likeNum;
        this.unlikeNum = unlikeNum;
        this.title = title;
        this.postUrl = postUrl;
        this.coverUrl = coverUrl;
        this.imgYear = imgYear;
        this.imgMonth = imgMonth;
        this.imgId = imgId;
        this.tags = tags;
    }

    public int getPostId() {
        if (postId == 0) {
            postId = imgYear == null || imgMonth == null || imgId == null ? ERROR_ID
                    : Integer.parseInt(imgYear + imgMonth + imgId);
        }

        return postId;
    }

    @Override
    public String toString() {
        return "MeizhiPost{" +
                "title='" + title + '\'' +
                ", postUrl='" + postUrl + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", imgYear='" + imgYear + '\'' +
                ", imgMonth='" + imgMonth + '\'' +
                ", imgId='" + imgId + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
