package com.gobletsoft.ptouchprintercontrol;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class LabelOlustur extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_label_olustur);

        final EditText etOlcumDegeri = findViewById(R.id.editTextOlcumDegeri);
        final EditText etAciklama1 = findViewById(R.id.editTextAciklama1);
        final EditText etAciklama2 = findViewById(R.id.editTextAciklama2);

        //final ImageView ivBitmap = findViewById(R.id.imageViewBm);


        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
        final String formattedDate = df.format(c.getTime());

        SimpleDateFormat dfSaat = new SimpleDateFormat("HH:mm");
        final String formattedSaat = dfSaat.format(c.getTime());


        Button btnGonder = findViewById(R.id.buttonGonder);


        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etOlcumDegeri.getText().toString().isEmpty() || etAciklama1.getText().toString().isEmpty() || etAciklama2.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(), "Yukarıdaki Alanlar Boş Bırakılamaz, Lütfen Doldurunuz.", Toast.LENGTH_LONG).show();
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
                    int yPos = (int) (c.getHeight() / 1.65);
                    //- ((p.descent() + p.ascent()) / 2) - 10);

                    String textAciklama = aciklama1;
                    Paint p1 = new Paint();
                    p1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    p1.setTextSize(160);
                    p1.setColor(Color.BLACK);

                    int yPosAciklama = (int) (c.getHeight() / 1.28);

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

                    int yPosAciklama3 = (int) (c.getHeight() / 4);


                    String saat = formattedSaat;
                    Paint p4 = new Paint();
                    p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    p4.setTextSize(130);
                    p4.setColor(Color.BLACK);

                    int yPosAciklama4 = (int)  (c.getHeight() / 2.7 );

                    c.drawText(textOlcumDegeri, (c.getWidth() / 6), yPos, p);
                    c.drawText(textAciklama, (c.getWidth() / 6), yPosAciklama, p1);
                    c.drawText(textAciklama2, (c.getWidth() / 6), yPosAciklama2, p2);
                    c.drawText(Tarih, (c.getWidth() - (c.getWidth() / 4)), yPosAciklama3, p3);
                    c.drawText(saat, (c.getWidth() - (c.getWidth() / 4)), yPosAciklama4, p4);

                    final BitmapDrawable drawable = new BitmapDrawable(getResources(), bmp);

                    //ivBitmap.setBackground(drawable);







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
                        // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        // Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
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












                    Intent i = new Intent(LabelOlustur.this, Activity_PrintImage.class);
                    //i.putExtra("bitmap", bmp);
                    startActivity(i);
                    //(new Intent(LabelOlustur.this, Activity_PrintImage.class));
                }
            }
        });
    }

}
