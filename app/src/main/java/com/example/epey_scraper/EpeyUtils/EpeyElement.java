package com.example.epey_scraper.EpeyUtils;

public class EpeyElement {


    private final String name;
    private final String price;
    private final String imUrl;
    private String infoPageUrl;

    public EpeyElement(String imUrl, String infoPageUrl, String name, String price) {
        this.imUrl = imUrl;
        this.infoPageUrl = infoPageUrl;
        this.name = name;
        this.price = price;

    }

    public String getName() {
        return name;
    }

    public String getInfoPageUrl() {
        return infoPageUrl;
    }

    public String getPrice() {
        if (price.isEmpty())
            return "Bilinmiyor";
        return price + "₺";
    }

    public String getImage() {
        return imUrl.replace("https", "http");
    }
}
