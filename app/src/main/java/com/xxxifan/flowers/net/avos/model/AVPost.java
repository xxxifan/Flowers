package com.xxxifan.flowers.net.avos.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xxxifan.flowers.net.model.MeizhiPost;

/**
 * Created by xifan on 15-10-12.
 */
@AVClassName("MeizhiPost")
public class AVPost extends AVObject {

    public static AVPost fromMeizhiPost(MeizhiPost post) {
        AVPost avPost = null;
        try {
            avPost = AVPost.createWithoutData(AVPost.class, post.getObjectId());
            avPost.setCoverUrl(post.coverUrl);
            avPost.setImgId(post.imgId);
            avPost.setImgMonth(post.imgMonth);
            avPost.setImgYear(post.imgYear);
            avPost.setPostUrl(post.postUrl);
            avPost.setTags(post.tags);
            avPost.setTitle(post.title);
            avPost.setPostId(post.getPostId());
            avPost.setLikeNum(post.likeNum);
            avPost.setUnlikeNum(post.unlikeNum);
        } catch (AVException e) {
            e.printStackTrace();
        }

        return avPost;
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getPostUrl() {
        return getString("postUrl");
    }

    public void setPostUrl(String postUrl) {
        put("postUrl", postUrl);
    }

    public String getCoverUrl() {
        return getString("coverUrl");
    }

    public void setCoverUrl(String coverUrl) {
        put("coverUrl", coverUrl);
    }

    public String getImgYear() {
        return getString("imgYear");
    }

    public void setImgYear(String imgYear) {
        put("imgYear", imgYear);
    }

    public String getImgMonth() {
        return getString("imgMonth");
    }

    public void setImgMonth(String imgMonth) {
        put("imgMonth", imgMonth);
    }

    public String getImgId() {
        return getString("imgId");
    }

    public void setImgId(String imgId) {
        put("imgId", imgId);
    }

    public String[] getTags() {
        if (get("tags") == null) {
            return new String[0];
        }
        return getString("tags").split(",");
    }

    public void setTags(String[] tags) {
        if (tags == null) {
            return;
        }
        String str = "";
        for (int i = 0, s = tags.length; i < s; i++) {
            if (i > 0) {
                str += ",";
            }
            str += tags[i];
        }
        put("tags", str);
    }

    public int getPostId() {
        return getInt("postId");
    }

    public void setPostId(int id) {
        put("postId", id);
    }

    public int getLikeNum() {
        return getInt("likeNum");
    }

    public void setLikeNum(int num) {
        put("likeNum", num);
    }

    public int getUnlikeNum() {
        return getInt("unlikeNum");
    }

    public void setUnlikeNum(int num) {
        put("unlikeNum", num);
    }

    public MeizhiPost toMeizhiPost() {
        return new MeizhiPost(this);
    }
}
