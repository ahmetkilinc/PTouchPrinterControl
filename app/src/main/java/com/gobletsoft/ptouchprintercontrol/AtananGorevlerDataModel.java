package com.gobletsoft.ptouchprintercontrol;

public class AtananGorevlerDataModel {

    /*String name;
    String type;
    String version_number;
    String feature;*/

    String firmaadi;
    String lokasyonadi;


    /*public DevamEdenGorevlerDataModel(String name, String type, String version_number, String feature ) {

        this.name=name;
        this.type=type;
        this.version_number=version_number;
        this.feature=feature;
    }*/

    public AtananGorevlerDataModel(String firmaadi, String lokasyonadi) {

        this.firmaadi=firmaadi;
        this.lokasyonadi=lokasyonadi;
    }

    public String getFirmaadi() {

        return firmaadi;
    }

    public String getLokasyonadi() {

        return lokasyonadi;
    }

}
