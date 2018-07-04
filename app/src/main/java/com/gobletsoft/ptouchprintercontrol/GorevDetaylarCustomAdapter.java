package com.gobletsoft.ptouchprintercontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GorevDetaylarCustomAdapter extends ArrayAdapter<GorevDetaylarDataModel> implements View.OnClickListener {

    private ArrayList<GorevDetaylarDataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        //TextView txtName;
        //TextView txtType;

        TextView txtDegiskenadi;
        TextView txtDegiskendegeri;

        //TextView txtVersion;
        ImageView info;
    }

    public GorevDetaylarCustomAdapter(ArrayList<GorevDetaylarDataModel> data, Context context) {

        super(context, R.layout.list_gorev_detaylar, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object= getItem(position);
        GorevDetaylarDataModel gorevDetaylarDataModel =(GorevDetaylarDataModel)object;

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
        GorevDetaylarDataModel gorevDetaylarDataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        GorevDetaylarCustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new GorevDetaylarCustomAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_gorev_detaylar, parent, false);
            viewHolder.txtDegiskenadi = (TextView) convertView.findViewById(R.id.degiskenadi);
            viewHolder.txtDegiskendegeri = (TextView) convertView.findViewById(R.id.degiskendegeri);
            //viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_number);
            //viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GorevDetaylarCustomAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;*/


        viewHolder.txtDegiskenadi.setText(gorevDetaylarDataModel.getDegiskenadi());
        viewHolder.txtDegiskendegeri.setText(gorevDetaylarDataModel.getDegiskendegeri());
        //viewHolder.txtVersion.setText(devamEdenGorevlerDataModel.getVersion_number());
        //viewHolder.info.setOnClickListener(this);
        //viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
