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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OlcumNoktaDuzenle extends AppCompatActivity {

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
    private static String url_olcumnoktalari_duzenle = "";

    private ProgressDialog pDialog;

    String olcumNoktaId, sebekeTipi, olculenTip, olculenNokta, karakteristik, inn, anaIletkenKesiti, korumaIletkenKesiti,
            kacakAkimRolesi, rx, iaa, raa, olcumeGoreSonuc, kabloyaGoreSonuc, olcumBolumAdi;

    private AlertDialog alertDialog;

    /*
    * KacakAkimRolesi = Integer.parseInt(spKacakAkimRolesi.getSelectedItem().toString());
      In = Integer.parseInt(spIn.getSelectedItem().toString());
    * */

    private String SebekeTipi, OlculenTip, Karakteristik, AnaIletkenKesiti, OlcumBolumAdi, OlculenNokta;
    private int KacakAkimRolesi, In;
    private double KorumaIletkenKesiti, Rx, doubleAnaIletkenKesit;


    private Spinner spSebekeTipi, spOlculenTip, spKarakteristik, spKacakAkimRolesi, spIn, spAnaIletkenKesit;
    private EditText etOlcumBolumAdi, etOlculenNokta, etKorumaIletkenKesiti, etRx;


    //sonucların yazdırıldığı stringler.
    private String kabloyaGore, olcumeGore;
    private Double Iaa, Raa;

    private String StKacakAkimRolesi, StIn, StKorumaIletkenKesiti, StRx, StIaa, StRaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_olcum_nokta_duzenle);

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

        //OlcumnoktaDetaylarından alınan veriler ...
        olcumNoktaId = getIntent().getStringExtra("olcumnoktaid");

        sebekeTipi = getIntent().getStringExtra("sebekeTipi");
        olculenTip = getIntent().getStringExtra("olculenTip");
        karakteristik = getIntent().getStringExtra("karakteristik");
        anaIletkenKesiti = getIntent().getStringExtra("anaIletkenKesiti");
        kacakAkimRolesi = getIntent().getStringExtra("kacakAkimRolesi");
        olcumBolumAdi = getIntent().getStringExtra("olcumBolumAdi");
        olculenNokta = getIntent().getStringExtra("olculenNokta");
        inn = getIntent().getStringExtra("in");
        korumaIletkenKesiti = getIntent().getStringExtra("korumaIletkenKesiti");
        rx = getIntent().getStringExtra("rx");

        /*iaa = getIntent().getStringExtra("iaa");
        raa = getIntent().getStringExtra("raa");
        olcumeGoreSonuc = getIntent().getStringExtra("olcumeGoreSonuc");
        kabloyaGoreSonuc = getIntent().getStringExtra("kabloyaGoreSonuc");*/

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

                                startActivity(new Intent(OlcumNoktaDuzenle.this, Gorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(OlcumNoktaDuzenle.this, DevamEdenGorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 3){

                                startActivity(new Intent(OlcumNoktaDuzenle.this, Activity_Settings.class));
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

        //button Id lerinin kullanılması.
        spSebekeTipi = findViewById(R.id.spinnerDuzenleSebekeTipi);
        spOlculenTip = findViewById(R.id.spinnerDuzenleOlculenTip);
        spKarakteristik = findViewById(R.id.spinnerDuzenleKarakteristik);
        spKacakAkimRolesi = findViewById(R.id.spinnerDuzenleKacakAkimRolesi);
        spIn = findViewById(R.id.spinnerDuzenleIn);
        spAnaIletkenKesit = findViewById(R.id.spinnerDuzenleAnaIletkenKesiti);

        etOlcumBolumAdi = findViewById(R.id.editTextDuzenleOlcumBolumAdi);
        etOlculenNokta = findViewById(R.id.editTextDuzenleOlculenNokta);
        etKorumaIletkenKesiti = findViewById(R.id.editTextDuzenleKorumaIletkenKesiti);
        etRx = findViewById(R.id.editTextDuzenleRx);

        Button btnOlcumNoktaDuzenle = findViewById(R.id.buttonOlcumNoktaDuzenle);
        Button btnOlcumNoktaDuzenleVazgec = findViewById(R.id.buttonOlcumNoktaDuzenleVazgec);

        btnOlcumNoktaDuzenleVazgec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog = new AlertDialog.Builder(OlcumNoktaDuzenle.this)
                        .setTitle("Düzenlemekten Vazgeç?")
                        .setMessage("Ölçüm noktasını düzenlemekten vazgeçmeye emin misiniz?")
                        .setCancelable(false)
                        .setPositiveButton("Evet, Vazgeç.",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {

                                        startActivity(new Intent(OlcumNoktaDuzenle.this, DevamEdenGorevler.class));
                                    }
                                })
                        .setNegativeButton("Hayır, Düzenlemeye Devam Et.",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {


                                    }
                                }).create();
                alertDialog.show();
            }
        });

        btnOlcumNoktaDuzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog = new AlertDialog.Builder(OlcumNoktaDuzenle.this)
                        .setTitle("Nokta Detaylarını Düzenle")
                        .setMessage("Ölçüm noktası düzenleniyor, Onaylıyor musunuz?")
                        .setCancelable(false)
                        .setPositiveButton("Evet, Düzenle.",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {


                                        if (etOlculenNokta.getText().toString().isEmpty() || etKorumaIletkenKesiti.getText().toString().isEmpty() ||
                                                etRx.getText().toString().isEmpty()){

                                            Toast.makeText(getApplicationContext(), "Bolum Adı dışında alan boş bırakılamaz lütfen doldurunuz.", Toast.LENGTH_LONG).show();
                                        }

                                        else{

                                            if (etOlcumBolumAdi.getText().toString().isEmpty()){

                                                etOlcumBolumAdi.setText("-");
                                            }

                                            SebekeTipi = spSebekeTipi.getSelectedItem().toString();
                                            OlculenTip = spOlculenTip.getSelectedItem().toString();
                                            Karakteristik = spKarakteristik.getSelectedItem().toString();
                                            AnaIletkenKesiti = spAnaIletkenKesit.getSelectedItem().toString();
                                            KacakAkimRolesi = Integer.parseInt(spKacakAkimRolesi.getSelectedItem().toString());
                                            In = Integer.parseInt(spIn.getSelectedItem().toString());

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

                                            hesapla(SebekeTipi, OlculenTip, Karakteristik, doubleAnaIletkenKesit,
                                                    KacakAkimRolesi, In, OlcumBolumAdi, OlculenNokta, KorumaIletkenKesiti, Rx);

                                            new olcumnoktaduzenle().execute();
                                        }
                                    }
                                })
                        .setNegativeButton("Hayır, Düzenlemeye Devam Et.",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {


                                    }
                                }).create();
                alertDialog.show();
            }
        });
    }

    class olcumnoktaduzenle extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OlcumNoktaDuzenle.this);
            pDialog.setMessage("Ölçüm Noktası Düzenleniyor.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();


            StKacakAkimRolesi = String.valueOf(KacakAkimRolesi);
            StIn = String.valueOf(In);
            StKorumaIletkenKesiti = String.valueOf(KorumaIletkenKesiti);
            StRx = String.valueOf(Rx);
            StIaa = String.valueOf(Iaa);
            StRaa = String.valueOf(Raa);

            params.add(new BasicNameValuePair("olcumnoktaid", olcumNoktaId));
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

            json = jsonParser.makeHttpRequest(url_olcumnoktalari_duzenle,"GET", params);


            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                int kontrol = json.getInt("success");

                if (kontrol == 1){

                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(OlcumNoktaDuzenle.this, DevamEdenGorevler.class));
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
