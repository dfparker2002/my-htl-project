package com.htl.subrato.core.pojo;

import com.adobe.cq.sightly.WCMUsePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.htl.subrato.core.bean.FeedComponentMultifieldBean;
import org.apache.sling.commons.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FeedComponentMultifieldUse extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(FeedComponentMultifieldUse.class);
    private List<FeedComponentMultifieldBean> fallbackItems = new ArrayList<FeedComponentMultifieldBean>();
    @Override
    public void activate() {
        System.out.println(" Inside multifield activate method");
    setMultifieldItems();
    }
    private List<FeedComponentMultifieldBean> setMultifieldItems() {
        JSONObject jObj;
        try{
            String[] itemsProps = getProperties().get("myUserSubmenu", String[].class);
            System.out.println(" setMultifieldItems" +itemsProps);
            if (itemsProps != null) {
                for (int i = 0; i < itemsProps.length; i++) {

                    jObj = new JSONObject(itemsProps[i]);
                    FeedComponentMultifieldBean menuItem = new FeedComponentMultifieldBean();

                    String title = jObj.getString("title");
                    System.out.println(" title ->" +title);
                    String description = jObj.getString("description");
                    System.out.println(" description ->" +description);
                    String pubDate = jObj.getString("pubdate");
                    System.out.println(" pubDate ->" +pubDate);

                    menuItem.setTitle(title);
                    menuItem.setDescription(description);
                    menuItem.setPubDate(pubDate);
                    fallbackItems.add(menuItem);
                }
            }
        }catch(Exception e){
            LOG.error("Exception while Multifield data {}", e.getMessage(), e);
            System.out.println(" Exception while Multifield data ->" +e.getMessage());
        }
        return fallbackItems;
    }

    public List<FeedComponentMultifieldBean> getMultiFieldItems() {
        return fallbackItems;
    }
}
