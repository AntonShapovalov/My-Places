package ru.org.adons.mplace.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;

import ru.org.adons.mplace.R;

public class CategoryAdapter extends ArrayAdapter<Integer> {

    public static final Map<Integer, String> categories = new TreeMap<Integer, String>() {{
        put(R.id.nav_place, "Place");
        put(R.id.nav_shop, "Shop");
        put(R.id.nav_cafe, "Cafe");
        put(R.id.nav_picnic, "Picnic");
        put(R.id.nav_favorite, "Favorite");
    }};

    public CategoryAdapter(Context context, int resource) {
        super(context, resource);
        addAll(categories.keySet());
    }

    private static class ViewHolder {
        TextView text;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getConvertView(position, convertView, parent, android.R.layout.simple_spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getConvertView(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
    }

    private View getConvertView(int position, View convertView, ViewGroup parent, int resID) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            convertView = inflater.inflate(resID, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(categories.get((int) getItemId(position)));
        return convertView;
    }

}
