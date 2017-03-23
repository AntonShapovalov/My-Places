package ru.org.adons.mplace.legacy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<Integer> {

    public CategoryAdapter(Context context, int resource) {
        super(context, resource);
        addAll(MainActivity.categories.keySet());
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
        holder.text.setText(MainActivity.categories.get((int) getItemId(position)));
        return convertView;
    }

}
