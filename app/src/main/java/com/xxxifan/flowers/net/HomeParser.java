package com.xxxifan.flowers.net;

import android.text.TextUtils;

import com.xxxifan.flowers.net.model.MeizhiPost;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xifan on 15-10-18.
 */
public class HomeParser extends MeizhiParser {
    public HomeParser(Element element) {
        super(element);
    }

    public List<MeizhiPost> toList() {
        Elements mainElements = getElement().getElementsByAttributeValue(KEY_ID, DIV_MAIN);
        List<MeizhiPost> meizhiList = new ArrayList<>();

        int count = 0;
        Elements contentElements = mainElements.attr(KEY_ID, DIV_CONTENT).first().children();
        MeizhiPost meizhi = null;
        for (Element item : contentElements) {
            if (item.className().startsWith(CLASS_POST_META)) {
                Elements pTags = item.getElementsByTag(TAG_P);
                for (int i = 0; i < pTags.size(); i++) {
                    String tags = pTags.get(i).text();
                    if (!TextUtils.isEmpty(tags) && tags.startsWith("Tags:")) {
                        meizhi = new MeizhiPost();
                        meizhi.tags = tags.replace("Tags:", "").trim().split(",");
                        meizhiList.add(count, meizhi);
                        break;
                    }
                }
            } else if (item.className().equals(CLASS_POST_CONTENT)) {
                if (count >= meizhiList.size() || meizhiList.get(count) == null) {
                    continue;
                }
                Elements aTags = item.getElementsByTag(TAG_A);
                for (int i = 0; i < aTags.size(); i++) {
                    Element tag = aTags.get(i);
                    if (!TextUtils.isEmpty(tag.toString())) {
                        meizhi = meizhiList.get(count);
                        meizhi.title = tag.attr(ATTR_TITLE);
                        meizhi.postUrl = tag.attr(ATTR_HREF);
                        Elements imgTags = tag.getElementsByTag(TAG_IMG);
                        meizhi.coverUrl = imgTags.attr(ATTR_SRC);

                        // get date info
                        String str = meizhi.coverUrl;
                        int end = str.indexOf("/01.jpg") - 1;
                        int idStart = str.lastIndexOf("/", end);
                        if (idStart >= 0) {
                            String id = str.substring(idStart + 1, end + 1);
                            int monthStart = str.lastIndexOf("/", idStart - 1);
                            if (monthStart >= 0) {
                                String month = str.substring(monthStart + 1, idStart);
                                int yearStart = str.lastIndexOf("/", monthStart - 1);
                                if (yearStart >= 0) {
                                    String year = str.substring(yearStart + 1, monthStart);

                                    meizhi.imgYear = year.replace("a", "");
                                    meizhi.imgMonth = month;
                                    meizhi.imgId = id;
                                }
                            }
                        }

                        meizhiList.set(count, meizhi);
                        count++;
                        break;
                    }
                }
            }
        }

        return meizhiList;
    }
}
