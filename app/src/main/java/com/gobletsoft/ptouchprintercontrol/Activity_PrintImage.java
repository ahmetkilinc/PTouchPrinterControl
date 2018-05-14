package com.gobletsoft.ptouchprintercontrol;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.ptouch.sdk.PrinterInfo;
import com.bumptech.glide.Glide;
import com.gobletsoft.ptouchprintercontrol.common.Common;
import com.gobletsoft.ptouchprintercontrol.common.MsgDialog;
import com.gobletsoft.ptouchprintercontrol.common.MsgHandle;
import com.gobletsoft.ptouchprintercontrol.printprocess.ImagePrint;
import com.gobletsoft.ptouchprintercontrol.printprocess.MultiImagePrint;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Activity_PrintImage extends BaseActivity {

    private final ArrayList<String> mFiles = new ArrayList<String>();
    private ImageView mImageView;
    private Button mBtnPrint;
    private Button mMultiPrint;

    private String SelectedLabel;

    private String tempString;

    private AccountHeader headerResult = null;
    Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_print_image);
















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
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
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

        PrimaryDrawerItem itemBasaDon = new PrimaryDrawerItem().withIdentifier(1).withName("1").withSelectable(false).withIcon(
                R.drawable.basadon);

        PrimaryDrawerItem itemTumSonuclariGor = new PrimaryDrawerItem().withIdentifier(2).withName("2").withSelectable(false).withIcon(
                R.drawable.sonuclar);

        PrimaryDrawerItem itemAyarlar = new PrimaryDrawerItem().withIdentifier(3).withName("3").withSelectable(false).withIcon(
                R.drawable.ayarlar);

        PrimaryDrawerItem itemCikisYap = new PrimaryDrawerItem().withIdentifier(4).withName("4").withSelectable(false).withIcon(
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

                            }

                            else if(drawerItem.getIdentifier() == 2){

                            }

                            else if(drawerItem.getIdentifier() == 3){

                            }

                            else if (drawerItem.getIdentifier() == 4){

                            }
                        }
                        //istenilen event gerçekleştikten sonra drawer'ı kapat ->
                        return false;
                    }
                })
                .build();






























        final String hop2 = getIntent().getExtras().getString("labelAdress");

        Toast.makeText(getApplicationContext(), hop2, Toast.LENGTH_LONG).show();

        Button btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSelectFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                selectFileButtonOnClick();
            }
        });

        Button btnPrinterSettings = findViewById(R.id.btnPrinterSettings);
        btnPrinterSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                printerSettingsButtonOnClick();
            }
        });

        Button btnPrinterStatus = findViewById(R.id.btnPrinterStatus);
        btnPrinterStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                printerStatusButtonOnClick();
            }
        });
        Button btnSendFile = findViewById(R.id.btnSendFile);
        btnSendFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                sendFileButtonOnClick();
            }
        });

        mMultiPrint = (Button) findViewById(R.id.btnMultiPrint);
        mMultiPrint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                printMultiFileButtonOnClick();
            }
        });

        mMultiPrint.setEnabled(false);

        // initialization for Activity
        mBtnPrint = (Button) findViewById(R.id.btnPrint);
        mBtnPrint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                    printButtonOnClick();
            }
        });

        mBtnPrint.setEnabled(false);

        CheckBox chkMutilSelect = this
                .findViewById(R.id.chkMultipleSelect);
        chkMutilSelect
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean arg1) {

                        showMultiSelect(arg1);
                    }
                });

        mImageView = (ImageView) this.findViewById(R.id.imageView);

        //mImageView.setBackground(drawable);

        // get data from other application by way of intent sending
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String file = extras.getString(Common.INTENT_FILE_NAME);
            setDisplayFile(file);
            mBtnPrint.setEnabled(true);
        }

        // initialization for printing
        mDialog = new MsgDialog(this);
        mHandle = new MsgHandle(this, mDialog);
        myPrint = new ImagePrint(this, mHandle, mDialog);

        //when use bluetooth print set the adapter
        //BluetoothAdapter bluetoothAdapter = super.getBluetoothAdapter();
        //myPrint.setBluetoothAdapter(bluetoothAdapter);
    }

    /**
     * Called when [select file] button is tapped
     */
   @Override
    public void selectFileButtonOnClick() {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        // call File Explorer Activity to select a image or prn file
        final String imagePrnPath = prefs.getString(
                Common.PREFES_IMAGE_PRN_PATH, "");

        final Intent fileList = new Intent(Activity_PrintImage.this,
                Activity_FileList.class);

        fileList.putExtra(Common.INTENT_TYPE_FLAG, Common.FILE_SELECT_PRN_IMAGE);
        fileList.putExtra(Common.INTENT_FILE_NAME, imagePrnPath);
        startActivityForResult(fileList, Common.FILE_SELECT_PRN_IMAGE);
    }

    /**
     * Called when [Print] button is tapped
     */
    @Override
    public void printButtonOnClick() {

        //String tempString = ;
       // mFiles.add(tempString);

        // set the printing data
        ((ImagePrint) myPrint).setFiles(mFiles);

        if (!checkUSB())
            return;

        // call function to print
        myPrint.print();
    }

    /**
     * Called when [Print] button is tapped
     */
    public void printMultiFileButtonOnClick() {
        myPrint = new MultiImagePrint(this, mHandle, mDialog);

        // set the printing data
        ((MultiImagePrint) myPrint).setFiles(mFiles);

        if (!checkUSB())
            return;

        // call function to print
        myPrint.print();

        myPrint = new ImagePrint(this, mHandle, mDialog);
    }

    /**
     * Called when [Printer Status] button is tapped
     */
    private void printerStatusButtonOnClick() {

        if (!checkUSB())
            return;
        myPrint.getPrinterStatus();
    }

    /**
     * Called when [Printer Status] button is tapped
     */
    private void sendFileButtonOnClick() {

        // set the printing data
        ((ImagePrint) myPrint).setFiles(mFiles);

        if (!checkUSB())
            return;

        sendFile();
    }


    /**
     * Launch the thread to print
     */
    private void sendFile() {


        SendFileThread getTread = new SendFileThread();
        getTread.start();
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional data
     * from it.
     */
    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // set the image/prn file
        if (resultCode == RESULT_OK
                && requestCode == Common.FILE_SELECT_PRN_IMAGE) {
            final String strRtn = data.getStringExtra(Common.INTENT_FILE_NAME);
            setImageOrPrnFile(strRtn);
        }
    }

    /**
     * set the image/prn file
     */
    private void setImageOrPrnFile(String file) {

        CheckBox chkMultiSelect = this
                .findViewById(R.id.chkMultipleSelect);
        TextView tvSelectedFiles = findViewById(R.id.tvSelectedFiles);

        if (chkMultiSelect.isChecked()) {

            if (!mFiles.contains(file)) {

                mFiles.add(file);

                int count = mFiles.size();
                String str = "";
                for (int i = 0; i < count; i++) {

                    str = str + mFiles.get(i) + "\n";
                }
                tvSelectedFiles.setText(str);
            }
        } else {
            setDisplayFile(file);
        }
        mMultiPrint.setEnabled(true);
        mBtnPrint.setEnabled(true);
    }

    /**
     * set the selected file to display
     */
    @SuppressWarnings("deprecation")
    private void setDisplayFile(String file) {

        mFiles.clear();
        mFiles.add(file);

        ((TextView) findViewById(R.id.tvSelectedFiles)).setText(file);

        Toast.makeText(getApplicationContext(), file,Toast.LENGTH_LONG).show();

        if (Common.isImageFile(file)) {

            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int displayWidth = display.getWidth();
            int displayHeight = display.getHeight();
            TextView tvSelectedFiles = findViewById(R.id.tvSelectedFiles);

            int[] location = new int[2];
            tvSelectedFiles.getLocationOnScreen(location);

            int height = displayHeight - location[1]
                    - tvSelectedFiles.getHeight();
            Bitmap mBitmap = Common.fileToBitmap(file, displayWidth, height);

            mImageView.setImageBitmap(mBitmap);
        } else {

            mImageView.setImageBitmap(null);
        }
    }

    /**
     * set the status of controls when the [Multi Select] CheckBox is checked or
     * not
     */
    private void showMultiSelect(boolean isVisible) {
        mFiles.clear();
        mBtnPrint.setEnabled(false);
        mMultiPrint.setEnabled(false);

        TextView tvSelectedFiles = (TextView) findViewById(R.id.tvSelectedFiles);
        tvSelectedFiles.setText("");

        if (isVisible) {
            mImageView.setImageBitmap(null);
        }
    }

    /**
     * Thread for getting the printer's status
     */
    private class SendFileThread extends Thread {
        @Override
        public void run() {

            // set info. for printing
            myPrint.setPrinterInfo();

            // start message
            Message msg = mHandle.obtainMessage(Common.MSG_PRINT_START);
            mHandle.sendMessage(msg);

            myPrint.getPrinter().startCommunication();

            int count = ((ImagePrint) myPrint).getFiles().size();

            for (int i = 0; i < count; i++) {

                String strFile = ((ImagePrint) myPrint).getFiles().get(i);

                myPrint.setPrintResult(myPrint.getPrinter().sendBinaryFile(strFile));

                // if error, stop print next files
                if (myPrint.getPrintResult().errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                    break;
                }
            }


            myPrint.getPrinter().endCommunication();

            // end message
            mHandle.setResult(myPrint.showResult());
            mHandle.setBattery(myPrint.getBattery());
            msg = mHandle.obtainMessage(Common.MSG_PRINT_END);
            mHandle.sendMessage(msg);

        }
    }
}
