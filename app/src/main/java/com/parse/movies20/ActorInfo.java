package com.parse.movies20;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.parse.movies20.MainActivity.covers;
import static com.parse.movies20.MainActivity.movieIds;
import static com.parse.movies20.MainActivity.movies;
import static com.parse.movies20.MainActivity.overviews;
import static com.parse.movies20.MainActivity.releases;

public class ActorInfo extends AppCompatActivity {

    String id;
    TextView actorName;
    TextView bioTextView;
    TextView birthTextView;
    ImageView imageViewProfile;
    TextView birthplaceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_info);

        setTitle("");

        actorName = (TextView) findViewById(R.id.actorName);
        bioTextView = (TextView) findViewById(R.id.bioTextView);
        birthTextView = (TextView) findViewById(R.id.birthTextView);
        imageViewProfile = (ImageView) findViewById(R.id.imageViewProfile);
        birthplaceTextView = (TextView) findViewById(R.id.birthplaceTextView);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        getActorInfo(id);
    }

    public void getActorInfo(String actorId) {

        String actorInfo = "";
        DownloadTask task = new DownloadTask();

        try {

            actorInfo = task.execute("https://api.themoviedb.org/3/person/" + actorId + "?api_key=64d2a074e794f43cfe1b4782eac289ff&append_to_response=images").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (actorInfo != "") {

            processActorData(actorInfo);

        }

    }

    private void processActorData(String actorData) {

        String cover = "";

        try {

            JSONObject jsonObject = new JSONObject(actorData);

            String bio = jsonObject.getString("biography");
            String name = jsonObject.getString("name");
            String birthday = jsonObject.getString("birthday");
            String birthplace = jsonObject.getString("place_of_birth");
            cover = "http://image.tmdb.org/t/p/w780" + jsonObject.getString("profile_path");

            actorName.setText(name);

            if (bio == "null") {

                bioTextView.setText("");

            } else {

                bioTextView.setText(bio);

            }

            birthTextView.setText(birthday);
            birthplaceTextView.setText(birthplace);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        DownloadImage dlTask = new DownloadImage();
        Bitmap myImage = null;

        try {
            myImage = dlTask.execute(cover).get();


        } catch (Exception e) {

            e.printStackTrace();

        }

        if (myImage != null) {

            imageViewProfile.setImageBitmap(myImage);

        }

    }

    public void viewMovies(View view) {

        String credits = "";
        DownloadTask task = new DownloadTask();

        try {

            credits = task.execute("https://api.themoviedb.org/3/discover/movie?api_key=64d2a074e794f43cfe1b4782eac289ff&with_cast=" + id).get();

            if (credits != null) {

                processCreditsAndPrint(credits);

            }

        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void processCreditsAndPrint(String result) {

        processMovieCredits(result);

        if (movies.get(0) != "") {

            showSearchResults();

        } else {

            Toast.makeText(this, "No results, try again.", Toast.LENGTH_SHORT).show();

        }

    }

    public void processMovieCredits(String result) {
        movies = new ArrayList<>();
        covers = new ArrayList<>();
        releases = new ArrayList<>();
        overviews = new ArrayList<>();
        movieIds = new ArrayList<>();

        String title;
        String release;
        String cover;
        String overview;
        String movieId;

        try {

            JSONObject jsonObject = new JSONObject(result);

            String results = jsonObject.getString("results");

            JSONArray arr = new JSONArray(results);

            for (int i = 0; i < arr.length(); i++) {

                JSONObject jsonPart = arr.getJSONObject(i);

                title = jsonPart.getString("original_title");
                movies.add(title);

                cover = "http://image.tmdb.org/t/p/w780" + jsonPart.getString("poster_path");
                covers.add(cover);

                release = jsonPart.getString("release_date");
                releases.add(release);

                overview = jsonPart.getString("overview");
                overviews.add(overview);

                movieId = jsonPart.getString("id");
                movieIds.add(movieId);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showSearchResults() {

        Intent intent = new Intent(getApplicationContext(), ShowMovies.class);
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

                    SearchHandling handling = new SearchHandling(ActorInfo.this);

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
