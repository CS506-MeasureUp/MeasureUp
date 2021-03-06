package com.example.aksha.measureup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aksha.db.models.VideoObject;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<VideoObject> videoObjects;
    private LayoutInflater inflater;

    public GridAdapter(Context context, List<VideoObject> videoObjects) {
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        this.setVideoObjects(videoObjects);
    }

    public void setVideoObjects(List<VideoObject> videoObjects) {
        this.videoObjects = videoObjects;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (videoObjects != null) {
            return videoObjects.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return videoObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        VideoObject videoObject = videoObjects.get(position);

        View objectView;
        if (convertView == null) {
            objectView = inflater.inflate(R.layout.gallery_object_layout, null);

            TextView text = objectView.findViewById(R.id.textView);
            text.setText(videoObject.getName());

            ImageView img = objectView.findViewById(R.id.imageView);

            Log.i("path: ", videoObject.getThumbnailPath());

            Bitmap thumbnail = BitmapFactory.decodeFile(videoObject.getThumbnailPath());
            img.setImageBitmap(thumbnail);
        } else {
            objectView = (View) convertView;
        }

        return objectView;
    }

}

