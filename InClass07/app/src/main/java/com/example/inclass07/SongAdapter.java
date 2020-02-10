package com.example.inclass07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {

    public SongAdapter(@NonNull Context context, int resource, @NonNull List<Song> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Song songGet = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_songs_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.track_name = convertView.findViewById(R.id.track_name);
            viewHolder.album_name = convertView.findViewById(R.id.album_name);
            viewHolder.artist_name = convertView.findViewById(R.id.artist_name);
            viewHolder.song_date = convertView.findViewById(R.id.song_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.track_name.setText("Track: "+songGet.track_name);
        viewHolder.artist_name.setText("Artist: "+songGet.artist_name);
        viewHolder.album_name.setText("Album: " + songGet.album_name);
        viewHolder.song_date.setText("Date: "+songGet.updated_time);


        return convertView;
    }

    private static class ViewHolder {
        TextView track_name;
        TextView album_name;
        TextView artist_name;
        TextView song_date;
    }
}

