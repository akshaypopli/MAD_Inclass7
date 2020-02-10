package com.example.inclass07;

public class Song {
    String track_name, album_name, artist_name, updated_time, track_share_url;

    @Override
    public String toString() {
        return "Song{" +
                "track_name='" + track_name + '\'' +
                ", album_name='" + album_name + '\'' +
                ", artist_name='" + artist_name + '\'' +
                ", updated_time='" + updated_time + '\'' +
                ", track_share_url='" + track_share_url + '\'' +
                '}';
    }
}
