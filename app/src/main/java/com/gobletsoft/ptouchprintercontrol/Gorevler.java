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
import android.widget.Button;
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
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
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
import java.util.List;

public class Gorevler extends AppCompatActivity {

    private Button btnKabul1;
    private Button btnKabul2;
    private Button btnKabul3;
    private Button btnKabul4;
    private Button btnKabul5;
    private Button btnKabul6;
    private Button btnKabul7;

    private Button btnIptal1;
    private Button btnIptal2;
    private Button btnIptal3;
    private Button btnIptal4;
    private Button btnIptal5;
    private Button btnIptal6;
    private Button btnIptal7;

    private TextView tvGorevId1;
    private TextView tvGorevId2;
    private TextView tvGorevId3;
    private TextView tvGorevId4;
    private TextView tvGorevId5;
    private TextView tvGorevId6;
    private TextView tvGorevId7;

    private TextView tvGorevAd1;
    private TextView tvGorevAd2;
    private TextView tvGorevAd3;
    private TextView tvGorevAd4;
    private TextView tvGorevAd5;
    private TextView tvGorevAd6;
    private TextView tvGorevAd7;

    private ProgressDialog pDialog;

    //dont need them.
    private String email;
    private String password;

    private String[] firmaAdlar;
    private String[] firmaIdler;

    int firmaAdSayisi, firmaIdSayisi;

    //php stuff
    private JSONObject json;
    JSONParser jsonParser = new JSONParser();
    private static String url_gorevleri_getir = "http://10.0.0.100:85/ptouchAndroid/gorevlerigetir.php";

    private AccountHeader headerResult = null;
    Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gorevler);

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

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.headerradsan)
                .addProfiles(
                        //profil ekleme kısmı, giriş yapılan verileri al ve ekle.

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

                                startActivity(new Intent(Gorevler.this, KullaniciGirisi.class));
                            }
                        }
                        //istenilen event gerçekleştikten sonra drawer'ı kapat ->
                        return false;
                    }
                })
                .build();

        btnKabul1 = findViewById(R.id.buttonOk1);
        btnKabul2 = findViewById(R.id.buttonOk2);
        btnKabul3 = findViewById(R.id.buttonOk3);
        btnKabul4 = findViewById(R.id.buttonOk4);
        btnKabul5 = findViewById(R.id.buttonOk5);
        btnKabul6 = findViewById(R.id.buttonOk6);
        btnKabul7 = findViewById(R.id.buttonOk7);

        btnIptal1 = findViewById(R.id.buttonCancel1);
        btnIptal2 = findViewById(R.id.buttonCancel2);
        btnIptal4 = findViewById(R.id.buttonCancel4);
        btnIptal3 = findViewById(R.id.buttonCancel3);
        btnIptal5 = findViewById(R.id.buttonCancel5);
        btnIptal6 = findViewById(R.id.buttonCancel6);
        btnIptal7 = findViewById(R.id.buttonCancel7);

        tvGorevId1 = findViewById(R.id.textViewGorev1id);
        tvGorevId2 = findViewById(R.id.textViewGorev2id);
        tvGorevId3 = findViewById(R.id.textViewGorev3id);
        tvGorevId4 = findViewById(R.id.textViewGorev4id);
        tvGorevId5 = findViewById(R.id.textViewGorev5id);
        tvGorevId6 = findViewById(R.id.textViewGorev6id);
        tvGorevId7 = findViewById(R.id.textViewGorev7id);

        tvGorevAd1 = findViewById(R.id.textViewGorev1ad);
        tvGorevAd2 = findViewById(R.id.textViewGorev2ad);
        tvGorevAd3 = findViewById(R.id.textViewGorev3ad);
        tvGorevAd4 = findViewById(R.id.textViewGorev4ad);
        tvGorevAd5 = findViewById(R.id.textViewGorev5ad);
        tvGorevAd6 = findViewById(R.id.textViewGorev6ad);
        tvGorevAd7 = findViewById(R.id.textViewGorev7ad);

        new gorevleriGetir().execute();

        //Toast.makeText(getApplicationContext(), firmaAdlar[0], Toast.LENGTH_LONG).show();

        btnKabul1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Gorevler.this, GorevDetaylar.class));
            }
        });
    }

    //görev sayısı < button sayısından ise fazla olanları gizle.
    public void buttonGizle(){

        for (int i = 2; i > 8; i++){


        }
    }

    //görev sayısı > button sayısından ise yeni button ekle.
    public  void buttonEkle(){


    }

    class gorevleriGetir extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(Gorevler.this);
            pDialog.setMessage("Görev Bilgileri Getiriliyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

           // params.add(new BasicNameValuePair("kullanici_email", email));
           // params.add(new BasicNameValuePair("kullanici_sifre", password));

            json = jsonParser.makeHttpRequest(url_gorevleri_getir,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                JSONArray jArrayFirmaAdlar = json.getJSONArray("firmaAdlar");

                JSONArray jArrayFirmaIdler = json.getJSONArray("firmaIdler");

                firmaAdSayisi = jArrayFirmaAdlar.length();
                firmaIdSayisi = jArrayFirmaIdler.length();

                firmaAdlar = new String[firmaAdSayisi];
                firmaIdler = new String[firmaIdSayisi];

                // firmaAdlar[0] = jArrayFirmaAdlar.getString(0);

                for (int j = 0; j < firmaIdSayisi; j++){

                    firmaIdler[j] = jArrayFirmaIdler.getString(j);

                    System.out.println(firmaIdler[j]);
                }

                for (int i = 0; i <= jArrayFirmaAdlar.length(); i++){

                    firmaAdlar[i] = jArrayFirmaAdlar.getString(i);

                    System.out.println(firmaAdlar[i]);
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            tvGorevId1.setText("" + firmaIdler[0]);
            //tvGorevId2.setText("" + firmaIdler[1]);
            //tvGorevId3.setText(firmaIdler[2]);

            tvGorevAd1.setText("" + firmaAdlar[0]);
            tvGorevAd2.setText("" + firmaAdlar[1]);
            tvGorevAd3.setText("" + firmaAdlar[2]);
        }
    }
}