package com.htl.subrato.core.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
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
    private static final String FEED_URL ="feedURL";
    private static final String RESOURCE_OBJECT ="pagePath";
    private static final String MULTIFIELD_FIELD="fallback";
    private static final String RSS="rss";
    private static final String ITEM="item";
    private static final String CHANNEL="channel";



    @Override
    protected void doGet(final SlingHttpServletRequest slingRequest,
                         final SlingHttpServletResponse slingResponse) throws ServletException, IOException {
        LOG.debug("inside do method of Feed servlet");
        String feedURL = slingRequest.getParameter(FEED_URL);
        String resourceObject= slingRequest.getParameter(RESOURCE_OBJECT);
        String jsonString;
        String[] fallBackValues=multiFieldValues(slingRequest,resourceObject);
        int responseCode=HttpStatus.SC_OK;
        try {
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

            LOG.debug("xml to json : " + jsonString);

        } catch (Exception e) {

            LOG.error("error in feed : " + e.getMessage());
            }
        finally {
            if(responseCode !=HttpStatus.SC_OK) {
                jsonString = createJSON(fallBackValues).toString();
                slingResponse.getWriter().write(jsonString);
                LOG.debug("Manual json : " + jsonString);

            }
        }
    }

    private String getJSON(String xmldata) {
        JSONObject xmlJSONObj;
        String jsonPrettyPrintString= null;
        try {
           xmlJSONObj =XML.toJSONObject(xmldata);
           jsonPrettyPrintString = xmlJSONObj.toString(4);
           System.out.println(jsonPrettyPrintString);
        } catch (Exception e) {
            LOG.error("error in parsing XML:"+e.getMessage());
        }
        return jsonPrettyPrintString;
    }
    private String[] multiFieldValues (SlingHttpServletRequest slingRequest, String resourcePath){
        ResourceResolver resourceResolver= slingRequest.getResourceResolver();
        Resource resource = resourceResolver.getResource(resourcePath);
        ValueMap valueMap= resource.getValueMap();

        String[] multifiled=valueMap.get(MULTIFIELD_FIELD,String[].class);
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
            channel.add(ITEM,arrayFromString);
            rss.add(CHANNEL,channel);
            mainObject.add(RSS,rss);
        }catch (Exception e){
            LOG.error("Error creating json from multifield :"+e.getMessage());
        }
        return mainObject;
    }
}
