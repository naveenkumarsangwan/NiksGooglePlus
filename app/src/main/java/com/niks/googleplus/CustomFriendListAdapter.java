package com.niks.googleplus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.niks.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomFriendListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> itemname;
    private final List<String> imgid;

    public CustomFriendListAdapter(Activity context, List<String> itemname, List<String> imgid) {
        super(context, R.layout.friendlist_view, itemname);
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.friendlist_view, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);


        txtTitle.setText(itemname.get(position));
        if(imgid.get(position) != null) {
            Picasso.with(context).load(imgid.get(position))
                    .into(imageView);
        }
        return rowView;

    };
}