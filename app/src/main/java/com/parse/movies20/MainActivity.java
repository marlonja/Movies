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

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd";

    ImageView theatersImageView1;
    ImageView theatersImageView2;
    ImageView theatersImageView3;
    ImageView peopleImageView1;
    ImageView peopleImageView2;
    ImageView peopleImageView3;
    String currentDate;
    String currentDateMinusOneMonth;

    static ArrayList<String> movies = null;
    static ArrayList<String> covers;
    static ArrayList<String> releases;
    static ArrayList<String> overviews;
    static ArrayList<String> movieIds;
    static ArrayList<String> searchResults;
    static ArrayList<String> actorIds;
    static ArrayList<String> peopleCovers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("");
        theatersImageView1 = (ImageView) findViewById(R.id.theatersImageView1);
        theatersImageView2 = (ImageView) findViewById(R.id.theatersImageView2);
        theatersImageView3 = (ImageView) findViewById(R.id.theatersImageView3);
        peopleImageView1 = (ImageView) findViewById(R.id.peopleImageView1);
        peopleImageView2 = (ImageView) findViewById(R.id.peopleImageView2);
        peopleImageView3 = (ImageView) findViewById(R.id.peopleImageView3);

        getCurrentDate();
        getMoviesInTheaters();
        getPopularPeople();

    }

    private void getPopularPeople() {

        DownloadTask task = new DownloadTask();
        String result = "";

        try {

            result = task.execute("https://api.themoviedb.org/3/person/popular?api_key=64d2a074e794f43cfe1b4782eac289ff").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        if (result != "") {

            SearchHandling handling = new SearchHandling(MainActivity.this);
            handling.processPeopleCredits(result);

            DownloadImage taskImage1 = new DownloadImage();
            DownloadImage taskImage2 = new DownloadImage();
            DownloadImage taskImage3 = new DownloadImage();

            Bitmap image1 = null;
            Bitmap image2 = null;
            Bitmap image3 = null;

            try {

                image1 = taskImage1.execute(peopleCovers.get(0)).get();
                image2 = taskImage2.execute(peopleCovers.get(1)).get();
                image3 = taskImage3.execute(peopleCovers.get(2)).get();


            } catch (Exception e) {

                e.printStackTrace();

            }

            if (image1 != null && image2 != null && image3 != null) {

                peopleImageView1.setImageBitmap(image1);
                peopleImageView2.setImageBitmap(image2);
                peopleImageView3.setImageBitmap(image3);

            }

        }

    }

    public void showPeopleDetails(View view) {

        String tag = (String) view.getTag();
        int tagToInt = Integer.parseInt(tag);

        Intent intent = new Intent(getApplicationContext(), ActorInfo.class);
        intent.putExtra("id", actorIds.get(tagToInt));

        startActivity(intent);

    }

    public void showMovieDetails(View view) {

        String tag = (String) view.getTag();
        int tagToInt = Integer.parseInt(tag);

        getMoviesInTheaters();

        Intent intent = new Intent(getApplicationContext(), MovieInfo.class);
        intent.putExtra("name", movies.get(tagToInt));
        intent.putExtra("cover", covers.get(tagToInt));
        intent.putExtra("release", releases.get(tagToInt));
        intent.putExtra("overview", overviews.get(tagToInt));
        intent.putExtra("movieId", movieIds.get(tagToInt));

        startActivity(intent);

    }

    public void getMoviesInTheaters() {

        DownloadTask task = new DownloadTask();
        String result = "";

        try {

            result = task.execute("https://api.themoviedb.org/3/discover/movie?api_key=64d2a074e794f43cfe1b4782eac289ff&primary_release_date.gte=" + currentDateMinusOneMonth + "&primary_release_date.lte=" + currentDate).get();

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (result != "") {

            SearchHandling handling = new SearchHandling(MainActivity.this);
            handling.processMovieCredits(result);

            DownloadImage taskImage1 = new DownloadImage();
            DownloadImage taskImage2 = new DownloadImage();
            DownloadImage taskImage3 = new DownloadImage();

            Bitmap image1 = null;
            Bitmap image2 = null;
            Bitmap image3 = null;

            try {

                image1 = taskImage1.execute(covers.get(0)).get();
                image2 = taskImage2.execute(covers.get(1)).get();
                image3 = taskImage3.execute(covers.get(2)).get();


            } catch (Exception e) {

                e.printStackTrace();

            }

            if (image1 != null && image2 != null && image3 != null) {

                theatersImageView1.setImageBitmap(image1);
                theatersImageView2.setImageBitmap(image2);
                theatersImageView3.setImageBitmap(image3);

            }

        }

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

                    SearchHandling handling = new SearchHandling(MainActivity.this);

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

            default:
                return false;

        }

    }

    public void getCurrentDate() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        currentDate = sdf.format(cal.getTime());

        String[] parts = currentDate.split("-");
        String year = parts[0];
        String month = parts[1];
        String day = parts[2];

        int monthInt = Integer.parseInt(month);
        int yearInt = Integer.parseInt(year);

        if (monthInt != 1) {

            monthInt--;
            month = String.valueOf(monthInt);

            if (monthInt < 10) {
                month = "0" + String.valueOf(monthInt);

            }

        } else {

            yearInt--;
            year = String.valueOf(yearInt);
            month = "12";

        }

        currentDateMinusOneMonth = year + "-" + month + "-" + day;

    }


}

