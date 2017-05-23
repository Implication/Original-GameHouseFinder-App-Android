package trajon.felton.gamehousefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Trajon Felton on 11/5/2016.
 */

public class StoreAdapter extends ArrayAdapter<Store> {
    public StoreAdapter(Context context, ArrayList<Store> stores) {
        super(context, 0, stores);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Store store = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.store_item, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
        tvName.setText(store.storeName);
        tvDistance.setText(String.valueOf(store.distance)+" mi");

        return convertView;
    }
}

