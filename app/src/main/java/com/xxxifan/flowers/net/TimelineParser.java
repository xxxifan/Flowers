package com.xxxifan.flowers.net;

import com.xxxifan.flowers.net.model.MeizhiPost;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xifan on 15-9-29.
 */
public class TimelineParser extends Parser {

    private static final String COVER_URL = "/limg.jpg";
    private static final String FIRST_URL = "/01.jpg";
    private Element mElement;

    public TimelineParser(Element element) {
        mElement = element;
    }

    public List<MeizhiPost> toList() {
        Elements mainElements = mElement.getElementsByClass(CLASS_LIST);
        List<MeizhiPost> meizhiList = new ArrayList<>();

        MeizhiPost meizhi;
        Elements picElements = mainElements.select("div.pic");
        for (Element item : picElements) {
            meizhi = new MeizhiPost();
            Element aElement = item.child(0);
            Element imgElement = aElement.child(0);
            String imgUrl = imgElement.attr(ATTR_SRC);
            // syntax
            imgUrl = imgUrl.replace(COVER_URL, FIRST_URL);
            int end = imgUrl.indexOf(FIRST_URL) - 1;
            int idStart = imgUrl.lastIndexOf("/", end);
            if (idStart >= 0) {
                String id = imgUrl.substring(idStart + 1, end + 1);
                int monthStart = imgUrl.lastIndexOf("/", idStart - 1);
                if (monthStart >= 0) {
                    String month = imgUrl.substring(monthStart + 1, idStart);
                    int yearStart = imgUrl.lastIndexOf("/", monthStart - 1);
                    if (yearStart >= 0) {
                        String year = imgUrl.substring(yearStart + 1, monthStart);

                        meizhi.imgYear = year.replace("a", "");
                        meizhi.imgMonth = month;
                        meizhi.imgId = id;
                    }
                }
            }

            meizhi.postUrl = aElement.attr(ATTR_HREF);
            meizhi.coverUrl = imgUrl;
            meizhi.title = removeTags(imgElement.attr(ATTR_ALT));
            meizhiList.add(meizhi);
        }

        return meizhiList;
    }


}
