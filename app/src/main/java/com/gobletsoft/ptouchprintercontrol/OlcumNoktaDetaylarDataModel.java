package com.gobletsoft.ptouchprintercontrol;

public class OlcumNoktaDetaylarDataModel {

    String degiskenadi;
    String degiskendegeri;

    public OlcumNoktaDetaylarDataModel(String degiskenadi, String degiskendegeri){

        this.degiskenadi = degiskenadi;
        this.degiskendegeri = degiskendegeri;
    }

    public String getDegiskenadi() {

        return degiskenadi;
    }

    public String getDegiskendegeri() {

        return degiskendegeri;
    }
}
