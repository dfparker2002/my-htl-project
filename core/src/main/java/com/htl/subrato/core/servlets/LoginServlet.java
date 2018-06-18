package com.htl.subrato.core.servlets;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(paths = "/bin/loginForm", selectors = "submit", methods = "POST")
public class LoginServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(FeedServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse) {
        String username = slingRequest.getParameter("username");
        String password = slingRequest.getParameter("password");
        System.out.println("Username : " + username);
        System.out.println("Password : " + password);
        try {
            slingResponse.sendRedirect("/content/www-htl-com/fr.html");


        } catch (Exception e) {
            System.out.println(" exception aa gaya :" + e);
        }

    }
}
