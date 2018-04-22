package com.htl.subrato.core.servlets;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.htl.subrato.core.servlets.RssFeedConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedServletTest {
//    @Rule
//    public AemContext context = new AemContext();

    @Mock
    private MockSlingHttpServletRequest request;

    @Mock
    private MockSlingHttpServletResponse response;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    @Mock
    private ValueMap valueMap;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    FeedServlet feedServlet;

    @Before
    public void setUp() {
        when(request.getParameter(FEED_URL)).thenReturn("http://www.nba.com/bucks/rss.xml");
        when(request.getParameter(RESOURCE_OBJECT)).thenReturn("/content/www-htl-com/en/rss-feed/jcr:content/par/feed");
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource(anyString())).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        response = new MockSlingHttpServletResponse();
        String[] strArr = new String[10];
        strArr[0] = "{\"title\":\"dasdas\",\"description\":\"sadasd\",\"pubDate\":\"2018-04-18T14:28:00.000+02:00\"}";
        strArr[1] = "{\"title\":\"sdasd\",\"description\":\"asdasd\",\"pubDate\":\"2018-04-18T11:28:00.000+02:00\"}";
        when(valueMap.get(MULTIFIELD_FIELD, String[].class)).thenReturn(strArr);

    }

    @Test
    public void doGetTest() throws Exception {
        feedServlet.doGet(request, response);
        assertEquals("application/json;charset=utf-8", response.getContentType());
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

    }

    @Test
    public void getJSONTest () throws JSONException {
        String xmlData= "<note>\n" +
                "<to>Tove</to>\n" +
                "<from>Jani</from>\n" +
                "<heading>Reminder</heading>\n" +
                "<body>Don't forget me this weekend!</body>\n" +
                "</note>";
        JSONObject xmlJSONObj;
        xmlJSONObj = XML.toJSONObject(xmlData);
     String str = feedServlet.getJSON(xmlData);
        assertEquals( xmlJSONObj.toString(),str);
    }
    @Test
    public void getJSONIncorrectXMLTest(){
        String xmlData= "<note>\n" +
                "<to>Tove</to>\n" +
                "<from>Jani<from>\n" +
                "<heading>Reminder</heading>\n" +
                "<body>Don't forget me this weekend!</body>\n" +
                "</note>";
        String str = feedServlet.getJSON(xmlData);
        System.out.println(str);
        assertEquals("{error : true}",str);

    }


    }
