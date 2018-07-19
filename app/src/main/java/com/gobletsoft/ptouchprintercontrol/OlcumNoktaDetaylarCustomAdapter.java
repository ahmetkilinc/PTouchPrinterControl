package com.gobletsoft.ptouchprintercontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class OlcumNoktaDetaylarCustomAdapter extends ArrayAdapter<OlcumNoktaDetaylarDataModel> implements View.OnClickListener{

    private ArrayList<OlcumNoktaDetaylarDataModel> dataSet;
    Context mContext;

    private static class ViewHolder{

        TextView txtDegiskenadi;
        TextView txtDegiskendegeri;

        //TextView txtVersion;
        ImageView info;
    }

    public OlcumNoktaDetaylarCustomAdapter(ArrayList<OlcumNoktaDetaylarDataModel> data, Context context){

        super(context, R.layout.list_olcum_nokta_detaylar, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object= getItem(position);
        OlcumNoktaDetaylarDataModel olcumNoktaDetaylarDataModel =(OlcumNoktaDetaylarDataModel)object;

        switch (v.getId())
        {

            case R.id.item_info:

                //Snackbar.make

                break;


        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        OlcumNoktaDetaylarDataModel olcumNoktaDetaylarDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        OlcumNoktaDetaylarCustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new OlcumNoktaDetaylarCustomAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_olcum_nokta_detaylar, parent, false);
            viewHolder.txtDegiskenadi = (TextView) convertView.findViewById(R.id.degiskenadi);
            viewHolder.txtDegiskendegeri = (TextView) convertView.findViewById(R.id.degiskendegeri);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OlcumNoktaDetaylarCustomAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;*/


        viewHolder.txtDegiskenadi.setText(olcumNoktaDetaylarDataModel.getDegiskenadi());
        viewHolder.txtDegiskendegeri.setText(olcumNoktaDetaylarDataModel.getDegiskendegeri());

        return convertView;
    }
}
