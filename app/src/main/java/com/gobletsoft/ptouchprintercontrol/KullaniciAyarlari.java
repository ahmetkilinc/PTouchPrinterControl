package com.gobletsoft.ptouchprintercontrol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KullaniciAyarlari extends AppCompatActivity {

    private ProgressDialog pDialog;

    //php connections
    JSONParser jsonParser = new JSONParser();
    private static String url_hesapbilgilerini_getir = "http://10.0.0.100:85/ptouchAndroid/hesapbilgilerinial.php";
    private static String url_hesapbilgilerini_guncelle = "http://10.0.0.100:85/ptouchAndroid/hesapbilgileriniguncelle.php";
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

    // Session Manager Class
    SessionManager session;
    //private String kullaniciAdiSession;
    private String adiSession;
    private String soyadiSession;
    //private String emailSession;

    private AccountHeader headerResult = null;
    Drawer result;

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
        //kullaniciAdiSession = user.get(SessionManager.KEY_KULLANICIADI);
        adiSession = user.get(SessionManager.KEY_ADI);
        soyadiSession = user.get(SessionManager.KEY_SOYADI);
        //emailSession = user.get(SessionManager.KEY_NAME);
        //tvEmail.setText(email + sifre);


        //session var mı kontrol et, yok ise Giriş sayfasına at.
        if (adiSession == null || adiSession.isEmpty()){

            Toast.makeText(getApplicationContext(), "Lütfen Giriş Yapınız.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(KullaniciAyarlari.this, KullaniciGirisi.class));
        }






        //navigation drawer header

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {

                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {

                    return DrawerUIUtils.getPlaceHolder(ctx);
                }
                else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {

                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                }
                else if ("customUrlItem".equals(tag)) {

                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
        //image loader logic.

        //profil eklendiği zaman düzenle. ->

        //final IProfile profile = new ProfileDrawerItem().withName(displayName).withEmail(displayEmail).withIcon(displayPhotoUrl).withIdentifier(100);
        final IProfile profile = new ProfileDrawerItem().withName(adiSession + " " + soyadiSession).withEmail(email).withIdentifier(100);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.headerradsan)
                .addProfiles(
                        profile

                        //new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIdentifier(PROFILE_SETTING)
                        //new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001)
                )
                .withSavedInstance(savedInstanceState)
                .build();


        //adding navigation drawer
        final Toolbar toolbar = findViewById(R.id.toolbar);

        new DrawerBuilder().withActivity(this).build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem itemText = new PrimaryDrawerItem().withName("").withSelectable(false);

        PrimaryDrawerItem itemAtananGorevler = new PrimaryDrawerItem().withIdentifier(1).withName("Atanan Görevler").withSelectable(false).withIcon(
                R.drawable.kabuledilengorev);

        PrimaryDrawerItem itemDevamEdenGorevler = new PrimaryDrawerItem().withIdentifier(2).withName("Devam Eden Görevler").withSelectable(false).withIcon(
                R.drawable.tamamlanmisgorev);

        PrimaryDrawerItem itemAyarlar = new PrimaryDrawerItem().withIdentifier(3).withName(getString(R.string.dn_settings)).withSelectable(false).withIcon(
                R.drawable.ayarlar);

        PrimaryDrawerItem itemKapat = new PrimaryDrawerItem().withIdentifier(4).withName(getString(R.string.dn_close)).withSelectable(false).withIcon(
                R.drawable.cikis);
        //SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.navigation_item_settings);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemText,
                        itemAtananGorevler,
                        itemDevamEdenGorevler,
                        new DividerDrawerItem(),
                        itemAyarlar,
                        itemKapat
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null){

                            if(drawerItem.getIdentifier() == 1){

                                startActivity(new Intent(KullaniciAyarlari.this, Gorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(KullaniciAyarlari.this, DevamEdenGorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 3){

                                startActivity(new Intent(KullaniciAyarlari.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 4){

                                session.logoutUser();

                                Intent i = new Intent(getApplicationContext(), KullaniciGirisi.class);
                                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK | i.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                //startActivity(new Intent(Activity_StartMenu.this, KullaniciGirisi.class));
                            }

                        }
                        //istenilen event gerçekleştikten sonra drawer'ı kapat ->
                        return false;
                    }
                })
                .build();











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