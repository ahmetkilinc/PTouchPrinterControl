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
    private String olcumyeriid;

    private int successDetaylar, successKabul;

    private String lokasyonAdiGelen, firmaAdiGelen;
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
        lokasyonAdiGelen = getIntent().getStringExtra("lokasyonadi");
        firmaAdiGelen = getIntent().getStringExtra("firmaadi");
        //gorev türünü atanan veya devam eden olarak al. atanan=2, devameden=1;
        gorevTuru = getIntent().getIntExtra("gorevTuru", 0);

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

                                startActivity(new Intent(GorevDetaylar.this, Gorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(GorevDetaylar.this, DevamEdenGorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 3){

                                startActivity(new Intent(GorevDetaylar.this, Activity_Settings.class));
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

        new gorevdetaylargetir().execute();

        listView = findViewById(R.id.listViewGorevDetaylar);

        gorevDetaylarDataModels = new ArrayList<>();

        Button btnGeriDon = findViewById(R.id.buttonGeriGorevDetaylar);
        Button btnIlerle = findViewById(R.id.buttonIlerleGorevDetay);
        Button btnTumOlcumNoktalari = findViewById(R.id.buttonTumOlcumNoktalari);

        btnTumOlcumNoktalari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(GorevDetaylar.this, OlcumNoktalari.class);
                in.putExtra("olcumyeriid", olcumyeriid);
                startActivity(in);
                //startActivity(new Intent(GorevDetaylar.this, OlcumNoktalari.class));
            }
        });

        btnGeriDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(GorevDetaylar.this, DevamEdenGorevler.class));
            }
        });

        if (gorevTuru == 1){

            btnIlerle.setText("Ölçüm Noktası Ekle");

            btnIlerle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent in = new Intent(GorevDetaylar.this, OlcumNoktalariEkle.class);
                    in.putExtra("olcumyeriid", olcumyeriid);
                    in.putExtra("lokasyonadi", lokasyonadi);
                    in.putExtra("firmaadi", firmaadi);
                    startActivity(in);;
                }
            });
        }
        else if (gorevTuru == 2){

            btnIlerle.setText("Kabul Et");

            btnTumOlcumNoktalari.setVisibility(View.GONE);

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

                                            Intent in = new Intent(GorevDetaylar.this, OlcumOrtamBilgileri.class);
                                            in.putExtra("olcumyeriid", olcumyeriid);
                                            in.putExtra("lokasyonadi", lokasyonadi);
                                            in.putExtra("firmaadi", firmaadi);
                                            startActivity(in);
                                        }
                                    })
                            .setNegativeButton("Reddet",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(final DialogInterface dialog,
                                                            final int which) {

                                            Intent in = new Intent(GorevDetaylar.this, Gorevler.class);
                                            in.setFlags(in.FLAG_ACTIVITY_CLEAR_TOP | in.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(in);
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

            params.add(new BasicNameValuePair("lokasyon", lokasyonAdiGelen));
            params.add(new BasicNameValuePair("firmaadi", firmaAdiGelen));

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
                olcumyeriid = json.getString("olcumyeriid");
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

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
