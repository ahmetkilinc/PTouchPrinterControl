package com.gobletsoft.ptouchprintercontrol;

import android.content.Context;
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
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class LabelOlustur extends AppCompatActivity {

    private AccountHeader headerResult = null;
    Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_label_olustur);

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

                                startActivity(new Intent(LabelOlustur.this, LabelOlustur.class));
                            }

                            else if(drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(LabelOlustur.this, Gorevler.class));
                            }

                            else if(drawerItem.getIdentifier() == 3){

                                //startActivity(new Intent(Activity_StartMenu.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 4){


                            }

                            else if (drawerItem.getIdentifier() == 5){

                                startActivity(new Intent(LabelOlustur.this, Activity_Settings.class));
                            }

                            else if (drawerItem.getIdentifier() == 6){

                                startActivity(new Intent(LabelOlustur.this, KullaniciGirisi.class));
                            }

                        }
                        //istenilen event gerçekleştikten sonra drawer'ı kapat ->
                        return false;
                    }
                })
                .build();

        final EditText etOlcumDegeri = findViewById(R.id.editTextOlcumDegeri);
        final EditText etAciklama1 = findViewById(R.id.editTextAciklama1);
        final EditText etAciklama2 = findViewById(R.id.editTextAciklama2);

        //final ImageView ivBitmap = findViewById(R.id.imageViewBm);


        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
        final String formattedDate = df.format(c.getTime());

        SimpleDateFormat dfSaat = new SimpleDateFormat("HH:mm");
        final String formattedSaat = dfSaat.format(c.getTime());


        Button btnGonder = findViewById(R.id.buttonGonder);


        btnGonder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (etOlcumDegeri.getText().toString().isEmpty() || etAciklama1.getText().toString().isEmpty() || etAciklama2.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), getString(R.string.toast_message_label_olustur), Toast.LENGTH_LONG).show();
                }
                else {

                    double olcumDegeri = Double.parseDouble(etOlcumDegeri.getText().toString());
                    String aciklama1 = etAciklama1.getText().toString();
                    String aciklama2 = etAciklama2.getText().toString();

                    Bitmap bitmap = BitmapFactory.decodeResource(
                            getResources(), R.drawable.labeltemplate);

                    final Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    final Canvas c = new Canvas(bmp);

                    String textOlcumDegeri = olcumDegeri + "";
                    Paint p = new Paint();
                    p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    p.setTextSize(160);
                    p.setColor(Color.BLACK);

                    //yazının fotoda nerede olacağı (aşağı yukarı)
                    int yPos = (int) (c.getHeight() / 1.95);

                    String textAciklama = aciklama1;
                    Paint p1 = new Paint();
                    p1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    p1.setTextSize(160);
                    p1.setColor(Color.BLACK);

                    int yPosAciklama = (int) (c.getHeight() / 1.35);

                    String textAciklama2 = aciklama2;
                    Paint p2 = new Paint();
                    p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    p2.setTextSize(160);
                    p2.setColor(Color.BLACK);

                    int yPosAciklama2 = (int) (c.getHeight() / 1.04);

                    String Tarih = formattedDate;
                    Paint p3 = new Paint();
                    p3.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    p3.setTextSize(130);
                    p3.setColor(Color.BLACK);

                    int yPosAciklama3 = (int) (c.getHeight() / 5.5);


                    String saat = formattedSaat;
                    Paint p4 = new Paint();
                    p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    p4.setTextSize(130);
                    p4.setColor(Color.BLACK);

                    int yPosAciklama4 = (int)  (c.getHeight() / 3.5 );

                    c.drawText(textOlcumDegeri, (c.getWidth() / 6), yPos, p);
                    c.drawText(textAciklama, (c.getWidth() / 6), yPosAciklama, p1);
                    c.drawText(textAciklama2, (c.getWidth() / 6), yPosAciklama2, p2);
                    c.drawText(Tarih, (c.getWidth() - (c.getWidth() / 4)), yPosAciklama3, p3);
                    c.drawText(saat, (c.getWidth() - (c.getWidth() / 4)), yPosAciklama4, p4);

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
                    MediaScannerConnection.scanFile(LabelOlustur.this, new String[]{file.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {

                                public void onScanCompleted(String path, Uri uri) {

                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });


                    String hop = "/storage/emulated/0/Pictures/saved_images/Image-" + n + ".png";

                    Intent i = new Intent(LabelOlustur.this, Activity_PrintImage.class);
                    i.putExtra("labelAdress", hop);
                    startActivity(i);
                }
            }
        });
    }
}