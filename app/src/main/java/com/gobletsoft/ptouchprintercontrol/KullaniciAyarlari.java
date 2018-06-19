package com.gobletsoft.ptouchprintercontrol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KullaniciAyarlari extends AppCompatActivity {

    SessionManager session;

    private ProgressDialog pDialog;

    //php connections
    JSONParser jsonParser = new JSONParser();
    private static String url_hesapbilgilerini_getir = "";
    private static String url_hesapbilgilerini_guncelle = "";
    private static final String TAG_SUCCESS = "success";
    private JSONObject json;


    private String email;
    private EditText etKullaniciAdi;
    private EditText etAd;
    private EditText etSoyad;
    private EditText etEposta;
    private EditText etSifre;
    private Button btnKullaniciAyarlariGuncelle;

    private String GuncelKullaniciAdi;
    private String GuncelAd;
    private String GuncelSoyad;
    private String GuncelEmail;
    private String GuncelSifre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_kullanici_ayarlari);

        session = new SessionManager(getApplicationContext());

        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();

        etKullaniciAdi = findViewById(R.id.editTextKullaniciAdi);
        etAd = findViewById(R.id.editTextAd);
        etSoyad = findViewById(R.id.editTextSoyad);
        etEposta = findViewById(R.id.editTextEmailKa);
        etSifre = findViewById(R.id.editTextSifre);
        btnKullaniciAyarlariGuncelle = findViewById(R.id.buttonKullaniciAyarlariGuncelle);

        etEposta.setFocusable(false);

        email = user.get(SessionManager.KEY_NAME);
        String sifre = user.get(SessionManager.KEY_EMAIL);

        //tvEmail.setText(email + sifre);

        new kullaniciBilgileriniGetir().execute();

        btnKullaniciAyarlariGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etKullaniciAdi.getText().toString().isEmpty() || etAd.getText().toString().isEmpty() || etSoyad.getText().toString().isEmpty() ||
                        etEposta.getText().toString().isEmpty() || etSifre.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Lütfen Tüm Boşlukları Doldurunuz.", Toast.LENGTH_LONG);
                }

                else{

                    GuncelKullaniciAdi = etKullaniciAdi.getText().toString();
                    GuncelAd = etAd.getText().toString();
                    GuncelSoyad = etSoyad.getText().toString();
                    GuncelEmail = etEposta.getText().toString();
                    GuncelSifre = etSifre.getText().toString();

                    //Toast.makeText(getApplicationContext(), GuncelKullaniciAdi + GuncelSifre + GuncelEmail + GuncelAd + GuncelSoyad, Toast.LENGTH_LONG).show();

                    new kullaniciBilgileriniGuncelle().execute();
                }
            }
        });
    }

    class kullaniciBilgileriniGetir extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(KullaniciAyarlari.this);
            pDialog.setMessage("Kullanıcı Bilgileri Getiriliyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("kullanici_email", email));

            json = jsonParser.makeHttpRequest(url_hesapbilgilerini_getir,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                String kullaniciAdi = json.getString("kullaniciad");
                String ad = json.getString("ad");
                String soyad = json.getString("soyad");
                String email = json.getString("email");
                String sifre = json.getString("sifre");

                etKullaniciAdi.setText(kullaniciAdi);
                etAd.setText(ad);
                etSoyad.setText(soyad);
                etEposta.setText(email);
                etSifre.setText(sifre);
            }
            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    class kullaniciBilgileriniGuncelle extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(KullaniciAyarlari.this);
            pDialog.setMessage("Bilgileriniz Güncelleniyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("email", GuncelEmail));
            params.add(new BasicNameValuePair("kullanici_adi", GuncelKullaniciAdi));
            params.add(new BasicNameValuePair("ad", GuncelAd));
            params.add(new BasicNameValuePair("soyad", GuncelSoyad));
            params.add(new BasicNameValuePair("sifre", GuncelSifre));

            json = jsonParser.makeHttpRequest(url_hesapbilgilerini_guncelle,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                int success = json.getInt("success");

                if (success == 1){

                    Toast.makeText(getApplicationContext(), "Bilgileriniz Başarı ile Güncellendi.", Toast.LENGTH_LONG).show();
                }

                else{

                    Toast.makeText(getApplicationContext(), "Verilen Bilgilerde Bir Sıkıntı Oldu, Lütfen Tekrar Deneyin.",Toast.LENGTH_LONG);
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }
}
