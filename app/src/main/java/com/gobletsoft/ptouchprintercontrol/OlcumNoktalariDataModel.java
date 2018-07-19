package com.gobletsoft.ptouchprintercontrol;

public class OlcumNoktalariDataModel {

    String olcumbolumadi;
    String olculennokta;
    String degiskennumarasi;

    public OlcumNoktalariDataModel(String olcumbolumadi, String olculennokta, String degiskennumarasi){

        this.olcumbolumadi = olcumbolumadi;
        this.olculennokta = olculennokta;
        this.degiskennumarasi = degiskennumarasi;
    }

    public String getOlcumbolumadi() {

        return olcumbolumadi;
    }

    public String getOlculennokta() {

        return olculennokta;
    }

    public String getDegiskennumarasi() {

        return degiskennumarasi;
    }
}
