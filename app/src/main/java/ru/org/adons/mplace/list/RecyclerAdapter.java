package ru.org.adons.mplace.list;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;

import ru.org.adons.mplace.MConstants;
import ru.org.adons.mplace.Place;
import ru.org.adons.mplace.R;
import ru.org.adons.mplace.db.DBContentProvider;
import ru.org.adons.mplace.db.PlaceTable;
import ru.org.adons.mplace.view.ViewActivity;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements LoaderManager.LoaderCallbacks<Cursor> {

    private final Context context;
    private final int itemBackground;
    private final CursorAdapter cursorAdapter;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View holderView;
        public final ImageView image;
        public final TextView name;

        public ViewHolder(View v) {
            super(v);
            holderView = v;
            image = (ImageView) v.findViewById(R.id.item_image);
            name = (TextView) v.findViewById(R.id.item_name);
        }
    }

    public RecyclerAdapter(Context context) {
        this.context = context;
        final TypedValue backgroundValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, backgroundValue, true);
        itemBackground = backgroundValue.resourceId;
        // TODO: replace with simple cursor
        cursorAdapter = new CursorHandler(context, null, false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        view.setBackgroundResource(itemBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        cursorAdapter.getCursor().moveToPosition(position);

        byte[] bytes = cursorAdapter.getCursor().getBlob(PlaceTable.COLUMN_THUMBNAIL_INDEX);
        if (bytes != null && bytes.length > 0) {
            Glide.with(holder.image.getContext())
                    .load(cursorAdapter.getCursor().getBlob(PlaceTable.COLUMN_THUMBNAIL_INDEX))
                    .centerCrop()
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_image_white_48dp);
        }

        holder.name.setText(cursorAdapter.getCursor().getString(PlaceTable.COLUMN_NAME_INDEX));

        // VIEW PLACE
        holder.holderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorAdapter.getCursor().moveToPosition(position);
                Intent intent = new Intent(v.getContext(), ViewActivity.class);
                Place place = new Place();
                place.setID(cursorAdapter.getCursor().getInt(PlaceTable.COLUMN_ID_INDEX));
                place.setDate(new Date(cursorAdapter.getCursor().getLong(PlaceTable.COLUMN_DATE_INDEX)));
                place.setName(cursorAdapter.getCursor().getString(PlaceTable.COLUMN_NAME_INDEX));
                place.setDescription(cursorAdapter.getCursor().getString(PlaceTable.COLUMN_DESCRIPTION_INDEX));
                place.setImagePath(cursorAdapter.getCursor().getString(PlaceTable.COLUMN_IMAGE_PATH_INDEX));
                intent.putExtra(MConstants.EXTRA_PLACE, place);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursorAdapter.getCount();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, DBContentProvider.CONTENT_URI, PlaceTable.PLACES_SUMMARY_PROJECTION, null, null, PlaceTable.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
        notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    // to handle cursor only
    private final class CursorHandler extends CursorAdapter {

        public CursorHandler(Context context, Cursor c, boolean flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }

}
