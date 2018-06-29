package com.gobletsoft.ptouchprintercontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AtananGorevlerCustomAdapter extends ArrayAdapter<AtananGorevlerDataModel> implements View.OnClickListener {

    private ArrayList<AtananGorevlerDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        //TextView txtName;
        //TextView txtType;

        TextView txtFirmaadi;
        TextView txtLokasyonadi;

        //TextView txtVersion;
        ImageView info;
    }



    public AtananGorevlerCustomAdapter(ArrayList<AtananGorevlerDataModel> data, Context context) {
        super(context, R.layout.list_atanan_gorevler, data);
        this.dataSet = data;
        this.mContext = context;

    }


    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object= getItem(position);
        AtananGorevlerDataModel atananGorevlerDataModel =(AtananGorevlerDataModel) object;




        switch (v.getId())
        {

            case R.id.item_info:

                break;


        }


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AtananGorevlerDataModel atananGorevlerDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        AtananGorevlerCustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new AtananGorevlerCustomAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_atanan_gorevler, parent, false);
            viewHolder.txtFirmaadi = (TextView) convertView.findViewById(R.id.gorevlerFirmaAdi);
            viewHolder.txtLokasyonadi = (TextView) convertView.findViewById(R.id.gorevlerLokasyonAdi);
            //viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AtananGorevlerCustomAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;*/


        viewHolder.txtFirmaadi.setText(atananGorevlerDataModel.getFirmaadi());
        viewHolder.txtLokasyonadi.setText(atananGorevlerDataModel.getLokasyonadi());
        //viewHolder.txtVersion.setText(atananGorevlerDataModel.getVersion_number());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
