package com.xxxifan.flowers.net.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by xifan on 15-9-30.
 */
public class MeizhiPost implements Parcelable, Comparable<MeizhiPost> {

    public static final Parcelable.Creator<MeizhiPost> CREATOR = new Parcelable.Creator<MeizhiPost>() {
        public MeizhiPost createFromParcel(Parcel source) {
            return new MeizhiPost(source);
        }

        public MeizhiPost[] newArray(int size) {
            return new MeizhiPost[size];
        }
    };
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

    protected MeizhiPost(Parcel in) {
        this.likeNum = in.readInt();
        this.unlikeNum = in.readInt();
        this.title = in.readString();
        this.postUrl = in.readString();
        this.coverUrl = in.readString();
        this.imgYear = in.readString();
        this.imgMonth = in.readString();
        this.imgId = in.readString();
        this.tags = in.createStringArray();
        this.postId = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.likeNum);
        dest.writeInt(this.unlikeNum);
        dest.writeString(this.title);
        dest.writeString(this.postUrl);
        dest.writeString(this.coverUrl);
        dest.writeString(this.imgYear);
        dest.writeString(this.imgMonth);
        dest.writeString(this.imgId);
        dest.writeStringArray(this.tags);
        dest.writeInt(this.postId);
    }

    @Override
    public int compareTo(MeizhiPost another) {
        return another == null ? 1 : (another.getPostId() - getPostId());
    }
}
