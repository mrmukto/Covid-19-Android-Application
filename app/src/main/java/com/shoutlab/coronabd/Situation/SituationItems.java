package com.shoutlab.coronabd.Situation;

public class SituationItems {
    private String countryName, countryCases;

    public SituationItems(String countryName, String countryCases) {
        this.countryName = countryName;
        this.countryCases = countryCases;
    }

    String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    String getCountryCases() {
        return countryCases;
    }

    public void setCountryCases(String countryCases) {
        this.countryCases = countryCases;
    }
}
