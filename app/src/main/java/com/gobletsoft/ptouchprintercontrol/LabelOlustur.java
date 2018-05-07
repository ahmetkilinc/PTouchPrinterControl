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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        final ImageView ivBitmap = findViewById(R.id.imageViewBm);


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

                    String textOlcumDegeri = olcumDegeri + " OHM";
                    Paint p = new Paint();
                    p.setTypeface(Typeface.DEFAULT);
                    p.setTextSize(10);
                    p.setColor(Color.BLACK);

                    int yPos = (int) (-20 + (c.getHeight() / (3 / 2)));
                    //- ((p.descent() + p.ascent()) / 2) - 10);


                    String textAciklama = aciklama1;
                    Paint p1 = new Paint();
                    p1.setTypeface(Typeface.DEFAULT);
                    p1.setTextSize(10);
                    p1.setColor(Color.BLACK);

                    int yPosAciklama = (int) (-10 + (c.getHeight() / (3 / 2)));

                    String textAciklama2 = aciklama2;
                    Paint p2 = new Paint();
                    p2.setTypeface(Typeface.DEFAULT);
                    p2.setTextSize(10);
                    p2.setColor(Color.BLACK);

                    int yPosAciklama2 = (int) (0 + (c.getHeight() / (3 / 2)));

                    String Tarih = formattedDate;
                    Paint p3 = new Paint();
                    p3.setTypeface(Typeface.DEFAULT);
                    p3.setTextSize(5);
                    p3.setColor(Color.BLACK);

                    int yPosAciklama3 = (int) (-52 + (c.getHeight() / (3 / 2)));


                    String saat = formattedSaat;
                    Paint p4 = new Paint();
                    p4.setTypeface(Typeface.DEFAULT);
                    p4.setTextSize(5);
                    p4.setColor(Color.BLACK);

                    int yPosAciklama4 = (int) (-42 + (c.getHeight() / (3 / 2)));

                    c.drawText(textOlcumDegeri, (0), yPos, p);
                    c.drawText(textAciklama, (0), yPosAciklama, p1);
                    c.drawText(textAciklama2, (0), yPosAciklama2, p2);
                    c.drawText(Tarih, (123), yPosAciklama3, p3);
                    c.drawText(saat, (123), yPosAciklama4, p4);

                    final BitmapDrawable drawable = new BitmapDrawable(getResources(), bmp);

                    ivBitmap.setBackground(drawable);







                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    // path to /data/data/yourapp/app_data/imageDir
                    File directory = cw.getDir("Images", Context.MODE_PRIVATE);
                    // Create imageDir
                    File mypath = new File(directory,"radsan-label.jpg");

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mypath);
                        // Use the compress method on the BitMap object to write image to the OutputStream
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();



                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }












                    Intent i = new Intent(LabelOlustur.this, Activity_PrintImage.class);
                    i.putExtra("bitmap", bmp);
                    startActivity(i);
                    //(new Intent(LabelOlustur.this, Activity_PrintImage.class));
                }
            }
        });
    }

}
