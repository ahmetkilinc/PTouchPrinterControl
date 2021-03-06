package com.gobletsoft.ptouchprintercontrol;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.bumptech.glide.Glide;
import com.gobletsoft.ptouchprintercontrol.common.Common;
import com.gobletsoft.ptouchprintercontrol.common.MsgDialog;
import com.gobletsoft.ptouchprintercontrol.printprocess.PrinterModelInfo;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_StartMenu extends Activity {

    private final int PERMISSION_WRITE_EXTERNAL_STORAGE = 10001;

    private AccountHeader headerResult = null;
    Drawer result;

    // Session Manager Class
    SessionManager session;

    private String kullaniciAdiSession;
    private String adiSession;
    private String soyadiSession;
    private String emailSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_startmenu);



        // Session class instance
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        if (!session.isLoggedIn()){

            startActivity(new Intent(Activity_StartMenu.this, KullaniciGirisi.class));
        }

        //session.getUserDetails();


        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        kullaniciAdiSession = user.get(SessionManager.KEY_KULLANICIADI);
        adiSession = user.get(SessionManager.KEY_ADI);
        soyadiSession = user.get(SessionManager.KEY_SOYADI);
        emailSession = user.get(SessionManager.KEY_NAME);

        if (adiSession == null || adiSession.isEmpty()){

            startActivity(new Intent(Activity_StartMenu.this, KullaniciGirisi.class));
        }

        init();
        setListView();
        getDataFromIntent();
        // initialize the SharedPreferences
        setPreferences();


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

        //final IProfile profile = new ProfileDrawerItem().withName(adiSession + " " + soyadiSession).withEmail(emailSession).withIcon(displayPhotoUrl).withIdentifier(100);
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

        /*PrimaryDrawerItem itemYeniEtiket = new PrimaryDrawerItem().withIdentifier(1).withName(getString(R.string.dn_new_label)).withSelectable(false).withIcon(
                R.drawable.newlabel);

        PrimaryDrawerItem itemGorevler = new PrimaryDrawerItem().withIdentifier(2).withName(getString(R.string.dn_gorevler)).withSelectable(false).withIcon(
                R.drawable.gorevler);*/

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

                                startActivity(new Intent(Activity_StartMenu.this, Gorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 2){

                                startActivity(new Intent(Activity_StartMenu.this, DevamEdenGorevler.class));
                            }

                            else if (drawerItem.getIdentifier() == 3){

                                startActivity(new Intent(Activity_StartMenu.this, Activity_Settings.class));
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
    }

    private void init() {
        /*
         * copy .bin file (RJ paper size info.) to
         * /mnt/sdcard/customPaperFileSet/ .bin file is made by Printer Setting
         * Tool which can be downloaded from the Brother Net Site
         */
        try {

            raw2file("RJ4030_102mm.bin", R.raw.rj4030_102mm);
            raw2file("RJ4030_102mm152mm.bin", R.raw.rj4030_102mm152mm);
            raw2file("RJ4040_102mm.bin", R.raw.rj4040_102mm);
            raw2file("RJ4040_102mm152mm.bin", R.raw.rj4040_102mm152mm);
            raw2file("RJ4030Ai_102mm.bin", R.raw.rj4030ai_102mm);
            raw2file("RJ4030Ai_102mm152mm.bin", R.raw.rj4030ai_102mm152mm);

            raw2file("RJ3050_76mm.bin", R.raw.rj3050_76mm);
            raw2file("RJ3150_76mm.bin", R.raw.rj3150_76mm);
            raw2file("RJ3150_76mm44mm.bin", R.raw.rj3150_76mm44mm);
            raw2file("RJ3050Ai_76mm.bin", R.raw.rj3050ai_76mm);
            raw2file("RJ3150Ai_76mm.bin", R.raw.rj3150ai_76mm);
            raw2file("RJ3150Ai_76mm44mm.bin", R.raw.rj3150ai_76mm44mm);
            raw2file("RJ2030_50mm.bin", R.raw.rj2030_50mm);
            raw2file("RJ2050_50mm.bin", R.raw.rj2050_50mm);
            raw2file("RJ2030_58mm.bin", R.raw.rj2030_58mm);
            raw2file("RJ2050_58mm.bin", R.raw.rj2050_58mm);
            raw2file("RJ2140_58mm.bin", R.raw.rj2140_58mm);
            raw2file("RJ2140_50x85mm.bin", R.raw.rj2140_50x85mm);
            raw2file("RJ2150_58mm.bin", R.raw.rj2150_58mm);
            raw2file("RJ2150_50x85mm.bin", R.raw.rj2150_50x85mm);


            raw2file("TD2020_57mm.bin", R.raw.td2020_57mm);
            raw2file("TD2020_40mm40mm.bin", R.raw.td2020_40mm40mm);
            raw2file("TD2120_57mm.bin", R.raw.td2120_57mm);
            raw2file("TD2120_40mm40mm.bin", R.raw.td2120_40mm40mm);
            raw2file("TD2130_57mm.bin", R.raw.td2130_57mm);
            raw2file("TD2130_40mm40mm.bin", R.raw.td2130_40mm40mm);
            raw2file("TD4100N_102mm.bin", R.raw.td4100n_102mm);
            raw2file("TD4100N_102mm152mm.bin", R.raw.td4100n_102mmx152mm);
            raw2file("TD4000_102mm.bin", R.raw.td4000_102mm);
            raw2file("TD4000_102mm152mm.bin", R.raw.td4000_102mmx152mm);

        } catch (Exception ignored) {
        }

    }

    private boolean isPermitWriteStorage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onStart() {

        super.onStart();
        if (!isPermitWriteStorage()) {

            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_WRITE_EXTERNAL_STORAGE: {

                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, getString(R.string.unable_access),
                            Toast.LENGTH_SHORT).show();
                } else {

                    init();
                }
            }
        }
    }

    private void setPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        // initialization for print
        Printer printer = new Printer();
        PrinterInfo printerInfo = printer.getPrinterInfo();

        if (printerInfo == null) {

            printerInfo = new PrinterInfo();
            printer.setPrinterInfo(printerInfo);
        }

        if (sharedPreferences.getString("printerModel", "").equals("")) {

            String printerModel = printerInfo.printerModel.toString();
            PrinterModelInfo.Model model = PrinterModelInfo.Model.valueOf("PT_E550W");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            //editor.putString("printerModel", printerModel);
            editor.putString("printerModel", "PT_E550W");
            editor.putString("port", Common.NET);
            editor.putString("address", printerInfo.ipAddress);
            editor.putString("macAddress", printerInfo.macAddress);

            // Override SDK default paper size
            editor.putString("paperSize", model.getDefaultPaperSize());

            //editor.putString("orientation", printerInfo.orientation.toString());
            editor.putString("orientation", "LANDSCAPE");

            editor.putString("numberOfCopies", Integer.toString(printerInfo.numberOfCopies));

            editor.putString("halftone", printerInfo.halftone.toString());

            editor.putString("printMode", printerInfo.printMode.toString());

            editor.putString("pjCarbon", Boolean.toString(printerInfo.pjCarbon));

            editor.putString("pjDensity", Integer.toString(printerInfo.pjDensity));

            editor.putString("pjFeedMode", printerInfo.pjFeedMode.toString());

            editor.putString("align", printerInfo.align.toString());

            editor.putString("leftMargin", Integer.toString(printerInfo.margin.left));

            editor.putString("valign", printerInfo.valign.toString());

            editor.putString("topMargin", Integer.toString(printerInfo.margin.top));

            editor.putString("customPaperWidth", Integer.toString(printerInfo.customPaperWidth));

            editor.putString("customPaperLength", Integer.toString(printerInfo.customPaperLength));

            editor.putString("customFeed", Integer.toString(printerInfo.customFeed));

            editor.putString("paperPosition", printerInfo.paperPosition.toString());

            editor.putString("customSetting", sharedPreferences.getString("customSetting", ""));

            editor.putString("rjDensity", Integer.toString(printerInfo.rjDensity));

            editor.putString("rotate180", Boolean.toString(printerInfo.rotate180));

            editor.putString("dashLine", Boolean.toString(printerInfo.dashLine));

            editor.putString("peelMode", Boolean.toString(printerInfo.peelMode));

            editor.putString("mode9", Boolean.toString(printerInfo.mode9));

            editor.putString("pjSpeed", Integer.toString(printerInfo.pjSpeed));

            editor.putString("pjPaperKind", printerInfo.pjPaperKind.toString());

            editor.putString("printerCase", printerInfo.rollPrinterCase.toString());

            editor.putString("printQuality", printerInfo.printQuality.toString());

            editor.putString("skipStatusCheck", Boolean.toString(printerInfo.skipStatusCheck));

            editor.putString("checkPrintEnd", printerInfo.checkPrintEnd.toString());

            editor.putString("imageThresholding", Integer.toString(printerInfo.thresholdingValue));

            editor.putString("scaleValue", Double.toString(printerInfo.scaleValue));

            editor.putString("trimTapeAfterData", Boolean.toString(printerInfo.trimTapeAfterData));

            editor.putString("enabledTethering", Boolean.toString(printerInfo.enabledTethering));

            editor.putString("processTimeout",
                    Integer.toString(printerInfo.timeout.processTimeoutSec));
            editor.putString("sendTimeout", Integer.toString(printerInfo.timeout.sendTimeoutSec));

            editor.putString("receiveTimeout", Integer.toString(printerInfo.timeout.receiveTimeoutSec));

            editor.putString("connectionTimeout", Integer.toString(printerInfo.timeout.connectionWaitMSec));

            editor.putString("closeWaitTime", Integer.toString(printerInfo.timeout.closeWaitDisusingStatusCheckSec));

            editor.putString("overwrite", Boolean.toString(printerInfo.overwrite));

            editor.putString("savePrnPath", printerInfo.savePrnPath);

            editor.putString("softFocusing", Boolean.toString(printerInfo.softFocusing));

            editor.putString("rawMode", Boolean.toString(printerInfo.rawMode));

            editor.putString("workPath", printerInfo.workPath);

            editor.apply();
        }
    }

    /**
     * set the launcher's items
     */
    private void setListView() {

        final Map<Object, Object> activityClass = new HashMap<Object, Object>();
        activityClass.put(0, Gorevler.class);
        activityClass.put(1, Activity_PrintPdf.class);
        activityClass.put(2, Activity_PrintTemplate.class);
        activityClass.put(3, Activity_ManageTemplate.class);
        activityClass.put(4, Activity_PrinterPreference.class);

        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        ArrayList<String> mListItems = new ArrayList<String>();
       // mListItems.add(getString(R.string.text_print_image));
       // mListItems.add(getString(R.string.button_printer_settings));
       // mListItems.add(getString(R.string.startmenu_profile_settings));
        mListItems.add(getString(R.string.gorevler_textview));
        mListItems.add(getString(R.string.devam_eden_gorevler));


        int count = mListItems.size();
        Map<String, Object> listItem;

        for (int i = 0; i < count; i++) {

            listItem = new HashMap<String, Object>();
            listItem.put(Common.INTENT_FILE_NAME, mListItems.get(i));
            listItems.add(listItem);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, listItems,
                R.layout.xml_list_item,
                new String[]{Common.INTENT_FILE_NAME},
                new int[]{R.id.text});

        ListView lvFiles = this.findViewById(R.id.list);
        lvFiles.setAdapter(adapter);
        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (!isPermitWriteStorage()) {

                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE);
                    return;
                }

                if (arg2 == 1){

                    //startActivity(new Intent(Activity_StartMenu.this, Activity_Settings.class));
                    startActivity(new Intent(Activity_StartMenu.this, DevamEdenGorevler.class));
                }

                else if (arg2 == 2){

                    //startActivity(new Intent(Activity_StartMenu.this, KullaniciAyarlari.class));
                    //Toast.makeText(getApplicationContext(), "Kullanıcı Ayarları Burada Olacak.", Toast.LENGTH_LONG).show();
                    //devam eden görevler...
                }

                else if(arg2 == 3){

                    //startActivity(new Intent(Activity_StartMenu.this, Gorevler.class));
                }

                else{

                    Intent intent = new Intent(arg0.getContext(),
                            (Class<?>) activityClass.get(arg2));
                    arg0.getContext().startActivity(intent);
                }
            }
        });
    }

    private String parseFileName(String fileName) {

        if (fileName.contains("content://")) {

            if (getIntent().getExtras() == null) {

                return "";
            }
            final Uri imageUri = Uri.parse(
                    getIntent().getExtras().get("android.intent.extra.STREAM").toString());
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);

            if (cursor == null) {

                return "";
            }
            int columnIndex = 0;

            try {

                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            } catch (IllegalArgumentException e) {

                return "";
            }

            cursor.moveToFirst();
            fileName = cursor.getString(columnIndex);
            cursor.close();
        } else if (fileName.contains("file://")) {

            fileName = Uri.decode(fileName);
            fileName = fileName.substring(7);
        }
        return fileName;
    }

    private String saveDataFromIntent(Intent intent) {

        String fileName = "";

        try {

            Uri uri;
            if (Intent.ACTION_SEND.equals(intent.getAction())) {

                uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
            else {

                uri = getIntent().getData();
            }

            if (uri == null) {
                return "";
            }

            Cursor cursor = getContentResolver().query(uri, new String[]{
                    MediaStore.MediaColumns.DISPLAY_NAME
            }, null, null, null);

            if (cursor == null) {

                return "";
            }
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);

            String folder = Environment.getExternalStorageDirectory()
                    .toString() + "/com.brother.ptouch.sdk/";
            File newdir = new File(folder);

            if (!newdir.exists()) {

                newdir.mkdir();
            }

            if (nameIndex >= 0) {

                fileName = folder + cursor.getString(nameIndex);
            }
            cursor.close();
            File dstFile = new File(fileName);
            OutputStream output = new FileOutputStream(dstFile);
            InputStream input = new BufferedInputStream(getContentResolver().openInputStream(uri));

            int DEFAULT_BUFFER_SIZE = 1024 * 4;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = input.read(buffer))) {

                output.write(buffer, 0, n);
            }
            input.close();
            output.close();
        } catch (IOException e) {

            fileName = "";
        }


        return fileName;
    }

    /**
     * launch by intent sending
     */
    private void getDataFromIntent() {

        Intent intent = getIntent();
        if (intent == null) {

            return;
        }
        // Get file path from intent
        if (Intent.ACTION_SEND.equals(intent.getAction())
                || Intent.ACTION_VIEW.equals(intent.getAction())) {

            String fileName = "";
            // get the data of Intent.ACTION_SEND from other application
            if (Intent.ACTION_SEND.equals(intent.getAction())) {

                if (intent.getExtras() == null) {

                    return;
                }
                fileName = intent.getExtras()
                        .get("android.intent.extra.STREAM").toString();
                fileName = parseFileName(fileName);

            } else {

                Uri uri = intent.getData();
                if (uri == null) {

                    return;
                }
                fileName = uri.toString();
                if (fileName == null) {

                    return;
                }
                fileName = parseFileName(fileName);
            }
            if (fileName == null || "".equals(fileName)) {

                fileName = saveDataFromIntent(intent);
            }

            if (fileName == null || fileName.equals("")) {

                return;
            }
            // launch the PrintImage Activity when it is a image file or prn
            // file
            if (Common.isImageFile(fileName) || Common.isPrnFile(fileName)) {

                Intent printerList = new Intent(this, Activity_PrintImage.class);
                printerList.putExtra(Common.INTENT_FILE_NAME, fileName);
                startActivity(printerList);
            }
            // launch the PrintPdf Activity when it is a pdf file
            else if (Common.isPdfFile(fileName)) {

                Intent printerList = new Intent(this, Activity_PrintPdf.class);
                printerList.putExtra(Common.INTENT_FILE_NAME, fileName);
                startActivity(printerList);
            }
            // launch the TransferPdz Activity when it is a pdz file
            else if (Common.isTemplateFile(fileName)) {

                Intent printerList = new Intent(this,
                        Activity_TransferPdz.class);
                printerList.putExtra(Common.INTENT_FILE_NAME, fileName);
                startActivity(printerList);
            }
        }
    }

    /**
     * Called when the menu key is tapped
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when the menu's item is tapped
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.option_menu_setting: // printer settings
                startActivity(new Intent(this, Activity_Settings.class));
                break;
            case R.id.option_menu_about: // about dialog
                MsgDialog msgDialog = new MsgDialog(this);
                msgDialog.showAlertDialog(getString(R.string.about_title),
                        getString(R.string.about_text));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the application is closed
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * show message when BACK key is clicked
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            showTips();
        }
        return false;
    }

    /**
     * show the closing message
     */
    private void showTips() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.end_title)
                .setMessage(R.string.close_message)
                .setCancelable(false)
                .setPositiveButton(R.string.button_ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {

                                finish();
                            }
                        })
                .setNegativeButton(R.string.button_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {

                            }
                        }).create();
        alertDialog.show();
    }

    /**
     * copy from raw in resource
     */
    private void raw2file(String fileName, int fileID) {

        File newdir = new File(Common.CUSTOM_PAPER_FOLDER);
        if (!newdir.exists()) {
            newdir.mkdir();
        }
        File dstFile = new File(Common.CUSTOM_PAPER_FOLDER + fileName);
        if (!dstFile.exists()) {

            try {

                InputStream input;
                OutputStream output;
                input = this.getResources().openRawResource(fileID);
                output = new FileOutputStream(dstFile);
                int DEFAULT_BUFFER_SIZE = 1024 * 4;
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n;
                while (-1 != (n = input.read(buffer))) {

                    output.write(buffer, 0, n);
                }
                input.close();
                output.close();
            } catch (IOException ignored) {

            }
        }
    }
}