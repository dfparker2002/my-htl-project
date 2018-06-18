package com.htl.subrato.core.components;

import com.adobe.cq.address.api.AddressException;
import com.adobe.cq.address.api.location.Coordinates;
import com.adobe.cq.address.api.location.GeocodeProvider;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = {Resource.class})
public class Parser {
    Logger logger = LoggerFactory.getLogger(Parser.class);
    public static final String DEFAULT = "http://jsoup.org/cookbook/input/load-document-from-url";
    public static final String TRUE = "true";
    public static final String FALSE = "";
    @Inject
    @Named("address")
    @Default(values = {"http://jsoup.org/cookbook/input/load-document-from-url"})
    protected String addressDescription;
    @Inject
    @Named("imp")
    @Default(values = {""})
    protected String imp;
    @Inject
    @Named("link")
    @Default(values = {""})
    protected String link;
    @Inject
    @Named("img")
    @Default(values = {""})
    protected String img;
    @Inject
    private GeocodeProvider geoCode;
    public Coordinates coordinates;
    private List<String> file;
    private List<String> imports;
    private List<String> images;

    @PostConstruct
    public void activate() throws AddressException {
        this.file = new ArrayList();
        this.imports = new ArrayList();
        this.images = new ArrayList();
        System.out.println();
        System.out.println("images : " +img);
        System.out.println("address : " +addressDescription);

        this.logger.info("URL is {}", this.addressDescription);
        if (this.link.equals("true")) {
            this.file = new DataParser().parseLinks(this.addressDescription);
            this.logger.info("file size {}", this.file.size());
        }
        if (this.img.equals("true")) {
            this.images = new DataParser().parseImages(this.addressDescription);
            this.logger.info("Images size {}", this.images.size());
        }
        if (this.imp.equals("true")) {
            this.imports = new DataParser().parseImports(this.addressDescription);
            this.logger.info("Imports size {}", this.imports.size());
        }
        this.coordinates = this.geoCode.geocode(this.addressDescription);
    }

    public List<String> getLinks() {
        return this.file;
    }

    public List<String> getImages() {
        return this.images;
    }

    public List<String> getImports() {
        return this.imports;
    }
}


