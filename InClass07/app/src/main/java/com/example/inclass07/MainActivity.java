package com.example.inclass07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tv_limit;
    EditText et_search;
    Button btn_search;
    SeekBar seekBar;
    RadioGroup radioGroup;
    RadioButton radio_track;
    RadioButton radio_artist;
    ProgressBar progressBar;
    ListView lv_songs;
    int limit = 5;
    String songSearched;
    AsyncTask songAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MusixMatch Track Search");

        tv_limit = findViewById(R.id.tv_limit);
        btn_search = findViewById(R.id.btn_search);
        et_search = findViewById(R.id.et_search);
        seekBar = findViewById(R.id.seekBar);
        radioGroup = findViewById(R.id.radioGroup);
        radio_track = findViewById(R.id.radio_track);
        radio_artist = findViewById(R.id.radio_artist);
        progressBar = findViewById(R.id.progressBar);
        lv_songs = findViewById(R.id.lv_songs);
        progressBar.setVisibility(View.INVISIBLE);

        radioGroup.check(R.id.radio_track);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_limit.setText("Limit: " + String.valueOf(i));
                limit = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(songAsync.getStatus() == AsyncTask.Status.RUNNING){
                    songAsync.cancel(true);
                }
                RadioButton rb = findViewById(i);

                String selectedRadio="";
                String radioValue = rb.getText().toString();
                String apiUrl = "http://api.musixmatch.com/ws/1.1/track.search?q=";
                songSearched = et_search.getText().toString();

                if(songSearched.trim().equals("")  || et_search == null){
                    Toast.makeText(getApplicationContext(), "Please enter the song name", Toast.LENGTH_SHORT).show();
                }else{
                    if(radioValue.equals("Track rating")){
                        selectedRadio = "s_track_rating";
                    }else if(radioValue.equals("Artist rating")) {
                        selectedRadio = "s_artist_rating";
                    }

                    apiUrl += songSearched + "&page_size="+limit+"&"+ selectedRadio + "=desc&apikey=90964cbe6cde17f49039335f7c6f423f";
                    songAsync = new GetDataAsync().execute(apiUrl);
                    progressBar.setVisibility(View.VISIBLE);

                }
            }
        });




        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                songAsync.cancel(true);
                radioGroup.check(R.id.radio_track);

                String apiUrl = "http://api.musixmatch.com/ws/1.1/track.search?q=";
                songSearched = et_search.getText().toString();
                if(songSearched.trim().equals("")  || et_search == null){
                    Toast.makeText(getApplicationContext(), "Please enter the song name", Toast.LENGTH_SHORT).show();
                }else{

                    apiUrl += songSearched + "&page_size="+limit+"&s_artist_rating=desc&apikey=90964cbe6cde17f49039335f7c6f423f";
                    songAsync = new GetDataAsync().execute(apiUrl);
                    progressBar.setVisibility(View.VISIBLE);

                }

            }
        });

    }

    public class GetDataAsync extends AsyncTask<String, Void, ArrayList<Song>> {

        @Override
        protected ArrayList<Song> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            ArrayList<Song> result = new ArrayList<>();
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    JSONObject root = new JSONObject(json);
//                    Log.d("songs", root.toString());
                    JSONObject message = root.getJSONObject("message");
                    JSONObject bodyObj = message.getJSONObject("body");
                    JSONArray tracks = bodyObj.getJSONArray("track_list");
                    if(tracks.length() == 0){
                        Toast.makeText(getApplicationContext(), "No Songs Found", Toast.LENGTH_SHORT).show();
                    }else {
                        for(int i=0; i< tracks.length();i++){
                            JSONObject tracksJSON = tracks.getJSONObject(i);
                            JSONObject track = tracksJSON.getJSONObject("track");
                            Song song = new Song();
                            song.track_name = track.getString("track_name");
                            song.album_name = track.getString("album_name");
                            song.artist_name= track.getString("artist_name");
                            song.updated_time = track.getString("updated_time");
                            song.track_share_url = track.getString("track_share_url");
                            result.add(song);
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("result",result.toString());
            return result;
        }

        @Override
        protected void onPostExecute(final ArrayList<Song> songs) {
            super.onPostExecute(songs);

            SongAdapter songAdapter = new SongAdapter(MainActivity.this, R.layout.activity_songs_list, songs);
            progressBar.setVisibility(View.INVISIBLE);
            lv_songs.setAdapter(songAdapter);

            lv_songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String url = songs.get(i).track_share_url;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
