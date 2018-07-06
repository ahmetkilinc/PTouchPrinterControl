package com.gobletsoft.ptouchprintercontrol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AndroidException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GorevDetaylar extends AppCompatActivity {

    ArrayList<GorevDetaylarDataModel> gorevDetaylarDataModels;
    ListView listView;
    private static GorevDetaylarCustomAdapter adapter;

    // Session Manager Class
    SessionManager session;
    private String kullaniciAdiSession;
    private String adiSession;
    private String soyadiSession;
    private String emailSession;

    //drawer
    private AccountHeader headerResult = null;
    Drawer result;

    //php stuff
    private JSONObject json;
    JSONParser jsonParser = new JSONParser();

    private static String url_gorevdetaylar_getir = "";
    private static String url_gorevi_kabul_et = "";

    private ProgressDialog pDialog;

    private String firmaidd;

    private String firmaadi;
    private String ilgilikisi;
    private String adres;
    private String ilid;
    private String ilceid;
    private String telefon;
    private String email;
    private String kontrolnedeni;
    private String userid;
    private String olcumdurumid;
    private String aciklama;
    private String lokasyonadi;
    private String lokasyonil;
    private String lokasyonilce;
    private String olcumdurumadi;
    private String planlananolcumtarihi;
    private int successDetaylar, successKabul;

    private String lokasyonAdi, firmaAdi;
    private int gorevTuru;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gorev_detaylar);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        kullaniciAdiSession = user.get(SessionManager.KEY_KULLANICIADI);
        adiSession = user.get(SessionManager.KEY_ADI);
        soyadiSession = user.get(SessionManager.KEY_SOYADI);
        emailSession = user.get(SessionManager.KEY_NAME);

        if (adiSession == null || adiSession.isEmpty()){

            Toast.makeText(getApplicationContext(), "Lütfen Giriş Yapınız.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), KullaniciGirisi.class));
        }

        //lokasyon adını atanan veya devam eden görevlerden al.
        lokasyonAdi = getIntent().getStringExtra("lokasyonadi");
        firmaAdi = getIntent().getStringExtra("firmaadi");
        //gorev türünü atanan veya devam eden olarak al. atanan=2, devameden=1;
        gorevTuru = getIntent().getIntExtra("gorevTuru", 0);

        Toast.makeText(getApplicationContext(), "almakta sıkıntı yok? : " + lokasyonAdi, Toast.LENGTH_LONG).show();

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
        final IProfile profile = new ProfileDrawerItem().withName(adiSession + " " + soyadiSession).withEmail(emailSession).withIdentifier(100);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.headerradsan)
                .addProfiles(
                        //profil ekleme kısmı, giriş yapılan verileri al ve ekle.
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

        PrimaryDrawerItem itemYeniEtiket = new PrimaryDrawerItem().withIdentifier(1).withName(getString(R.string.dn_new_label)).withSelectable(false).withIcon(
                R.drawable.newlabel);

        PrimaryDrawerItem itemGorevler = new PrimaryDrawerItem().withIdentifier(2).withName(getString(R.string.dn_gorevler)).withSelectable(false).withIcon(
                R.drawable.gorevler);

        PrimaryDrawerItem itemKabuledilenGorevler = new PrimaryDrawerItem().withIdentifier(3).withName(getString(R.string.dn_kabul_edilen_gorevler)).withSelectable(false).withIcon(
                R.drawable.kabuledilengorev);

        PrimaryDrawerItem itemTamamlanmisGorevler = new PrimaryDrawerItem().withIdentifier(4).withName(getString(R.string.dn_tamamlanmis_gorevler)).withSelectable(false).withIcon(
                R.drawable.tamamlanmisgorev);

        PrimaryDrawerItem itemAyarlar = new PrimaryDrawerItem().withIdentifier(5).withName(getString(R.string.dn_settings)).withSelectable(false).withIcon(
                R.drawable.ayarlar);

        PrimaryDrawerItem itemKapat = new PrimaryDrawerItem().withIdentifier(6).withName(getString(R.string.dn_close)).withSelectable(false).withIcon(
                R.drawable.cikis);
        //SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.navigation_item_settings);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemText,
                        itemYeniEtiket,
                        itemGorevler,
                        itemKabuledilenGorevler,
                        itemTamamlanmisGorevler,
                        new DividerDrawerItem(),
                        itemAyarlar,
                        itemKapat
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null){

                            if (drawerItem.getIdentifier() == 1){

                                startActivity(new Intent(GorevDetaylar.this, LabelOlustur.class));
                            }

                            else if(drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(GorevDetaylar.this, Gorevler.class));
                            }

                            else if(drawerItem.getIdentifier() == 3){

                                //startActivity(new Intent(Activity_StartMenu.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 4){


                            }

                            else if (drawerItem.getIdentifier() == 5){

                                startActivity(new Intent(GorevDetaylar.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 6){

                                session.logoutUser();

                                Intent i = new Intent(getApplicationContext(), KullaniciGirisi.class);
                                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK | i.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                //startActivity(new Intent(getApplicationContext(), KullaniciGirisi.class));
                            }
                        }
                        //istenilen event gerçekleştikten sonra drawer'ı kapat ->
                        return false;
                    }
                })
                .build();

        new gorevdetaylargetir().execute();

        listView = findViewById(R.id.listViewGorevDetaylar);

        gorevDetaylarDataModels = new ArrayList<>();

        Button btnGeriDon = findViewById(R.id.buttonGeriGorevDetaylar);
        Button btnIlerle = findViewById(R.id.buttonIlerleGorevDetay);

        btnGeriDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(GorevDetaylar.this, Activity_StartMenu.class));
            }
        });

        if (gorevTuru == 1){

            btnIlerle.setText("Ölçüm Noktası Ekle");

            btnIlerle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(GorevDetaylar.this, OlcumNoktalariEkle.class));
                }
            });
        }
        else if (gorevTuru == 2){

            btnIlerle.setText("Kabul Et");

            btnIlerle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                            alertDialog = new AlertDialog.Builder(GorevDetaylar.this)
                            .setTitle("Görevi Kabul Et")
                            .setMessage("Sizin Adınıza Atanmış Görevi Kabul Etmek için Onaylayınız. (Reddettiğiniz görev başkasına atanana kadar burada durmaya devam edecektir.)")
                            .setCancelable(false)
                            .setPositiveButton("Kabul Et",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(final DialogInterface dialog, final int which) {

                                            //new gorevikabulet().execute();
                                            startActivity(new Intent(GorevDetaylar.this, OlcumOrtamBilgileri.class));
                                        }
                                    })
                            .setNegativeButton("Reddet",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(final DialogInterface dialog,
                                                            final int which) {

                                            Intent in = new Intent(GorevDetaylar.this, Gorevler.class);
                                            in.setFlags(in.FLAG_ACTIVITY_CLEAR_TOP | in.FLAG_ACTIVITY_CLEAR_TASK | in.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(in);
                                            //startActivity(new Intent(GorevDetaylar.this, Gorevler.class));

                                        }
                                    }).create();
                            alertDialog.show();

                    //startActivity(new Intent(GorevDetaylar.this, OlcumOrtamBilgileri.class));
                }
            });
        }

        else{

            btnIlerle.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Lütfen anasayfaya dönerek tekrar deneyiniz.", Toast.LENGTH_LONG).show();
        }
    }

    class gorevikabulet extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(GorevDetaylar.this);
            pDialog.setMessage("Seçilen Görev Lokasyonunun Detayları Yükleniyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("lokasyon", lokasyonAdi));
            params.add(new BasicNameValuePair("firmaadi", firmaAdi));

            json = jsonParser.makeHttpRequest(url_gorevi_kabul_et,"GET", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                //ilgilikisi = json.getString("olcumdurumadi");

                successKabul = json.getInt("success");

                if (successKabul == 1){

                    Intent in = new Intent(GorevDetaylar.this, OlcumOrtamBilgileri.class);
                    in.putExtra("firmaadi", firmaAdi);
                    in.putExtra("lokasyonadi", lokasyonAdi);
                    startActivity(in);
                }

                else{

                    Toast.makeText(getApplicationContext(), "Görev kabul edilirken hata meydana geldi, lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    class gorevdetaylargetir extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(GorevDetaylar.this);
            pDialog.setMessage("Seçilen Görev Lokasyonunun Detayları Yükleniyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("lokasyon", lokasyonAdi));
            params.add(new BasicNameValuePair("firmaadi", firmaAdi));

            json = jsonParser.makeHttpRequest(url_gorevdetaylar_getir,"GET", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                //ilgilikisi = json.getString("olcumdurumadi");

                successDetaylar = json.getInt("success");
                firmaidd = json.getString("firmaid");
                firmaadi = json.getString("firmaadi");
                ilgilikisi = json.getString("ilgilikisi");
                adres = json.getString("adres");
                ilid = json.getString("ilid");
                ilceid = json.getString("ilceid");
                telefon = json.getString("telefon");
                email = json.getString("email");
                kontrolnedeni = json.getString("kontrolnedeni");
                userid = json.getString("userid");
                olcumdurumid = json.getString("olcumdurumid");
                aciklama = json.getString("aciklama");
                lokasyonadi = json.getString("lokasyonadi");
                lokasyonil = json.getString("lokasyonil");
                lokasyonilce = json.getString("lokasyonilce");
                olcumdurumadi = json.getString("olcumdurumadi");
                planlananolcumtarihi = json.getString("planlananolcumtarihi");

            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            /*for (int i = 0;  i < firmaIdSayisi; i++){

                devamEdenGorevlerDataModels.add(new DevamEdenGorevlerDataModel(firmaAdlar[i], lokasyonlar[i]));
            }*/

            //Toast.makeText(getApplicationContext(), "hooyo: " + success, Toast.LENGTH_LONG).show();

            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("Firma Adı:", firmaadi));
            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("Lokasyon:", lokasyonadi));
            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("Lokasyon İl:", lokasyonil));
            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("Lokasyon İlçe:", lokasyonilce));
            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("İlgili Kişi:", ilgilikisi));
            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("E-Posta:", email));
            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("Kontrol Nedeni:", kontrolnedeni));
            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("Ölçüm Durumu:", olcumdurumadi));
            gorevDetaylarDataModels.add(new GorevDetaylarDataModel("P. Ölçüm Tarihi:", planlananolcumtarihi));

            adapter= new GorevDetaylarCustomAdapter(gorevDetaylarDataModels, getApplicationContext());

            listView.setAdapter(adapter);
        }
    }
}
