package com.gobletsoft.ptouchprintercontrol;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import org.json.JSONObject;

import java.util.HashMap;

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
    private static String url_olcumnoktadetaylar_getir = "";

    private ProgressDialog pDialog;


    String olcumNoktaId, sebekeTipi, olculenTip, olculenNokta, karakteristik, inn, anaIletkenKesiti, korumaIletkenKesiti,
            kacakAkimRolesi, rx, iaa, raa, olcumeGoreSonuc, kabloyaGoreSonuc, olcumBolumAdi;


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

        /*
        *       inten.putExtra("olcumnoktaid", olcumNoktaIdDB);
                inten.putExtra("sebekeTipi", sebekeTipi);
                inten.putExtra("olculenTip", olculenTip);
                inten.putExtra("olculenNokta", olculenNokta);
                inten.putExtra("karakteristik", karakteristik);
                inten.putExtra("in", in);
                inten.putExtra("anaIletkenKesiti", anaIletkenKesiti);
                inten.putExtra("korumaIletkenKesiti", korumaIletkenKesiti);
                inten.putExtra("kacakAkimRolesi", kacakAkimRolesi);
                inten.putExtra("rx", rx);
                inten.putExtra("iaa", iaa);
                inten.putExtra("raa", raa);
                inten.putExtra("olcumeGoreSonuc", olcumeGoreSonuc);
                inten.putExtra("kabloyaGoreSonuc", kabloyaGoreSonuc);
        *
        * */

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
        Spinner spSebekeTipi = findViewById(R.id.spinnerDuzenleSebekeTipi);
        Spinner spOlculenTip = findViewById(R.id.spinnerDuzenleOlculenTip);
        Spinner spKarakteristik = findViewById(R.id.spinnerDuzenleKarakteristik);
        Spinner spKacakAkimRolesi = findViewById(R.id.spinnerDuzenleKacakAkimRolesi);
        Spinner spIn = findViewById(R.id.spinnerDuzenleIn);
        Spinner spAnaIletkenKesit = findViewById(R.id.spinnerDuzenleAnaIletkenKesiti);

        EditText etOlcumBolumAdi = findViewById(R.id.editTextDuzenleOlcumBolumAdi);
        EditText etOlculenNokta = findViewById(R.id.editTextDuzenleOlculenNokta);
        EditText etKorumaIletkenKesiti = findViewById(R.id.editTextDuzenleKorumaIletkenKesiti);
        EditText etRx = findViewById(R.id.editTextDuzenleRx);



    }
}
