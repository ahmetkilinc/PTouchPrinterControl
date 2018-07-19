package com.gobletsoft.ptouchprintercontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OlcumNoktalariCustomAdapter extends ArrayAdapter<OlcumNoktalariDataModel> implements View.OnClickListener{

    private ArrayList<OlcumNoktalariDataModel> dataSet;
    Context mContext;

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object = getItem(position);
        OlcumNoktalariDataModel olcumNoktalariDataModel = (OlcumNoktalariDataModel) object;

        switch (v.getId())
        {

            case R.id.item_info:

                //Snackbar.make

                break;
        }
    }

    private static class ViewHolder{

        TextView txtOlcumBolumAdi;
        TextView txtOlculenNokta;
        TextView txtDegiskenNumarasi;
    }

    public OlcumNoktalariCustomAdapter(ArrayList<OlcumNoktalariDataModel> data, Context context){

        super(context, R.layout.list_olcum_noktalari, data);
        this.dataSet = data;
        this.mContext = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        OlcumNoktalariDataModel olcumNoktalariDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        OlcumNoktalariCustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new OlcumNoktalariCustomAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_olcum_noktalari, parent, false);
            viewHolder.txtOlcumBolumAdi = (TextView) convertView.findViewById(R.id.olcumbolumadi);
            viewHolder.txtOlculenNokta = (TextView) convertView.findViewById(R.id.olculennokta);
            viewHolder.txtDegiskenNumarasi = (TextView) convertView.findViewById(R.id.degiskennumarasi);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (OlcumNoktalariCustomAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;*/


        viewHolder.txtOlcumBolumAdi.setText(olcumNoktalariDataModel.getOlcumbolumadi());
        viewHolder.txtOlculenNokta.setText(olcumNoktalariDataModel.getOlculennokta());
        viewHolder.txtDegiskenNumarasi.setText(olcumNoktalariDataModel.getDegiskennumarasi());
        //viewHolder.info.setOnClickListener(this);
        //viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}