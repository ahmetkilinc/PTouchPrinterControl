package com.gobletsoft.ptouchprintercontrol;

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
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import java.util.ArrayList;
import java.util.List;

public class OlcumOrtamBilgileri extends AppCompatActivity {

    private String Marka, Model, SeriNumarasi, KalibrasyonYapanKurum, KalibrasyonTarihi, KalibrasyonGecerlilikSuresi;

    private String TemelTopraklayiciSekli, DerinTopraklayiciSekli, RingTopraklayiciSekli, BelirsizTopraklayiciSekli;

    private String TesiseAitProje, HavaDurumu, ToprakDurumu;

    private AccountHeader headerResult = null;
    Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_olcum_ortam_bilgileri);

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

                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
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

        PrimaryDrawerItem itemBasaDon = new PrimaryDrawerItem().withIdentifier(1).withName(getString(R.string.dn_go_back)).withSelectable(false).withIcon(
                R.drawable.basadon);

        PrimaryDrawerItem itemTumSonuclariGor = new PrimaryDrawerItem().withIdentifier(2).withName(getString(R.string.dn_new_label)).withSelectable(false).withIcon(
                R.drawable.sonuclar);

        PrimaryDrawerItem itemAyarlar = new PrimaryDrawerItem().withIdentifier(3).withName(getString(R.string.dn_settings)).withSelectable(false).withIcon(
                R.drawable.ayarlar);

        PrimaryDrawerItem itemCikisYap = new PrimaryDrawerItem().withIdentifier(4).withName(getString(R.string.dn_close)).withSelectable(false).withIcon(
                R.drawable.cikis);
        //SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.navigation_item_settings);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemText,
                        itemBasaDon,
                        itemTumSonuclariGor,
                        new DividerDrawerItem(),
                        itemAyarlar,
                        itemCikisYap
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null){

                            if (drawerItem.getIdentifier() == 1){

                                //startActivity(new Intent(Activity_StartMenu.this, Activity_Settings.class));
                            }

                            else if(drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(OlcumOrtamBilgileri.this, LabelOlustur.class));
                            }

                            else if(drawerItem.getIdentifier() == 3){

                                startActivity(new Intent(OlcumOrtamBilgileri.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 4){

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


        /* radiobutton değerlerini bu şekilde al.
        int tesiseAitProje = rgTesiseAitProje.getCheckedRadioButtonId();

        RadioButton rbTesiseAitProje = findViewById(tesiseAitProje);

        rbTesiseAitProje.getText();
        */


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

                    Toast.makeText(getApplicationContext(), TemelTopraklayiciSekli + " * " + DerinTopraklayiciSekli + " * " + RingTopraklayiciSekli + " * " + BelirsizTopraklayiciSekli, Toast.LENGTH_LONG).show();
                }

                startActivity(new Intent(OlcumOrtamBilgileri.this, OlcumNoktalariEkle.class));
            }
        });
    }
}