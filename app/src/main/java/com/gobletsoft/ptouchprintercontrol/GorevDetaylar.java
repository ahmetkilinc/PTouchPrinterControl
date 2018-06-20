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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GorevDetaylar extends AppCompatActivity {

    private AccountHeader headerResult = null;
    Drawer result;

    private ProgressDialog pDialog;

    //php connections
    JSONParser jsonParser = new JSONParser();
    private static String url_gorev_detaylarini_getir = "http://10.0.0.100:85/ptouchAndroid/gorevdetaylarinigetir.php";
    private static final String TAG_SUCCESS = "success";
    private JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gorev_detaylar);


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
                        //profile

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

                                startActivity(new Intent(GorevDetaylar.this, KullaniciGirisi.class));
                            }

                        }
                        //istenilen event gerçekleştikten sonra drawer'ı kapat ->
                        return false;
                    }
                })
                .build();




        TextView tvID = findViewById(R.id.textViewIdValue);
        TextView tvFirmaAdi = findViewById(R.id.textViewFirmNameValue);
        TextView tvKonum = findViewById(R.id.textViewFirmLocationValue);
        TextView tvIlgiliKisi = findViewById(R.id.textViewRelatedPersonValue);
        TextView tvAdres = findViewById(R.id.textViewAddressValue);
        TextView tvIl = findViewById(R.id.textViewCountryValue);
        TextView tvIlce = findViewById(R.id.textViewDistrictValue);
        TextView tvTelefon = findViewById(R.id.textViewPhoneValue);
        TextView tvEposta = findViewById(R.id.textViewEmailValue);
        TextView tvKontrolSebebi = findViewById(R.id.textViewControlReasonValue);
        TextView tvAciklama = findViewById(R.id.textViewStatementValue);
        TextView tvOlcumPersoneli = findViewById(R.id.textViewMeasurementStaffValue);

        new gorevdetaylarinigetir().execute();

        Button btnMeasure = findViewById(R.id.buttonMeasure);

        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //gittiği yeri değiştir.
                startActivity(new Intent(GorevDetaylar.this, OlcumOrtamBilgileri.class));
            }
        });
    }

    class gorevdetaylarinigetir extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(GorevDetaylar.this);
            pDialog.setMessage("Görevin Detayları Açılıyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("firmaId", "2039"));

            json = jsonParser.makeHttpRequest(url_gorev_detaylarini_getir,
                    "POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                int kontrol = json.getInt("kontrol");

                if (kontrol == 1){

                    String firmaAdi = json.getString("firmaAdi");
                    String lokasyonAdi = json.getString("lokasyonAdi");
                    String ilgiliKisi = json.getString("ilgiliKisi");
                    String adres = json.getString("adres");
                    String ilAdi = json.getString("ilAdi");
                    String ilceAdi = json.getString("ilceAdi");
                    String telefon = json.getString("telefon");
                    String email = json.getString("email");
                    String kontrolNedeni = json.getString("kontrolNedeni");
                    String aciklama = json.getString("aciklama");
                    String userAdi = json.getString("userAdi");

                    Toast.makeText(getApplicationContext(), "firma adı: " + firmaAdi + " " + lokasyonAdi + ilgiliKisi + adres +
                            ilAdi + ilceAdi + telefon + email + kontrolNedeni + aciklama + userAdi, Toast.LENGTH_LONG).show();
                }

                else{

                    Toast.makeText(getApplicationContext(), "Havada hata kokusu var...", Toast.LENGTH_LONG).show();
                }

            }
            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }
}