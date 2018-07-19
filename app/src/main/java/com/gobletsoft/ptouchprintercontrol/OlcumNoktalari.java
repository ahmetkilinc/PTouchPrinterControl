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

public class OlcumNoktalari extends AppCompatActivity {

    ArrayList<OlcumNoktalariDataModel> olcumNoktalariDataModels;
    ListView listView;
    private static OlcumNoktalariCustomAdapter adapter;

    private String OlcumBolumAdi;
    private String SebekeTipi;
    private String OlculenTip;
    private String OlculenNokta;
    private String Karakteristik;
    private Integer In;
    private Double AnaIletkenKesit;
    private Double KorumaIletkenKesit;
    private Integer KacakAkimRolesi;
    private Double Rx;
    private Double Iaa;
    private Double Raa;
    private String KabloyaGoreSonuc;
    private String OlcumeGoreSonuc;

    private AccountHeader headerResult = null;
    Drawer result;

    // Session Manager Class
    SessionManager session;
    private String kullaniciAdiSession;
    private String adiSession;
    private String soyadiSession;
    private String emailSession;

    //php stuff
    private JSONObject json;
    JSONParser jsonParser = new JSONParser();
    private static String url_olcumnoktalarini_getir = "http://10.0.0.100:85/ptouchAndroid/olcumnoktalarinigetir.php";

    private ProgressDialog pDialog;

    private String olcumYeriId;

    private String[] olcumBolumAdlar;
    private String[] olculenNoktalar;

    int olcumBolumAdSayisi;
    int olculenNoktaSayisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_olcum_noktalari);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        // sessiondan kullanıcı bilgilerini al
        HashMap<String, String> user = session.getUserDetails();

        kullaniciAdiSession = user.get(SessionManager.KEY_KULLANICIADI);
        adiSession = user.get(SessionManager.KEY_ADI);
        soyadiSession = user.get(SessionManager.KEY_SOYADI);
        emailSession = user.get(SessionManager.KEY_NAME);

        //session var mı kontrol et, yok ise Giriş sayfasına at.
        if (adiSession == null || adiSession.isEmpty()){

            Toast.makeText(getApplicationContext(), "Lütfen Giriş Yapınız.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(OlcumNoktalari.this, KullaniciGirisi.class));
        }

        //al al al al
        olcumYeriId = getIntent().getStringExtra("olcumyeriid");

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

                                startActivity(new Intent(OlcumNoktalari.this, Gorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(OlcumNoktalari.this, DevamEdenGorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 3){

                                startActivity(new Intent(OlcumNoktalari.this, Activity_Settings.class));
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

        Button btnGeriDon = findViewById(R.id.buttonGeriDonOlcumNoktalari);
        Button btnYeniOlcumNoktasi = findViewById(R.id.buttonYeniOlcumNoktasi);

        btnGeriDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(OlcumNoktalari.this, Activity_StartMenu.class));
            }
        });

        btnYeniOlcumNoktasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(OlcumNoktalari.this, OlcumNoktalariEkle.class);
                in.putExtra("olcumyeriid", olcumYeriId);
                startActivity(in);
            }
        });

        listView = findViewById(R.id.listViewOlcumNoktalari);

        olcumNoktalariDataModels = new ArrayList<>();

        new olcumnoktalarinigetir().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OlcumNoktalariDataModel olcumNoktalariDataModel = olcumNoktalariDataModels.get(position);

                //Toast.makeText(getApplicationContext(), olcumNoktalariDataModel.getLokasyonadi() + devamEdenGorevlerDataModel.getFirmaadi(), Toast.LENGTH_LONG).show();

                Intent in = new Intent(OlcumNoktalari.this, OlcumNoktaDetaylar.class);
                in.putExtra("olcumbolumadi", olcumNoktalariDataModel.getOlcumbolumadi());
                in.putExtra("olculennokta", olcumNoktalariDataModel.getOlculennokta());
                startActivity(in);
            }
        });
    }

    class olcumnoktalarinigetir extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OlcumNoktalari.this);
            pDialog.setMessage("Olcum Noktaları Getiriliyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("olcumyeriid", olcumYeriId));

            json = jsonParser.makeHttpRequest(url_olcumnoktalarini_getir,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                JSONArray jArrayOlcumBolumAdlar = json.getJSONArray("olcumbolumadlar");
                JSONArray jArrayOlculenNoktalar = json.getJSONArray("olculennoktalar");

                olcumBolumAdSayisi = jArrayOlcumBolumAdlar.length();
                olculenNoktaSayisi = jArrayOlculenNoktalar.length();

                olcumBolumAdlar = new String[olcumBolumAdSayisi];
                olculenNoktalar = new String[olculenNoktaSayisi];

                for (int j = 0; j < olcumBolumAdSayisi; j++){

                    olcumBolumAdlar[j] = jArrayOlcumBolumAdlar.getString(j);

                    System.out.println("firmaId: " + olcumBolumAdlar[j]);
                }

                for (int i = 0; i < olculenNoktaSayisi; i++){

                    olculenNoktalar[i] = jArrayOlculenNoktalar.getString(i);

                    System.out.println("firmaAd: " + olculenNoktalar[i]);
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            for (int i = 0;  i < olcumBolumAdSayisi; i++){

                String a = i + "";

                olcumNoktalariDataModels.add(new OlcumNoktalariDataModel(olcumBolumAdlar[i],olculenNoktalar[i], a));
            }

            adapter = new OlcumNoktalariCustomAdapter(olcumNoktalariDataModels, getApplicationContext());

            listView.setAdapter(adapter);
        }
    }
}