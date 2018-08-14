package com.gobletsoft.ptouchprintercontrol;

// ekledikten sonra Çıktı Al checkbox'ı tikli ise çıktı alım ekranına gönder.

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.util.function.Predicate.isEqual;

public class OlcumNoktalariEkle extends AppCompatActivity {

    private String SebekeTipi, OlculenTip, Karakteristik, AnaIletkenKesiti;
    private String OlcumBolumAdi, OlculenNokta;
    private Integer KacakAkimRolesi, In;
    private Double doubleAnaIletkenKesit, KorumaIletkenKesiti, Rx;
    private Double Iaa, Raa;

    private String StKacakAkimRolesi, StIn, StKorumaIletkenKesiti, StRx, StIaa, StRaa;

    private AccountHeader headerResult = null;
    Drawer result;

    private String kabloyaGore, olcumeGore;

    private CheckBox cbYazdir;
    private String formattedDate;
    private String formattedSaat;

    private String olcumYeriId;

    private ProgressDialog pDialog;

    //php connections
    JSONParser jsonParser = new JSONParser();
    private static String url_olcum_noktalari_ekle = "";
    private static final String TAG_SUCCESS = "success";
    private JSONObject json;

    // Session Manager Class
    SessionManager session;
    private String kullaniciAdiSession;
    private String adiSession;
    private String soyadiSession;
    private String emailSession;

    private AlertDialog alertDialog;

