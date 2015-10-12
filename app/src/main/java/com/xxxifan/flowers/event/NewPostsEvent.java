package com.xxxifan.flowers.event;

import com.xxxifan.devbox.library.entity.CustomEvent;

/**
 * Created by xifan on 15-10-13.
 */
public class NewPostsEvent implements CustomEvent {
    public int num;

    public NewPostsEvent(int num) {
        this.num = num;
    }
}
