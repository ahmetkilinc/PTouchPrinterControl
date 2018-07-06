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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class OlcumOrtamBilgileri extends AppCompatActivity {

    private String Marka, Model, SeriNumarasi, KalibrasyonYapanKurum, KalibrasyonTarihi, KalibrasyonGecerlilikSuresi;

    private String TemelTopraklayiciSekli, DerinTopraklayiciSekli, RingTopraklayiciSekli, BelirsizTopraklayiciSekli, TopraklayiciSekli;

    private String TesiseAitProje, HavaDurumu, ToprakDurumu;

    private AccountHeader headerResult = null;
    Drawer result;

    String GorevDetayId;

    private ProgressDialog pDialog;

    //php connections
    JSONParser jsonParser = new JSONParser();
    private static String url_olcum_ortam_bilgileri_ekle = "";
    private static final String TAG_SUCCESS = "success";
    private JSONObject json;

    // Session Manager Class
    SessionManager session;
    private String kullaniciAdiSession;
    private String adiSession;
    private String soyadiSession;
    private String emailSession;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_olcum_ortam_bilgileri);

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
            startActivity(new Intent(OlcumOrtamBilgileri.this, KullaniciGirisi.class));
        }

        GorevDetayId = getIntent().getStringExtra("GorevDetayId");

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

                                startActivity(new Intent(OlcumOrtamBilgileri.this, LabelOlustur.class));
                            }

                            else if(drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(OlcumOrtamBilgileri.this, Gorevler.class));
                            }

                            else if(drawerItem.getIdentifier() == 3){

                                //startActivity(new Intent(Activity_StartMenu.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 4){


                            }

                            else if (drawerItem.getIdentifier() == 5){

                                startActivity(new Intent(OlcumOrtamBilgileri.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 6){

                                session.logoutUser();

                                Intent i = new Intent(getApplicationContext(), KullaniciGirisi.class);
                                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK | i.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }
                        //istenilen event gerçekleştikten sonra drawer'ı kapat ->
                        return false;
                    }
                })
                .build();

        final EditText etMarka = findViewById(R.id.editTextMarka);
        final EditText etModel = findViewById(R.id.editTextModel);
        final EditText etSeriNumarasi = findViewById(R.id.editTextSerialNo);

        final EditText etKalibrasyonYapanKurum = findViewById(R.id.editTextKalibrasyonYapanKurum);
        final EditText etKalibrasyonTarihi = findViewById(R.id.editTextKalibrasyonTarihi);
        final EditText etKalibrasyonGecerlilikSuresi = findViewById(R.id.editTextKalibrasyonGecerlilikSuresi);

        final RadioGroup rgTesiseAitProje = findViewById(R.id.RadioGrouptesiseAitProje);
        final RadioGroup rgHavaDurumu = findViewById(R.id.RadioGroupHavaDurumu);
        final RadioGroup rgToprakDurumu = findViewById(R.id.RadioButtonToprakDurumu);

        final CheckBox cbTemel = findViewById(R.id.checkBoxTemel);
        final CheckBox cbDerin = findViewById(R.id.checkBoxDerin);
        final CheckBox cbRing = findViewById(R.id.checkBoxRing);
        final CheckBox cbBelirsiz = findViewById(R.id.checkBoxBelirsiz);


        Button btnKaydet = findViewById(R.id.buttonKaydet);

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMarka.getText().toString().isEmpty() || etModel.getText().toString().isEmpty() || etSeriNumarasi.getText().toString().isEmpty() ||
                        etKalibrasyonYapanKurum.getText().toString().isEmpty() || etKalibrasyonTarihi.getText().toString().isEmpty() ||
                        etKalibrasyonGecerlilikSuresi.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Lütfen Bilgileri Doldurunuz.", Toast.LENGTH_LONG).show();
                }

                else{

                    Marka = etMarka.getText().toString();
                    Model = etModel.getText().toString();
                    SeriNumarasi = etSeriNumarasi.getText().toString();
                    KalibrasyonYapanKurum = etKalibrasyonYapanKurum.getText().toString();
                    KalibrasyonTarihi = etKalibrasyonTarihi.getText().toString();
                    KalibrasyonGecerlilikSuresi = etKalibrasyonGecerlilikSuresi.getText().toString();

                    //checkboxlar
                    if (cbTemel.isChecked()){

                        TemelTopraklayiciSekli = cbTemel.getText().toString();
                    }
                    else{
                        //seçili olmadığını 0 ile ifade ettik.
                        TemelTopraklayiciSekli = "0";
                    }
                    if (cbDerin.isChecked()){

                        DerinTopraklayiciSekli = cbDerin.getText().toString();
                    }
                    else{

                        DerinTopraklayiciSekli = "0";
                    }

                    if (cbRing.isChecked()){

                        RingTopraklayiciSekli = cbRing.getText().toString();
                    }
                    else{

                        RingTopraklayiciSekli = "0";
                    }

                    if (cbBelirsiz.isChecked()){

                        BelirsizTopraklayiciSekli = cbBelirsiz.getText().toString();
                    }
                    else{

                        BelirsizTopraklayiciSekli = "0";
                    }

                    TopraklayiciSekli = TemelTopraklayiciSekli + "-" + DerinTopraklayiciSekli + "-" + RingTopraklayiciSekli + "-" + BelirsizTopraklayiciSekli;

                    //radio buttons
                    int tesiseAitProje = rgTesiseAitProje.getCheckedRadioButtonId();
                    RadioButton rbTesiseAitProje =findViewById(tesiseAitProje);
                    TesiseAitProje = rbTesiseAitProje.getText().toString();

                    int havaDurumu = rgHavaDurumu.getCheckedRadioButtonId();
                    RadioButton rbHavaDurumu = findViewById(havaDurumu);
                    HavaDurumu = rbHavaDurumu.getText().toString();

                    int toprakDurumu = rgToprakDurumu.getCheckedRadioButtonId();
                    RadioButton rbToprakDurumu = findViewById(toprakDurumu);
                    ToprakDurumu = rbToprakDurumu.getText().toString();

                    //Toast.makeText(getApplicationContext(), TemelTopraklayiciSekli + " * " + DerinTopraklayiciSekli + " * " + RingTopraklayiciSekli + " * " + BelirsizTopraklayiciSekli, Toast.LENGTH_LONG).show();

                    new olcumOrtamBilgileriEkle().execute();
                }

                //startActivity(new Intent(OlcumOrtamBilgileri.this, OlcumNoktalariEkle.class));
            }
        });
    }

    class olcumOrtamBilgileriEkle extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OlcumOrtamBilgileri.this);
            pDialog.setMessage("Ölçüm Ortam Bilgileri Kaydediliyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("gorevDetayId", GorevDetayId));
            params.add(new BasicNameValuePair("marka", Marka));
            params.add(new BasicNameValuePair("model", Model));
            params.add(new BasicNameValuePair("seriNo", SeriNumarasi));
            params.add(new BasicNameValuePair("kalibrasyonYapanKurum", KalibrasyonYapanKurum));
            params.add(new BasicNameValuePair("kalibrasyonTarihi", KalibrasyonTarihi));
            params.add(new BasicNameValuePair("kalibrasyonGecerlilikSuresi", KalibrasyonGecerlilikSuresi));
            params.add(new BasicNameValuePair("projeVarmi", TesiseAitProje));
            params.add(new BasicNameValuePair("havaDurumu", HavaDurumu));
            params.add(new BasicNameValuePair("toprakDurumu", ToprakDurumu));
            params.add(new BasicNameValuePair("tesisSekli", TopraklayiciSekli));

            json = jsonParser.makeHttpRequest(url_olcum_ortam_bilgileri_ekle,
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

                    Toast.makeText(getApplicationContext(), "Kayıt Başarılı.", Toast.LENGTH_LONG).show();

                    Intent in = new Intent(OlcumOrtamBilgileri.this, OlcumNoktalariEkle.class);
                    in.putExtra("gorevDetayId", GorevDetayId);
                    startActivity(in);
                    //startActivity(new Intent(OlcumOrtamBilgileri.this, OlcumNoktalariEkle.class));
                }

                else{

                    Toast.makeText(getApplicationContext(), "Kayıt Başarısız.", Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed()
    {

        alertDialog = new AlertDialog.Builder(OlcumOrtamBilgileri.this)
                .setTitle("Ölçüm Ortam Bilgisi")
                .setMessage("Ölçüm ortam bilgisi eklemeden çıkarsanız bu lokasyondaki görevi kabul etmemiş olursunuz, lütfen bilgileri giriniz.")
                .setCancelable(false)
                .setPositiveButton("Anladım, geri gitmek istiyorum.",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {

                                //new gorevikabulet().execute();
                                startActivity(new Intent(OlcumOrtamBilgileri.this, Gorevler.class));
                            }
                        })
                .setNegativeButton("Vazgeç ve ölçüm noktasını gir.",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {

                            }
                        }).create();
        alertDialog.show();
    }
}
