package ru.org.adons.mplace;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;

import java.util.Date;

public class Place {

    private Context context;
    private int ID; // rowID in DB
    private String name;
    private String date;
    private String description;
    private Bitmap thumbnail;
    private String imagePath;

    public Place(Context context) {
        this.context = context;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = DateFormat.getLongDateFormat(context).format(date);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
