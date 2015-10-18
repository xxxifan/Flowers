package com.xxxifan.flowers.net;

import org.jsoup.nodes.Element;

/**
 * Created by xifan on 15-9-29.
 */
public class MeizhiParser {
    public static final String DIV_MAIN = "maincontent";
    public static final String DIV_CONTENT = "picture";
    public static final String DIV_TAG = "metaRight";
    public static final String KEY_ID = "id";
    public static final String TAG_A = "a";
    public static final String TAG_IMG = "img";
    public static final String TAG_P = "p";

    public static final String ATTR_TITLE = "title";
    public static final String ATTR_HREF = "href";
    public static final String ATTR_SRC = "src";
    public static final String ATTR_ALT = "alt";
    public static final String CLASS_LIST = "wp-list";
    public static final String CLASS_POST_META = "postmeta";
    public static final String CLASS_POST_CONTENT = "postContent";
    public static final String CLASS_DAY = "day";

    private static final String tagPattern = "<[^>]*>";

    private Element mElement;


    public MeizhiParser(Element element) {
        mElement = element;
    }

    public static String removeTags(String str) {
        return str.replaceAll(tagPattern, "");
    }

    protected Element getElement() {
        return mElement;
    }
}
