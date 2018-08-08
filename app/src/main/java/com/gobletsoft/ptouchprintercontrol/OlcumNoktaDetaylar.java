package com.gobletsoft.ptouchprintercontrol;

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

public class OlcumNoktaDetaylar extends AppCompatActivity {

    ArrayList<OlcumNoktaDetaylarDataModel> olcumNoktaDetaylarDataModels;
    ListView listView;
    private static OlcumNoktaDetaylarCustomAdapter adapter;

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
    private static String url_olcumnoktadetaylar_getir = "http://10.0.0.100:85/ptouchAndroid/olcumnoktadetaylarinigetir.php";

    private ProgressDialog pDialog;

    private String olcumBolumAdiTY, olculenNoktaTY;

    private String olcumNoktaIdDB;
    private String olcumYeriIdDB;
    private String sebekeTipi;
    private String olcumBolumAdi;
    private String olculenTip;
    private String olculenNokta;
    private String karakteristik;
    private String in;
    private String anaIletkenKesiti;
    private String korumaIletkenKesiti;
    private String kacakAkimRolesi;
    private String rx;
    private String iaa;
    private String raa;
    private String olcumeGoreSonuc;
    private String kabloyaGoreSonuc;

    private Calendar takvim;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_olcum_nokta_detaylar);

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

        olcumBolumAdiTY = getIntent().getStringExtra("olcumbolumadi");
        olculenNoktaTY = getIntent().getStringExtra("olculennokta");

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

                                startActivity(new Intent(OlcumNoktaDetaylar.this, Gorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(OlcumNoktaDetaylar.this, DevamEdenGorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 3){

                                startActivity(new Intent(OlcumNoktaDetaylar.this, Activity_Settings.class));
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

        Button btnCiktiAl = findViewById(R.id.buttonCiktiAlOlcumNoktaDetay);
        Button btnGeriDon = findViewById(R.id.buttonGeriOlcumNoktaDetaylar);
        Button btnDuzenle = findViewById(R.id.buttonOlcumNoktaDuzenle);
        Button btnSil = findViewById(R.id.buttonSil);

        listView = findViewById(R.id.listViewOlcumNoktaDetaylar);

        olcumNoktaDetaylarDataModels = new ArrayList<>();

        new olcumnoktadetaylarinigetir().execute();

        //çıktıyı yollanacak fotonon oluşturulması

        takvim = Calendar.getInstance();

        //tarihi belirlemek
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yy");
        final String formattedDate = dateformat.format(takvim.getTime());

        //saati belirlemek
        SimpleDateFormat dateformatSaat = new SimpleDateFormat("HH:mm");
        final String formattedSaat = dateformatSaat.format(takvim.getTime());


        btnCiktiAl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.labeltemplate);

                final Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                final Canvas c = new Canvas(bmp);

                String textOlcumDegeri = rx + "";
                Paint p = new Paint();
                p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                p.setTextSize(180);
                p.setColor(Color.BLACK);

                //yazının fotoda nerede olacağı (aşağı yukarı)
                int yPos = (int) (c.getHeight() / 2.30);

                String textAciklama = olcumBolumAdi;
                Paint p1 = new Paint();
                p1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                p1.setTextSize(180);
                p1.setColor(Color.BLACK);

                int yPosAciklama = (int) (c.getHeight() / 1.45);

                String textAciklama2 = olculenNokta;
                Paint p2 = new Paint();
                p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                p2.setTextSize(180);
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
                MediaScannerConnection.scanFile(OlcumNoktaDetaylar.this, new String[]{file.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {

                            public void onScanCompleted(String path, Uri uri) {

                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });

                String hop = "/storage/emulated/0/Pictures/saved_images/Image-" + n + ".png";

                Intent i = new Intent(OlcumNoktaDetaylar.this, Activity_PrintImage.class);
                i.putExtra("labelAdress", hop);
                startActivity(i);

                /*Intent in = new Intent(OlcumNoktaDetaylar.this, LabelOlustur.class);
                in.putExtra("", "");
                in.putExtra("", "");*/
            }
        });

        btnGeriDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(OlcumNoktaDetaylar.this, OlcumNoktalari.class);
                in.putExtra("olcumyeriid", olcumYeriIdDB);
                startActivity(in);
                //startActivity(new Intent(OlcumNoktaDetaylar.this, OlcumNoktalari.class));
            }
        });

        btnDuzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inten = new Intent(OlcumNoktaDetaylar.this, OlcumNoktaDuzenle.class);
                inten.putExtra("olcumnoktaid", olcumNoktaIdDB);
                inten.putExtra("olcumBolumAdi", olcumBolumAdi);
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
                startActivity(inten);
            }
        });

        btnSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog = new AlertDialog.Builder(OlcumNoktaDetaylar.this)
                        .setTitle("Ölçüm Ortam Bilgisi")
                        .setMessage("Ölçüm noktasını silmek istediğinizden emin misiniz?")
                        .setCancelable(false)
                        .setPositiveButton("Evet, Sil.",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {

                                        Toast.makeText(getApplicationContext(), "Ölçüm Noktası Silinecek", Toast.LENGTH_LONG).show();
                                    }
                                })
                        .setNegativeButton("Silme, Devam Et.",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {


                                    }
                                }).create();
                alertDialog.show();
            }
        });
    }

    class olcumnoktadetaylarinigetir extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OlcumNoktaDetaylar.this);
            pDialog.setMessage("Seçilen Nokta Detayları Yükleniyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args){

            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("olcumbolumadi", olcumBolumAdiTY));
            params.add(new BasicNameValuePair("olculennokta", olculenNoktaTY));

            json = jsonParser.makeHttpRequest(url_olcumnoktadetaylar_getir,"POST", params);

            // check log cat for response
            Log.d("Create Response", json.toString());

            return null;
        }

        protected void onPostExecute(String file_url){

            pDialog.dismiss();

            try {

                olcumNoktaIdDB = json.getString("id");
                olcumYeriIdDB = json.getString("olcumyeriid");

                sebekeTipi = json.getString("sebeketip");
                olcumBolumAdi = json.getString("olcumbolumad");
                olculenTip = json.getString("olculentip");
                olculenNokta = json.getString("olculennokta");
                karakteristik = json.getString("karakteristik");
                in = json.getString("in");
                anaIletkenKesiti = json.getString("anailetkenkesit");
                korumaIletkenKesiti = json.getString("korumailetkenkesit");
                kacakAkimRolesi = json.getString("kacakakimrole");
                rx = json.getString("rx");
                iaa = json.getString("iaa");
                raa = json.getString("raa");
                olcumeGoreSonuc = json.getString("olcumegoresonuc");
                kabloyaGoreSonuc = json.getString("kabloyagoresonuc");
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Sebeke Tipi:", sebekeTipi));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Olçüm Bölüm Adı:", olcumBolumAdi));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Ölçülen Tip:", olculenTip));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Ölçülen Nokta:", olculenNokta));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Karakteristik:", karakteristik));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("In:", in));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Ana İletken Kesiti:", anaIletkenKesiti));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Koruma İletken Kesiti:", korumaIletkenKesiti));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Kaçak Akım Rolesi:", kacakAkimRolesi));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Rx:", rx));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("İaa:", iaa));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Raa:", raa));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Ölçüme Göre Sonuç:", olcumeGoreSonuc));
            olcumNoktaDetaylarDataModels.add(new OlcumNoktaDetaylarDataModel("Kabloya Göre Sonuç:", kabloyaGoreSonuc));

            adapter= new OlcumNoktaDetaylarCustomAdapter(olcumNoktaDetaylarDataModels, getApplicationContext());

            listView.setAdapter(adapter);
        }
    }
}