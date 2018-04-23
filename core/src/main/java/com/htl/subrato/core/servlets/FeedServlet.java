package com.htl.subrato.core.servlets;

import com.google.gson.JsonObject;
import com.htl.subrato.core.services.FeedService;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

@SlingServlet(paths = "/bin/feeds", methods = "GET")
public class FeedServlet extends SlingSafeMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(FeedServlet.class);

    @Reference
    private FeedService feedService;

    @Override
    protected void doGet(final SlingHttpServletRequest slingRequest,
                         final SlingHttpServletResponse slingResponse) throws ServletException {
        LOG.debug("Inside do method of Feed servlet");
        String feedURL = slingRequest.getParameter(RssFeedConstants.FEED_URL);
        String responseOutput = StringUtils.EMPTY;
        try {
            String jsonString;
            responseOutput = feedService.getResponse(feedURL);
            if (!StringUtils.EMPTY.equals(responseOutput)) {
                LOG.debug(" Feed response XML version: {}", responseOutput);
                jsonString = feedService.getJSON(responseOutput);
                slingResponse.setContentType("application/json");
                slingResponse.setCharacterEncoding("utf-8");
                slingResponse.setStatus(HttpStatus.SC_OK);
                slingResponse.getWriter().write(jsonString);
                LOG.debug("xml to json : " + jsonString);
            }
        } catch (IOException e) {
            LOG.error("Error Writing in feed response: {}", e);
        } finally {
            if (StringUtils.EMPTY.equals(responseOutput)) {
                fallBack(slingRequest, slingResponse);
            }
        }
    }

    private void fallBack(SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse) {
        try {

            String resourceObject = slingRequest.getParameter(RssFeedConstants.RESOURCE_OBJECT);
            String fallBackValues = feedService.multiFieldValues(slingRequest.getResourceResolver(), resourceObject);
            JsonObject jsonString = feedService.createJsonObject(fallBackValues);
            slingResponse.setContentType("application/json");
            slingResponse.setCharacterEncoding("utf-8");
            slingResponse.setStatus(HttpStatus.SC_OK);
            slingResponse.getWriter().write(jsonString.toString());
        } catch (IOException e) {
            LOG.error("Writing of json to slingResponse fails : {}", e);
        } catch (ResourceNotFoundException e) {
            LOG.error(" Resource Not found : {}", e);
        }

    }


}
