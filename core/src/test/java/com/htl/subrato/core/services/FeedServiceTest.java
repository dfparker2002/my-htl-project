package com.htl.subrato.core.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.htl.subrato.core.servlets.RssFeedConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static com.htl.subrato.core.servlets.RssFeedConstants.MULTI_FIELD;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedServiceTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    @InjectMocks
    private FeedServiceImpl feedServiceImpl;

    @Mock
    private ValueMap valueMap;

    @Test
    public void getJSONTest() throws JSONException {
        String xmlData = "<note>\n" +
                "<to>Tove</to>\n" +
                "<from>Jani</from>\n" +
                "<heading>Reminder</heading>\n" +
                "<body>Don't forget me this weekend!</body>\n" +
                "</note>";
        JSONObject xmlJSONObj = XML.toJSONObject(xmlData);
        String str = feedServiceImpl.getJSON(xmlData);
        assertEquals(xmlJSONObj.toString(), str);
    }

    @Test
    public void getJSONIncorrectXMLTest() {
        String xmlData = "<note>\n" +
                "<to>Tove</to>\n" +
                "<from>Jani<from>\n" +
                "<heading>Reminder</heading>\n" +
                "<body>Don't forget me this weekend!</body>\n" +
                "</note>";
        String str = feedServiceImpl.getJSON(xmlData);
        assertEquals("{error : true}", str);

    }

    @Test
    public void multiFieldValuesTest() {
        when(resourceResolver.getResource(anyString())).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        String[] strArr = new String[10];
        strArr[0] = "{\"mfg\":\"dasdas\",\"description\":\"sadasd\",\"pubDate\":\"2018-04-18T14:28:00.000+02:00\"}";
        strArr[1] = "{\"title\":\"sdasd\",\"description\":\"asdasd\",\"pubDate\":\"2018-04-18T11:28:00.000+02:00\"}";
        when(valueMap.get(MULTI_FIELD, String[].class)).thenReturn(strArr);
        String multiFieldValues = feedServiceImpl.multiFieldValues(resourceResolver, resource.getPath());

        assertEquals(Arrays.toString(strArr), multiFieldValues);
    }

    @Test
    public void createJsonObjectTest() {
        JsonObject jsonObject = new JsonObject();
        JsonObject rss = new JsonObject();
        JsonObject channel = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        String[] strArr = new String[10];
        strArr[0] = "{\"mfg\":\"dasdas\",\"description\":\"sadasd\",\"pubDate\":\"2018-04-18T14:28:00.000+02:00\"}";
        strArr[1] = "{\"title\":\"sdasd\",\"description\":\"asdasd\",\"pubDate\":\"2018-04-18T11:28:00.000+02:00\"}";

        String jsonArrayString = Arrays.toString(strArr);
        JsonArray arrayFromString = jsonParser.parse(jsonArrayString).getAsJsonArray();
        channel.add(RssFeedConstants.ITEM, arrayFromString);
        rss.add(RssFeedConstants.CHANNEL, channel);
        jsonObject.add(RssFeedConstants.RSS, rss);
        JsonObject jsonResult = feedServiceImpl.createJsonObject(Arrays.toString(strArr));
        assertEquals(jsonObject, jsonResult);

    }
}
