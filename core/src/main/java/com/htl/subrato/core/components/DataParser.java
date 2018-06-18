package com.htl.subrato.core.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataParser
{
    Logger logger = LoggerFactory.getLogger(DataParser.class);

    public Document docParse(String url)
    {
        try
        {
            return Jsoup.connect(url).get();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> parseLinks(String url)
    {
        List<String> hyperLinks = new ArrayList();
        try
        {
            Elements links = docParse(url).select("a[href]");
            for (Element link : links)
            {
                hyperLinks.add(link.attr("abs:href"));
                this.logger.info(link.attr("abs:href"));
            }
        }
        catch (Exception e)
        {
            this.logger.info("Something went wrong for parsing link.. {}", e);
        }
        return hyperLinks;
    }

    public List<String> parseImports(String url)
    {
        List<String> imports = new ArrayList();
        try
        {
            Elements imp = docParse(url).select("link[href]");
            for (Element i : imp) {
                imports.add(i.attr("abs:href"));
            }
        }
        catch (Exception e)
        {
            this.logger.info("Something went wrong for parsing imports.. {}", e);
        }
        return imports;
    }

    public List<String> parseImages(String url)
    {
        List<String> images = new ArrayList();
        try
        {
            Elements img = docParse(url).select("[src]");
            for (Element i : img) {
                if (i.tagName().equals("img"))
                {
                    images.add(i.attr("abs:src"));
                    this.logger.info(i.attr("abs:src"));
                }
            }
        }
        catch (Exception e)
        {
            this.logger.info("Something went wrong for parsing images.. {}", e);
        }
        return images;
    }
}