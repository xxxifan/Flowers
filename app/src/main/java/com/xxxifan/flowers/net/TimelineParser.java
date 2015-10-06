package com.xxxifan.flowers.net;

import com.xxxifan.flowers.net.model.MeizhiPost;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by xifan on 15-9-29.
 */
public class TimelineParser extends Parser {




    public TimelineParser(Element element) {
        initElements(element);
    }

    private void initElements(Element element) {

        Elements contentElements = element.getElementsByAttributeValue(KEY_ID, DIV_CONTENT);
        MeizhiPost meizhi;
        for (Element item : contentElements) {
            meizhi = new MeizhiPost();
            Elements aTag = item.getElementsByTag(TAG_A);
            Elements imgTag = item.getElementsByTag(TAG_IMG);
            meizhi.titile = aTag.attr(ATTR_TITLE);
            meizhi.postUrl = aTag.attr(ATTR_HREF);
            meizhi.coverUrl = imgTag.attr(ATTR_SRC);

        }
    }


}
