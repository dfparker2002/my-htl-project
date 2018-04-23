package com.htl.subrato.core.services;

import com.google.gson.JsonObject;
import org.apache.sling.api.resource.ResourceResolver;

public interface FeedService {

    /**
     * Create Json format string from XML format string
     *
     * @param xmlFormatString - XML format String
     * @return Json in String format
     */
    String getJSON(String xmlFormatString);

    /**
     * Create String from MultiField dialog values
     *
     * @param resourceResolver - ResourceResolver object from slingRequest
     * @param resourcePath     - path where component is used
     * @return multiField in String[] format
     */
    String multiFieldValues(ResourceResolver resourceResolver, String resourcePath);

    /**
     * This method creates a JSONObject tree from a Multifield ValueMap.
     *
     * @param multiFieldValues - multiFieldValues from component dialog as String array
     * @return JSONObject according to RSS structure
     */
    JsonObject createJsonObject(String multiFieldValues);

    /**
     * Returns String representation of http response
     * @param url - End point url
     * @return response in String format
     */
    String getResponse(String url);

}