    private Calendar takvim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_olcum_noktalari_ekle);

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
            startActivity(new Intent(OlcumNoktalariEkle.this, KullaniciGirisi.class));
        }

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
                R.drawable.cikis);        //SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.navigation_item_settings);

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

                                startActivity(new Intent(OlcumNoktalariEkle.this, Gorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(OlcumNoktalariEkle.this, DevamEdenGorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 3){

                                startActivity(new Intent(OlcumNoktalariEkle.this, Activity_Settings.class));
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

        final Spinner sSebekeTipi = findViewById(R.id.spinnerSebekeTipi);
        final Spinner sOlculenTip = findViewById(R.id.spinnerOlculenTip);
        final Spinner sKarakteristik = findViewById(R.id.spinnerKarakteristik);
        final Spinner sAnaIletkenKesiti = findViewById(R.id.spinnerAnaIletkenKesiti);
        final Spinner sKacakAkimRolesi = findViewById(R.id.spinnerKacakAkimRolesi);
        final Spinner sIn = findViewById(R.id.spinnerIn);

        final EditText etOlcumBolumAdi = findViewById(R.id.editTextOlcumBolumAdi);
        final EditText etOlculenNokta = findViewById(R.id.editTextOlculenNokta);
        final EditText etKorumaIletkenKesiti = findViewById(R.id.editTextKorumaIletkenKesiti);
        final EditText etRx = findViewById(R.id.editTextRx);

        cbYazdir = findViewById(R.id.checkBoxYazdir);

        Button btnEkle = findViewById(R.id.buttonEkle);

        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etOlculenNokta.getText().toString().isEmpty() || etKorumaIletkenKesiti.getText().toString().isEmpty() ||
                        etRx.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Bölüm Adı dışında alan boş bırakılamaz lütfen doldurunuız.", Toast.LENGTH_LONG).show();
                }

                else{

                    if (etOlcumBolumAdi.getText().toString().isEmpty()){

                        etOlcumBolumAdi.setText("-");
                    }


                    SebekeTipi = sSebekeTipi.getSelectedItem().toString();
                    OlculenTip = sOlculenTip.getSelectedItem().toString();
                    Karakteristik = sKarakteristik.getSelectedItem().toString();
                    AnaIletkenKesiti = sAnaIletkenKesiti.getSelectedItem().toString();
                    KacakAkimRolesi = Integer.parseInt(sKacakAkimRolesi.getSelectedItem().toString());
                    In = Integer.parseInt(sIn.getSelectedItem().toString());

                    //Toast.makeText(getApplicationContext(), In.toString(), Toast.LENGTH_LONG).show();

                    OlcumBolumAdi = etOlcumBolumAdi.getText().toString();
                    OlculenNokta = etOlculenNokta.getText().toString();
                    KorumaIletkenKesiti = Double.parseDouble(etKorumaIletkenKesiti.getText().toString());
                    Rx = Double.parseDouble(etRx.getText().toString());


                    switch (AnaIletkenKesiti) {
                        case "0":

                            doubleAnaIletkenKesit = 0.0;
                            break;

                        case "2x1,5":

                            doubleAnaIletkenKesit = 1.5;
                            break;
                        case "2x2,5":

                            doubleAnaIletkenKesit = 2.5;
                            break;
                        case "2x4":

                            doubleAnaIletkenKesit = 4.0;
                            break;
                        case "2x6":

                            doubleAnaIletkenKesit = 6.0;
                            break;
                        case "2x10":

                            doubleAnaIletkenKesit = 10.0;
                            break;
                        case "2x16":

                            doubleAnaIletkenKesit = 16.0;
                            break;
                        case "2x25":

                            doubleAnaIletkenKesit = 25.0;
                            break;
                        case "2x35":

                            doubleAnaIletkenKesit = 35.0;
                            break;
                        case "2x50":

                            doubleAnaIletkenKesit = 50.0;
                            break;
                        case "2x70":

                            doubleAnaIletkenKesit = 70.0;
                            break;
                        case "2x95":

                            doubleAnaIletkenKesit = 95.0;
                            break;
                        case "2x120":

                            doubleAnaIletkenKesit = 120.0;
                            break;
                        case "2x150":

                            doubleAnaIletkenKesit = 150.0;
                            break;
                        case "2x185":

                            doubleAnaIletkenKesit = 185.0;
                            break;
                        case "2x240":

                            doubleAnaIletkenKesit = 240.0;
                            break;
                        case "2x300":

                            doubleAnaIletkenKesit = 300.0;
                            break;
                        case "3x1,5":

                            doubleAnaIletkenKesit = 1.5;
                            break;
                        case "3x2,5":

                            doubleAnaIletkenKesit = 2.5;
                            break;
                        case "3x4":

                            doubleAnaIletkenKesit = 4.0;
                            break;
                        case "3x10":

                            doubleAnaIletkenKesit = 10.0;
                            break;
                        case "3x16":

                            doubleAnaIletkenKesit = 16.0;
                            break;
                        case "3x25":

                            doubleAnaIletkenKesit = 25.0;
                            break;
                        case "3x35":

                            doubleAnaIletkenKesit = 35.0;
                            break;
                        case "3x50":

                            doubleAnaIletkenKesit = 50.0;
                            break;
                        case "3x70":

                            doubleAnaIletkenKesit = 70.0;
                            break;
                        case "3x95":

                            doubleAnaIletkenKesit = 95.0;
                            break;
                        case "3x120":

                            doubleAnaIletkenKesit = 120.0;
                            break;
                        case "3x150":

                            doubleAnaIletkenKesit = 150.0;
                            break;
                        case "3x185":

                            doubleAnaIletkenKesit = 185.0;
                            break;
                        case "3x240":

                            doubleAnaIletkenKesit = 240.0;
                            break;
                        case "3x300":

                            doubleAnaIletkenKesit = 300.0;
                            break;
                        case "4x1,5":

                            doubleAnaIletkenKesit = 1.5;
                            break;
                        case "4x2,5":

                            doubleAnaIletkenKesit = 2.5;
                            break;
                        case "4x4":

                            doubleAnaIletkenKesit = 4.0;
                            break;
                        case "4x6":

                            doubleAnaIletkenKesit = 6.0;
                            break;
                        case "4x10":

                            doubleAnaIletkenKesit = 10.0;
                            break;
                        case "4x16":

                            doubleAnaIletkenKesit = 16.0;
                            break;
                        case "4x25":

                            doubleAnaIletkenKesit = 25.0;
                            break;
                        case "4x35":

                            doubleAnaIletkenKesit = 35.0;
                            break;
                        case "4x50":

                            doubleAnaIletkenKesit = 50.0;
                            break;
                        case "4x70":

                            doubleAnaIletkenKesit = 70.0;
                            break;
                        case "4x95":

                            doubleAnaIletkenKesit = 95.0;
                            break;
                        case "4x120":

                            doubleAnaIletkenKesit = 120.0;
                            break;
                        case "4x150":

                            doubleAnaIletkenKesit = 150.0;
                            break;
                        case "4x185":

                            doubleAnaIletkenKesit = 185.0;
                            break;
                        case "4x240":

                            doubleAnaIletkenKesit = 240.0;
                            break;
                        case "4x300":

                            doubleAnaIletkenKesit = 300.0;
                            break;
                        case "3x16+10":

                            doubleAnaIletkenKesit = 16.0;
                            break;
                        case "3x50+25":

                            doubleAnaIletkenKesit = 50.0;
                            break;
                        case "3x70+35":

                            doubleAnaIletkenKesit = 70.0;
                            break;
                        case "3x95+50":

                            doubleAnaIletkenKesit = 95.0;
                            break;
                        case "3x120+70":

                            doubleAnaIletkenKesit = 120.0;
                            break;
                        case "3x150+70":

                            doubleAnaIletkenKesit = 150.0;
                            break;
                        case "3x185+95":

                            doubleAnaIletkenKesit = 185.0;
                            break;
                        case "3x240+120":

                            doubleAnaIletkenKesit = 240.0;
                            break;
                        case "3x300+150":

                            doubleAnaIletkenKesit = 300.0;
                            break;
                        case "4x(1x4)":

                            doubleAnaIletkenKesit = 4.0;
                            break;
                        case "4x(1x6)":

                            doubleAnaIletkenKesit = 6.0;
                            break;
                        case "4x(1x10)":

                            doubleAnaIletkenKesit = 10.0;
                            break;
                        case "4x(1x16)":

                            doubleAnaIletkenKesit = 16.0;
                            break;
                        case "4x(1x25)":

                            doubleAnaIletkenKesit = 25.0;
                            break;
                        case "4x(1x35)":

                            doubleAnaIletkenKesit = 35.0;
                            break;
                        case "4x(1x50)":

                            doubleAnaIletkenKesit = 50.0;
                            break;
                        case "4x(1x70)":

                            doubleAnaIletkenKesit = 70.0;
                            break;
                        case "4x(1x95)":

                            doubleAnaIletkenKesit = 95.0;
                            break;
                        case "4x(1x120)":

                            doubleAnaIletkenKesit = 120.0;
                            break;
                        case "4x(1x150)":

                            doubleAnaIletkenKesit = 150.0;
                            break;
                        case "4x(1x185)":

                            doubleAnaIletkenKesit = 185.0;
                            break;
                        case "4x(1x240)":

                            doubleAnaIletkenKesit = 240.0;
                            break;
                        case "4x(1x300)":

                            doubleAnaIletkenKesit = 300.0;
                            break;
                        case "4x(1x400)":

                            doubleAnaIletkenKesit = 400.0;
                            break;
                        case "4x(1x500)":

                            doubleAnaIletkenKesit = 500.0;
                            break;
                        case "4x(1x630)":

                            doubleAnaIletkenKesit = 630.0;
                            break;
                        case "5x4":

                            doubleAnaIletkenKesit = 4.0;
                            break;
                        case "5x6":

                            doubleAnaIletkenKesit = 6.0;
                            break;
                        case "5x10":

                            doubleAnaIletkenKesit = 10.0;
                            break;
                        case "5x16":

                            doubleAnaIletkenKesit = 16.0;
                            break;
                        case "5x25":

                            doubleAnaIletkenKesit = 25.0;
                            break;
                        case "5x35":

                            doubleAnaIletkenKesit = 35.0;
                            break;
                        case "5x50":

                            doubleAnaIletkenKesit = 50.0;
                            break;
                        case "5x70":

                            doubleAnaIletkenKesit = 70.0;
                            break;
                        case "5x95":

                            doubleAnaIletkenKesit = 95.0;
                            break;
                        case "5x120":

                            doubleAnaIletkenKesit = 120.0;
                            break;
                        case "5x150":

                            doubleAnaIletkenKesit = 150.0;
                            break;
                        case "5x185":

                            doubleAnaIletkenKesit = 185.0;
                            break;
                        case "5x240":

                            doubleAnaIletkenKesit = 240.0;
                            break;
                        case "5x300":

                            doubleAnaIletkenKesit = 300.0;
                            break;
                    }

                    //verileri hesapla fonksiyonuna yolla.
                    hesapla(SebekeTipi, OlculenTip, Karakteristik, doubleAnaIletkenKesit,
                            KacakAkimRolesi, In, OlcumBolumAdi, OlculenNokta, KorumaIletkenKesiti, Rx);

                    new olcumNoktalariEkle().execute();
                }
            }
        });
    }

    class olcumNoktalariEkle extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OlcumNoktalariEkle.this);
            pDialog.setMessage("Ölçüm Ortam Bilgileri Kaydediliyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

           /*
           * private Integer KacakAkimRolesi, In;
            private Double doubleAnaIletkenKesit, KorumaIletkenKesiti, Rx;
            private Double Iaa, Raa;
           *
           * */

            StKacakAkimRolesi = String.valueOf(KacakAkimRolesi);
            StIn = String.valueOf(In);
            StKorumaIletkenKesiti = String.valueOf(KorumaIletkenKesiti);
            StRx = String.valueOf(Rx);
            StIaa = String.valueOf(Iaa);
            StRaa = String.valueOf(Raa);

            params.add(new BasicNameValuePair("olcumYeriId", olcumYeriId));
            params.add(new BasicNameValuePair("sebekeTipi", SebekeTipi));
            params.add(new BasicNameValuePair("olcumBolumAdi", OlcumBolumAdi));
            params.add(new BasicNameValuePair("olculenTip", OlculenTip));
            params.add(new BasicNameValuePair("olculenNokta", OlculenNokta));
            params.add(new BasicNameValuePair("karakteristik", Karakteristik));
            params.add(new BasicNameValuePair("inn", StIn));
            params.add(new BasicNameValuePair("anaIletkenKesiti", AnaIletkenKesiti));
            params.add(new BasicNameValuePair("korumaIletkenKesiti", StKorumaIletkenKesiti));
            params.add(new BasicNameValuePair("kacakAkimRolesi", StKacakAkimRolesi));
            params.add(new BasicNameValuePair("rx", StRx));
            params.add(new BasicNameValuePair("iaa", StIaa));
            params.add(new BasicNameValuePair("raa", StRaa));
            params.add(new BasicNameValuePair("sonucOlcumeGore", olcumeGore));
            params.add(new BasicNameValuePair("sonucKabloyaGore", kabloyaGore));

            json = jsonParser.makeHttpRequest(url_olcum_noktalari_ekle,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                int kontrol = json.getInt("success");

                if (kontrol == 1){

                    if (cbYazdir.isChecked()){

                        //Toast.makeText(getApplicationContext(), "Checked!", Toast.LENGTH_LONG).show();
                        labelOlustur(Rx, OlcumBolumAdi, OlculenNokta);
                    }

                    else{

                        //eğer checkbox işaretli ise bu kısmı gösterme!, değilse göster
                        alertDialog = new AlertDialog.Builder(OlcumNoktalariEkle.this)
                                .setTitle("Başarılı!")
                                .setMessage("Ölçüm Noktası Başarıyla Eklendi. Yeni ölçüm noktası eklemek için YENİ EKLE ye, Ölçüm noktalarını görmek için ÖLÇÜM NOKTALARI na tıkla.")
                                .setCancelable(false)
                                .setPositiveButton("Yeni Ekle",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(final DialogInterface dialog, final int which) {

                                                Intent in = new Intent(OlcumNoktalariEkle.this, OlcumNoktalariEkle.class);
                                                in.putExtra("olcumyeriid", olcumYeriId);
                                                startActivity(in);
                                            }
                                        })
                                .setNegativeButton("Ölçüm Noktaları",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(final DialogInterface dialog, final int which) {

                                                Intent in = new Intent(OlcumNoktalariEkle.this, OlcumNoktalari.class);
                                                in.putExtra("olcumyeriid", olcumYeriId);
                                                startActivity(in);
                                            }
                                        }).create();
                        alertDialog.show();

                        //startActivity(new Intent(OlcumNoktalariEkle.this, OlcumNoktalari.class));
                    }
                }

                else{

                    Toast.makeText(getApplicationContext(), "Başarısız. Lütfen Tekrar Deneyin.", Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    public void hesapla(String SebekeTipi, String OlculenTip, String Karakteristik, Double doubleAnaIletkenKesit,
                        Integer KacakAkimRolesi, Integer In, String OlcumBolumAdi, String OlculenNokta, Double KorumaIletkenKesiti, Double Rx){

        if (doubleAnaIletkenKesit == 0.0){

            kabloyaGore = "UYGUNDUR";
        }

        else{

            if (doubleAnaIletkenKesit <= 16.0 && doubleAnaIletkenKesit.equals(KorumaIletkenKesiti)){

                kabloyaGore = "UYGUNDUR";
            }

            else if ((doubleAnaIletkenKesit > 16.0 && doubleAnaIletkenKesit <= 35.0) && KorumaIletkenKesiti == 16.0){

                kabloyaGore = "UYGUNDUR";
            }

            else if (doubleAnaIletkenKesit > 35.0 && ((doubleAnaIletkenKesit / 2) == KorumaIletkenKesiti)){

                kabloyaGore = "UYGUNDUR";
            }

            else{

                kabloyaGore = "UYGUNDEGIL";
            }
        }

        switch (OlculenTip) {
            case "Pano":

                if (SebekeTipi == "TT"){

                    if (In > 35){

                        Iaa = null;
                    }
                    else{

                        if (Karakteristik.equals("B")){

                            Iaa = (double) In * 5;
                        }
                        else{

                            Iaa = (double) In * 10;
                        }
                    }

                    if (KacakAkimRolesi == 0){

                        if (Iaa == null){

                            Raa = null;
                        }
                        else{

                            Raa = 50 / Iaa;
                        }
                    }

                    else if (KacakAkimRolesi == 30){

                        Raa = 1666.0;
                    }

                    else if (KacakAkimRolesi == 300){

                        Raa = 166.0;
                    }
                }

                else{

                    if (In > 160){

                        Iaa = null;
                    }

                    else{

                        if (Karakteristik == "B"){

                            Iaa = (double) In * 5;
                        }

                        else{

                            Iaa = (double) In * 10;
                        }
                    }

                    if (KacakAkimRolesi == 0){

                        if (Iaa == null){

                            Raa = null;
                        }

                        else{

                            Raa = 50 / Iaa;
                        }
                    }

                    else if (KacakAkimRolesi == 30){

                        Raa = 1666.0;
                    }

                    else if (KacakAkimRolesi == 300){

                        Raa = 166.0;
                    }
                }

                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Trafo Koruma":

                Raa = 2.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Trafo İşletme":

                Raa = 2.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Tank":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Parafadur":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Enh Direk":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "OG Direk":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Pompa":

                Raa = 1.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Eşpotansiyel Bara":

                Raa = 1.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Tel Cit":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Statik Levha":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Lift":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Vinc":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;

            case "Diğerleri":

                Raa = 5.0;
                olcumeGore = olcumSonuc(Raa, Rx);
                break;
        }

        Toast.makeText(getApplicationContext(), olcumeGore + " & " + kabloyaGore + " & " + Rx, Toast.LENGTH_LONG).show();

    }

    public void labelOlustur(double Rx, String OlcumBolumAdi, String OlculenNokta){

        takvim = Calendar.getInstance();

        //tarihi belirlemek
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yy");
        final String formattedDate = dateformat.format(takvim.getTime());

        //saati belirlemek
        SimpleDateFormat dateformatSaat = new SimpleDateFormat("HH:mm");
        final String formattedSaat = dateformatSaat.format(takvim.getTime());

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.labeltemplate);

        final Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        final Canvas c = new Canvas(bmp);

        String textOlcumDegeri = Rx + "";
        Paint p = new Paint();
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p.setTextSize(220);
        p.setColor(Color.BLACK);

        //yazının fotoda nerede olacağı (aşağı yukarı)
        int yPos = (int) (c.getHeight() / 2.30);

        String textAciklama = OlcumBolumAdi;
        Paint p1 = new Paint();
        p1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p1.setTextSize(220);
        p1.setColor(Color.BLACK);

        int yPosAciklama = (int) (c.getHeight() / 1.45);

        String textAciklama2 = OlculenNokta;
        Paint p2 = new Paint();
        p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p2.setTextSize(220);
        p2.setColor(Color.BLACK);

        int yPosAciklama2 = (int) (c.getHeight() / 1.07);

        String Tarih = formattedDate;
        Paint p3 = new Paint();
        p3.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p3.setTextSize(170);
        p3.setColor(Color.BLACK);

        int yPosAciklama3 = (int) (c.getHeight() / 8);

        String saat = formattedSaat;
        Paint p4 = new Paint();
        p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p4.setTextSize(170);
        p4.setColor(Color.BLACK);

        int yPosAciklama4 = (int)  (c.getHeight() / 4.5 );

        c.drawText(textOlcumDegeri, (c.getWidth() / 6), yPos, p);
        c.drawText(textAciklama, (c.getWidth() / 15), yPosAciklama, p1);
        c.drawText(textAciklama2, (c.getWidth() / 15), yPosAciklama2, p2);
        c.drawText(Tarih, (c.getWidth() - (c.getWidth() / 3)), yPosAciklama3, p3);
        c.drawText(saat, (c.getWidth() - (c.getWidth() / 3)), yPosAciklama4, p4);

        final BitmapDrawable drawable = new BitmapDrawable(getResources(), bmp);

        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".png";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();

        try {

            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.flush();
            out.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(OlcumNoktalariEkle.this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {

                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

        String hop = "/storage/emulated/0/Pictures/saved_images/Image-" + n + ".png";

        Intent i = new Intent(OlcumNoktalariEkle.this, Activity_PrintImage.class);
        i.putExtra("labelAdress", hop);
        startActivity(i);
    }

    public String olcumSonuc(Double Raa, Double Rx){

        if (Raa == null){

            Raa = 0.0;
        }

        if (Raa >= Rx){

            return "UYGUN";
        }

        if (Raa < Rx){

            Double FARK;

            FARK = Rx - Raa;

            if (FARK < 1){

                return "UYGUN***";
            }

            else{

                return "UYGUNDEGIL";
            }
        }

        else{

            return "UYGUNDEGIL";
        }
    }
}
