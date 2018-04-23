package com.htl.subrato.core.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.htl.subrato.core.servlets.RssFeedConstants;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Component(
        immediate = true,
        label = "Feed Service",
        description = "Feed Service provider")
@Service(value = FeedService.class)
public class FeedServiceImpl implements FeedService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedServiceImpl.class);

    @Override
    public String getJSON(String xmlFormatString) {
        try {
            JSONObject xmlJSONObj = XML.toJSONObject(xmlFormatString);
            return xmlJSONObj.toString();
        } catch (JSONException e) {
            LOG.error("error in parsing XML:" + e);
            return "{error : true}";
        }
    }

    @Override
    public String[] multiFieldValues(ResourceResolver resourceResolver, String resourcePath) {
        Resource resource = resourceResolver.getResource(resourcePath);
        if (null == resource) {
            throw new ResourceNotFoundException("Resource Not Found!");
        }
        ValueMap valueMap = resource.getValueMap();
        final String[] multiField = valueMap.get(RssFeedConstants.MULTIFIELD_FIELD, String[].class);
        LOG.debug("Multifield values:  " + Arrays.toString(multiField));
        return multiField;
    }

    @Override
    public JsonObject createJsonObject(String[] multiFieldValues) {
        JsonObject jsonObject = new JsonObject();
        JsonObject rss = new JsonObject();
        JsonObject channel = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            String jsonArrayString = Arrays.toString(multiFieldValues);
            JsonArray arrayFromString = jsonParser.parse(jsonArrayString).getAsJsonArray();
            channel.add(RssFeedConstants.ITEM, arrayFromString);
            rss.add(RssFeedConstants.CHANNEL, channel);
            jsonObject.add(RssFeedConstants.RSS, rss);
            return jsonObject;
        } catch (JsonSyntaxException e) {
            LOG.error("Error creating json from multiFieldValues :" + e);
        }
        return jsonObject;


    }

    @Override
    public String getResponse(String url) {
        String responseString = null;
        try {
            int responseCode;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(url.trim());
            getRequest.addHeader("accept", "application/xml");
            HttpResponse response = httpClient.execute(getRequest);
            LOG.debug(" Response from http call : {}", response);
            responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != HttpStatus.SC_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output;
            while ((output = br.readLine()) != null) {
                responseString = responseString + output;
            }
            httpClient.getConnectionManager().shutdown();
            return responseString.toString();
        } catch (ClientProtocolException e) {
            LOG.error("Error in connection : {}", e);
        } catch (IOException e) {
            LOG.error("Input Out put operation failed : {}", e);
        } catch (RuntimeException e) {
            LOG.error("RunTime Connection Exception : {}", e);
        }
        return responseString;
    }
}
