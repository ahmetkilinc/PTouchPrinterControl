package com.gobletsoft.ptouchprintercontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    private static String url_login = "http://10.0.0.100:85/ptouchAndroid/hesapbilgilerinial.php";
    private static final String TAG_SUCCESS = "success";
    private JSONObject json;

    private String email;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_ayarlari);

        tvEmail = findViewById(R.id.textViewEmail);

        session = new SessionManager(getApplicationContext());

        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();

        email = user.get(SessionManager.KEY_NAME);
        String sifre = user.get(SessionManager.KEY_EMAIL);

        //tvEmail.setText(email + sifre);

        new kullaniciBilgileriniGetir().execute();
    }

    class kullaniciBilgileriniGetir extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(KullaniciAyarlari.this);
            pDialog.setMessage("Giriş Yapılıyor");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("kullanici_email", email));

            json = jsonParser.makeHttpRequest(url_login,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                String kullaniciAdi = json.getString("kullaniciadi");

                Toast.makeText(getApplicationContext(), kullaniciAdi, Toast.LENGTH_LONG).show();
                //tvEmail.setText(kullaniciAdi);

            }
            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }
}
