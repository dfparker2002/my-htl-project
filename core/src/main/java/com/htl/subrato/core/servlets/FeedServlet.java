package com.htl.subrato.core.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


@SlingServlet(paths="/bin/feeds",methods = "GET")
public class FeedServlet extends SlingSafeMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(FeedServlet.class);


    @Override
    protected void doGet(final SlingHttpServletRequest slingRequest,
                         final SlingHttpServletResponse slingResponse) throws ServletException, IOException {
        LOG.debug("inside do method of Feed servlet");
        String feedURL = slingRequest.getParameter(RssFeedConstants.FEED_URL);
        String resourceObject = slingRequest.getParameter(RssFeedConstants.RESOURCE_OBJECT);
        String jsonString;
        String[] fallBackValues = null ;
        int responseCode=HttpStatus.SC_OK;
        try {
            fallBackValues = multiFieldValues(slingRequest.getResourceResolver(), resourceObject);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            LOG.debug("Before http call");
            HttpGet getRequest = new HttpGet(feedURL.trim());
            getRequest.addHeader("accept", "application/xml");
            HttpResponse response = httpClient.execute(getRequest);
            LOG.debug(" Feed Call response : {}", response);
            responseCode=response.getStatusLine().getStatusCode();
            if (responseCode != HttpStatus.SC_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output;
            String myXML = "";
            while ((output = br.readLine()) != null) {
                myXML = myXML + output;
            }
            httpClient.getConnectionManager().shutdown();
            LOG.debug(" Feed response XML version: {}", myXML);
            jsonString  = getJSON(myXML);
            slingResponse.setContentType("application/json");
            slingResponse.setCharacterEncoding("utf-8");
            slingResponse.getWriter().write(jsonString);
            slingResponse.setStatus(HttpStatus.SC_OK);

            LOG.debug("xml to json : " + jsonString);

        } catch ( HttpResponseException e ) {
            LOG.error("error in feed : " + e);
            }
        finally {
            if(responseCode !=HttpStatus.SC_OK) {
                jsonString = createJSON(fallBackValues).toString();
                slingResponse.getWriter().write(jsonString);
                LOG.debug("Manual json : " + jsonString);

            }
        }
    }

    protected String getJSON(String xmldata) {
        JSONObject xmlJSONObj;
        String jsonPrettyPrintString = null;
        try {
           xmlJSONObj =XML.toJSONObject(xmldata);
           jsonPrettyPrintString = xmlJSONObj.toString();
            } catch (JSONException e) {
            LOG.error("error in parsing XML:"+e);
            jsonPrettyPrintString="{error : true}";
            }
        return jsonPrettyPrintString;
    }
    private String[] multiFieldValues (ResourceResolver resourceResolver , String resourcePath){

        Resource resource = resourceResolver.getResource(resourcePath);
        String[] multifiled = null;
        if(null != resource) {
            ValueMap valueMap = resource.getValueMap();
        multifiled=valueMap.get(RssFeedConstants.MULTIFIELD_FIELD,String[].class);
        }
        LOG.debug("multifield values:>"+Arrays.toString(multifiled));
       return  multifiled;
    }
    private JsonObject createJSON (String[] multifield) {

        JsonObject mainObject= new JsonObject();
        JsonObject rss= new JsonObject();
        JsonObject channel= new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            String jsonArrayString = Arrays.toString(multifield);
            JsonArray arrayFromString =jsonParser.parse(jsonArrayString).getAsJsonArray();
            channel.add(RssFeedConstants.ITEM,arrayFromString);
            rss.add(RssFeedConstants.CHANNEL,channel);
            mainObject.add(RssFeedConstants.RSS,rss);
        }catch (Exception e){
            LOG.error("Error creating json from multifield :"+e);
        }
        return mainObject;
    }
}
