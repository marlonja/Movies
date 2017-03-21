package com.parse.movies20;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static com.parse.movies20.MainActivity.hmap;

public class MovieInfo extends AppCompatActivity implements AsyncResponse {

    TextView synopsisTextView;
    TextView movieTitle;
    ImageView imageView;
    ImageView castImageView1;
    ImageView castImageView2;
    ImageView castImageView3;
    TextView releaseTextView;
    String movieId;
    String title;
    String cover;
    String release;
    String overview;
    ArrayList<String> names;
    ArrayList<String> actorIds;
    ArrayList<String> actorCovers;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        setTitle("");

        movieTitle = (TextView) findViewById(R.id.movieTitle);
        imageView = (ImageView) findViewById(R.id.imageView);
        releaseTextView = (TextView) findViewById(R.id.releaseTextView);
        synopsisTextView = (TextView) findViewById(R.id.synopsisTextView);
        castImageView1 = (ImageView) findViewById(R.id.castImageView1);
        castImageView2 = (ImageView) findViewById(R.id.castImageView2);
        castImageView3 = (ImageView) findViewById(R.id.castImageView3);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        pb.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        title = intent.getStringExtra("name");
        cover = intent.getStringExtra("cover");
        release = intent.getStringExtra("release");
        overview = intent.getStringExtra("overview");
        movieId = intent.getStringExtra("movieId");

        movieTitle.setText(title);
        releaseTextView.setText(release);
        synopsisTextView.setText(overview);

        checkForCover();
        getCast(movieId);

    }

    public void getCover() {

        DownloadImage dlTask = new DownloadImage();
        Bitmap myImage = null;

        try {

            myImage = dlTask.execute(cover).get();

        } catch (Exception e) {

            e.printStackTrace();

        }

        if (imageView != null) {

            imageView.setImageBitmap(myImage);

        }

    }

    public void checkForCover() {

        String cutCover = cover.substring(30, cover.lastIndexOf("g") + 1);

        Bitmap temp = hmap.get(cutCover);

        if (temp == null) {

            getCover();

        } else {

            imageView.setImageBitmap(hmap.get(cutCover));

        }

    }

    public void getCast(String movieId) {

        pb.setVisibility(View.VISIBLE);
        DownloadCast task = new DownloadCast();
        task.delegate = this;
        task.execute("http://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=64d2a074e794f43cfe1b4782eac289ff");

    }

    @Override
    public void processFinish(String output) {
        pb.setVisibility(INVISIBLE);
        processCastData(output);

    }

    public void processCastData(String cast) {
        String actorId = "";
        String name = "";
        String actorCover;
        names = new ArrayList<>();
        actorIds = new ArrayList<>();
        actorCovers = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(cast);

            String results = jsonObject.getString("cast");

            JSONArray arr = new JSONArray(results);

            for (int i = 0; i < arr.length(); i++) {

                JSONObject jsonPart = arr.getJSONObject(i);

                actorId = jsonPart.getString("id");
                actorIds.add(actorId);

                name = jsonPart.getString("name");
                names.add(name);

                actorCover = "http://image.tmdb.org/t/p/w780" + jsonPart.getString("profile_path");
                actorCovers.add(actorCover);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        printCastImages();

    }

    public void printCastImages() {

        DownloadImage taskImage1 = new DownloadImage();
        DownloadImage taskImage2 = new DownloadImage();
        DownloadImage taskImage3 = new DownloadImage();

        Bitmap image1 = null;
        Bitmap image2 = null;
        Bitmap image3 = null;

        try {

            image1 = taskImage1.execute(actorCovers.get(0)).get();
            image2 = taskImage2.execute(actorCovers.get(1)).get();
            image3 = taskImage3.execute(actorCovers.get(2)).get();

        } catch (Exception e) {

            e.printStackTrace();

        }

        castImageView1.setImageBitmap(image1);
        castImageView2.setImageBitmap(image2);
        castImageView3.setImageBitmap(image3);

    }

    public void showActorDetails(View view) {

        String tag = (String) view.getTag();
        int tagToInt = Integer.parseInt(tag);

        Intent intent = new Intent(getApplicationContext(), ActorInfo.class);
        intent.putExtra("id", actorIds.get(tagToInt));

        startActivity(intent);

    }

    public void viewFullCast(View view) {

        Intent intent = new Intent(getApplicationContext(), ShowCast.class);
        intent.putStringArrayListExtra("names", names);
        intent.putStringArrayListExtra("actorIds", actorIds);
        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query != "") {

                    SearchHandling handling = new SearchHandling(MovieInfo.this);

                    handling.search(query);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.home:

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                return true;

            case R.id.about:

                Intent secondIntent = new Intent(getApplicationContext(), AboutUs.class);
                startActivity(secondIntent);

                return true;

            default:
                return false;

        }

    }



}