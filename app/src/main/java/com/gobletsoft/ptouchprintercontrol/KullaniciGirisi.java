package com.gobletsoft.ptouchprintercontrol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KullaniciGirisi extends AppCompatActivity {

    String kullaniciAdiServerdan;
    String adiServerdan;
    String soyadiServerdan;

    private String email;
    private String password;

    private ProgressDialog pDialog;

    //php connections
    JSONParser jsonParser = new JSONParser();
    private static String url_login = "";
    private static final String TAG_SUCCESS = "success";
    private JSONObject json;

    //session durumu
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_kullanici_girisi);

        final EditText etEmail = findViewById(R.id.editTextEmail);
        final EditText etPassword = findViewById(R.id.editTextPassword);
        Button btnSignin = findViewById(R.id.buttonSignin);
        Button btnForgotPassword = findViewById(R.id.buttonForgotPassword);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        //Toast.makeText(getApplicationContext(), "Kullanıcı Durumu: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        if (session.isLoggedIn() == true){

            startActivity(new Intent(KullaniciGirisi.this, Activity_StartMenu.class));
        }

        btnSignin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //cevap yollamak için,
                //kullanıcı bilgilerini db ile karşılaştır ve cevap yolla.

                if (etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Email ve Şifre Boş Bırakılamaz.", Toast.LENGTH_LONG).show();
                }
                else{

                    email = etEmail.getText().toString();
                    password = etPassword.getText().toString();

                    new loginBack().execute();
                }
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // şifre değiştirt - değişim maili yolla -, db ile email karşılaştır ve değiştir.
            }
        });
    }

    class loginBack extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(KullaniciGirisi.this);
            pDialog.setMessage("Giriş Yapılıyor");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("kullanici_email", email));
            params.add(new BasicNameValuePair("kullanici_sifre", password));

            json = jsonParser.makeHttpRequest(url_login,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                int success = json.getInt(TAG_SUCCESS);
                kullaniciAdiServerdan = json.getString("kullaniciadi");
                adiServerdan = json.getString("adi");
                soyadiServerdan = json.getString("soyadi");

                if (success == 1){

                    session.createLoginSession(email, password, kullaniciAdiServerdan, adiServerdan, soyadiServerdan);

                    /*Intent in = new Intent(KullaniciGirisi.this, Activity_StartMenu.class);
                    in.putExtra("kullaniciAdiServerdan", kullaniciAdiServerdan);
                    in.putExtra("adiServerdan", adiServerdan);
                    in.putExtra("soyadiServerdan", soyadiServerdan);
                    startActivity(in);*/
                    startActivity(new Intent(KullaniciGirisi.this, Activity_StartMenu.class));
                }

                else if (success == 0){

                    Toast.makeText(getApplicationContext(), "Alanlar doldurulmadı", Toast.LENGTH_LONG).show();
                }

                else if (success == 2){

                    Toast.makeText(getApplicationContext(), "Email veya Sifre Yanlış. Lütfen Tekrar Deneyin.", Toast.LENGTH_LONG).show();
                }

                else{

                    Toast.makeText(getApplicationContext(), "Email Adresi Kayıtlı Değil.", Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }
}
