package com.gobletsoft.ptouchprintercontrol;

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
import android.widget.AdapterView;
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

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Gorevler extends AppCompatActivity {

    ArrayList<AtananGorevlerDataModel> atananGorevlerDataModels;
    ListView listView;
    private static AtananGorevlerCustomAdapter adapter;

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
    private static String url_atanangorevleri_getir = "http://10.0.0.100:85/ptouchAndroid/atanangorevlerigetir.php";

    private ProgressDialog pDialog;

    int firmaAdSayisi, firmaIdSayisi, lokasyonSayisi, olcumdurumdegerSayisi;

    private String[] firmaAdlar;
    private String[] firmaIdler;
    private String[] lokasyonlar;
    private String[] olcumdurumdegerler;

    //devam eden görev türünü 2 olarak aldık. Görev detaylara bu şekilde haber veriyoruz nereden geldiğini.
    private int gorevTuru = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gorevler);

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

                                startActivity(new Intent(Gorevler.this, LabelOlustur.class));
                            }

                            else if(drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(Gorevler.this, Gorevler.class));
                            }

                            else if(drawerItem.getIdentifier() == 3){

                                //startActivity(new Intent(Activity_StartMenu.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 4){


                            }

                            else if (drawerItem.getIdentifier() == 5){

                                startActivity(new Intent(Gorevler.this, Activity_Settings.class));
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

        new atanangorevlerigetir().execute();

        listView = findViewById(R.id.listViewAtananGorevler);

        atananGorevlerDataModels = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AtananGorevlerDataModel atananGorevlerDataModel = atananGorevlerDataModels.get(position);

                Intent in = new Intent(Gorevler.this, GorevDetaylar.class);
                in.putExtra("lokasyonadi", atananGorevlerDataModel.getLokasyonadi());

                in.putExtra("firmaadi", atananGorevlerDataModel.getFirmaadi());
                //gorev türünü detayları yazdırdığımız sayfaya yolla.
                in.putExtra("gorevTuru", gorevTuru);
                startActivity(in);
                //Toast.makeText(getApplicationContext(), atananGorevlerDataModel.getFirmaadi()+"\n"+ atananGorevlerDataModel.getLokasyonadi(), Toast.LENGTH_LONG).show();
            }
        });
    }

    class atanangorevlerigetir extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(Gorevler.this);
            pDialog.setMessage("Atanmış Görevler Alınıyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            try {

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();

                params.add(new BasicNameValuePair("kullaniciemail", emailSession));
                // params.add(new BasicNameValuePair("kullanici_sifre", password));

                json = jsonParser.makeHttpRequest(url_atanangorevleri_getir,
                        "POST", params);

                // check log cat for response
                Log.d("Create Response", json.toString());

                return null;
            }

            catch (Exception e) {

                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Bağlantı sağlanamadı, lütfen ağ ayarlarınızı kontrol edin.", Toast.LENGTH_LONG).show();

                return null;
            }
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                JSONArray jArrayFirmaAdlar = json.getJSONArray("firmaAdlar");
                JSONArray jArrayFirmaIdler = json.getJSONArray("firmaIdler");
                JSONArray jArrayLokasyonlar = json.getJSONArray("lokasyonlar");
                JSONArray jArrayOlcumdurumdegerler = json.getJSONArray("olcumdurumdegerler");
                int success = json.getInt("success");

                if (success == 0){

                    Toast.makeText(getApplicationContext(), "Bağlantı sağlanamadı, lütfen ağ ayarlarınızı kontrol edin.", Toast.LENGTH_LONG).show();
                }

                firmaAdSayisi = jArrayFirmaAdlar.length();
                firmaIdSayisi = jArrayFirmaIdler.length();
                lokasyonSayisi = jArrayLokasyonlar.length();
                olcumdurumdegerSayisi = jArrayOlcumdurumdegerler.length();

                firmaAdlar = new String[firmaAdSayisi];
                firmaIdler = new String[firmaIdSayisi];
                lokasyonlar = new String[lokasyonSayisi];
                olcumdurumdegerler = new String[olcumdurumdegerSayisi];

                /*private String[] lokasyonlar;
                private String[] olcumdurumdegerler;*/

                // firmaAdlar[0] = jArrayFirmaAdlar.getString(0);

                for (int j = 0; j < firmaIdSayisi; j++){

                    firmaIdler[j] = jArrayFirmaIdler.getString(j);

                    System.out.println("firmaId: " + firmaIdler[j]);
                }

                for (int i = 0; i < firmaAdSayisi; i++){

                    firmaAdlar[i] = jArrayFirmaAdlar.getString(i);

                    System.out.println("firmaAd: " + firmaAdlar[i]);
                }

                for (int k = 0; k < lokasyonSayisi; k++){

                    lokasyonlar[k] = jArrayLokasyonlar.getString(k);

                    System.out.println("lokasyon: " + lokasyonlar[k]);
                }

                for (int l = 0; l < olcumdurumdegerSayisi; l++){

                    olcumdurumdegerler[l] = jArrayOlcumdurumdegerler.getString(l);

                    System.out.println("olcumdeger: " + olcumdurumdegerler[l]);
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            for (int i = 0;  i < firmaIdSayisi; i++){

                atananGorevlerDataModels.add(new AtananGorevlerDataModel(firmaAdlar[i], lokasyonlar[i]));
            }

            //adapter= new DevamEdenGorevlerCustomAdapter(devamEdenGorevlerDataModels, getApplicationContext());
            adapter = new AtananGorevlerCustomAdapter(atananGorevlerDataModels, getApplicationContext());

            listView.setAdapter(adapter);
        }
    }
}