package com.example.epey_scraper.EpeyUtils;

import android.util.Log;

import static android.support.constraint.Constraints.TAG;

public class LastUrl {
    private String domain = "";
    private String subDomain = "";
    private int page = 1;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getPage() {
        return String.valueOf(page);
    }

    public String nextPage() {

        return String.format("%s%s/%s", domain, subDomain, String.valueOf(++page));
    }

    public String previousPage() {
        return String.format("%s%s/%s", domain, subDomain, String.valueOf(--page));
    }

    public String gotoCategoryPage(String subDomain) {
        this.subDomain = subDomain.split(" \\(")[0];
        return String.format("%s%s", domain, this.subDomain);
    }


}
