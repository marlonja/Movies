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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd";

    ImageView theatersImageView1;
    ImageView theatersImageView2;
    ImageView theatersImageView3;
    ImageView peopleImageView1;
    ImageView peopleImageView2;
    ImageView peopleImageView3;
    ImageView inFocusImageView;
    ImageView imageViewLogo;
    String currentDate;
    String currentDateMinusOneMonth;
    TextView bioTextView;
    TextView textViewTheaters;
    TextView line1;
    TextView line2;
    TextView line3;
    TextView people;
    TextView focus;
    LinearLayout linearLayoutTwo;
    LinearLayout linearLayoutMovies;
    RelativeLayout relativeLayout;

    static ArrayList<String> frontMovies = null;
    static ArrayList<String> frontCovers;
    static ArrayList<String> frontReleases;
    static ArrayList<String> frontOverviews;
    static ArrayList<String> frontMovieIds;
    static ArrayList<String> frontActorIds;
    static ArrayList<String> frontPeopleCovers;
    static ArrayList<String> movies = null;
    static ArrayList<String> covers;
    static ArrayList<String> releases;
    static ArrayList<String> overviews;
    static ArrayList<String> movieIds;
    static ArrayList<String> searchResults;
    static ArrayList<String> actorIds;
    static ArrayList<String> peopleCovers;
    static String cutBioString;
    static String cutCoverMovieHome1;
    static String cutCoverMovieHome2;
    static String cutCoverMovieHome3;
    static String cutCoverPeopleHome1;
    static String cutCoverPeopleHome2;
    static String cutCoverPeopleHome3;
    static String cutCoverInFocus;
    ProgressBar pb;

    static HashMap<String, Bitmap> hmap;

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
        inFocusImageView = (ImageView) findViewById(R.id.inFocusImageView);
        imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        bioTextView = (TextView) findViewById(R.id.bioTextView);
        textViewTheaters = (TextView) findViewById(R.id.textViewTheaters);
        line1 = (TextView) findViewById(R.id.line1);
        line2 = (TextView) findViewById(R.id.line2);
        line3 = (TextView) findViewById(R.id.line3);
        people = (TextView) findViewById(R.id.people);
        focus = (TextView) findViewById(R.id.focus);
        linearLayoutTwo = (LinearLayout) findViewById(R.id.linearLayoutTwo);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        linearLayoutMovies = (LinearLayout) findViewById(R.id.linearLayoutMovies);

        pb = (ProgressBar) findViewById(R.id.progressBar);

        pb.setVisibility(INVISIBLE);

        getCurrentDate();

        setMovieImages();

    }

    public void setMovieImages() {

        if (hmap != null) {

            imageViewLogo.setVisibility(INVISIBLE);
            textViewTheaters.setVisibility(VISIBLE);
            linearLayoutMovies.setVisibility(VISIBLE);
            line1.setVisibility(VISIBLE);
            line2.setVisibility(VISIBLE);
            line3.setVisibility(VISIBLE);
            people.setVisibility(VISIBLE);
            linearLayoutTwo.setVisibility(VISIBLE);
            focus.setVisibility(VISIBLE);
            relativeLayout.setVisibility(VISIBLE);

            theatersImageView1.setImageBitmap(hmap.get(cutCoverMovieHome1));
            theatersImageView2.setImageBitmap(hmap.get(cutCoverMovieHome2));
            theatersImageView3.setImageBitmap(hmap.get(cutCoverMovieHome3));

            setPeopleImages();

        } else {

            hmap = new HashMap<>();
            getMoviesInTheaters();

        }

    }

    public void getMoviesInTheaters() {
        pb.setVisibility(VISIBLE);
        DownloadInTheatres task = new DownloadInTheatres();
        task.delegate = this;

        try {

            task.execute("https://api.themoviedb.org/3/discover/movie?api_key=64d2a074e794f43cfe1b4782eac289ff&primary_release_date.gte=" + currentDateMinusOneMonth + "&primary_release_date.lte=" + currentDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void processFinish(String output) {
        pb.setVisibility(INVISIBLE);

        processMovieData(output);

    }

    public void processMovieData(String result) {

        if (result != "") {

            SearchHandling handling = new SearchHandling(MainActivity.this);
            handling.processMoviesFrontPage(result);

            DownloadImage taskImage1 = new DownloadImage();
            DownloadImage taskImage2 = new DownloadImage();
            DownloadImage taskImage3 = new DownloadImage();

            Bitmap image1 = null;
            Bitmap image2 = null;
            Bitmap image3 = null;

            try {

                image1 = taskImage1.execute(frontCovers.get(0)).get();
                image2 = taskImage2.execute(frontCovers.get(1)).get();
                image3 = taskImage3.execute(frontCovers.get(2)).get();

                String coverMovieHome1 = frontCovers.get(0);
                cutCoverMovieHome1 = coverMovieHome1.substring(30, coverMovieHome1.lastIndexOf("g") + 1);

                String coverMovieHome2 = frontCovers.get(1);
                cutCoverMovieHome2 = coverMovieHome2.substring(30, coverMovieHome2.lastIndexOf("g") + 1);

                String coverMovieHome3 = frontCovers.get(2);
                cutCoverMovieHome3 = coverMovieHome3.substring(30, coverMovieHome3.lastIndexOf("g") + 1);

                hmap.put(cutCoverMovieHome1, image1);
                hmap.put(cutCoverMovieHome2, image2);
                hmap.put(cutCoverMovieHome3, image3);

                //Log.i("info", String.valueOf(Arrays.asList(hmap)));

            } catch (Exception e) {

                e.printStackTrace();

            }

            if (image1 != null && image2 != null && image3 != null) {

                theatersImageView1.setImageBitmap(image1);
                theatersImageView2.setImageBitmap(image2);
                theatersImageView3.setImageBitmap(image3);

            }

        }

        setPeopleImages();

    }

    public void setPeopleImages() {

        Bitmap temp = hmap.get(cutCoverPeopleHome1);

        if(temp == null) {

            getPopularPeople();

        } else {

            peopleImageView1.setImageBitmap(hmap.get(cutCoverPeopleHome1));
            peopleImageView2.setImageBitmap(hmap.get(cutCoverPeopleHome2));
            peopleImageView3.setImageBitmap(hmap.get(cutCoverPeopleHome3));

            setFocus();

        }

    }

    private void getPopularPeople() {

        DownloadPeople task = new DownloadPeople();

        try {

            task.execute("https://api.themoviedb.org/3/person/popular?api_key=64d2a074e794f43cfe1b4782eac289ff");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void processPeopleData(String result) {

        if (result != "") {

            SearchHandling handling = new SearchHandling(MainActivity.this);
            handling.processPeopleCredits(result);

        }

        printPeopleCovers();

    }

    public void printPeopleCovers() {

        DownloadImage taskImage1 = new DownloadImage();
        DownloadImage taskImage2 = new DownloadImage();
        DownloadImage taskImage3 = new DownloadImage();

        Bitmap image1 = null;
        Bitmap image2 = null;
        Bitmap image3 = null;

        try {

            image1 = taskImage1.execute(frontPeopleCovers.get(1)).get();
            image2 = taskImage2.execute(frontPeopleCovers.get(2)).get();
            image3 = taskImage3.execute(frontPeopleCovers.get(3)).get();

            String coverPeopleHome1 = frontPeopleCovers.get(1);
            cutCoverPeopleHome1 = coverPeopleHome1.substring(30, coverPeopleHome1.lastIndexOf("g") + 1);

            String coverPeopleHome2 = frontPeopleCovers.get(2);
            cutCoverPeopleHome2 = coverPeopleHome2.substring(30, coverPeopleHome2.lastIndexOf("g") + 1);

            String coverPeopleHome3 = frontPeopleCovers.get(3);
            cutCoverPeopleHome3 = coverPeopleHome3.substring(30, coverPeopleHome3.lastIndexOf("g") + 1);

            hmap.put(cutCoverPeopleHome1, image1);
            hmap.put(cutCoverPeopleHome2, image2);
            hmap.put(cutCoverPeopleHome3, image3);


        } catch (Exception e) {

            e.printStackTrace();

        }

        imageViewLogo.setVisibility(INVISIBLE);
        textViewTheaters.setVisibility(VISIBLE);
        linearLayoutMovies.setVisibility(VISIBLE);
        line1.setVisibility(VISIBLE);
        line2.setVisibility(VISIBLE);
        line3.setVisibility(VISIBLE);
        people.setVisibility(VISIBLE);
        linearLayoutTwo.setVisibility(VISIBLE);

        peopleImageView1.setImageBitmap(image1);
        peopleImageView2.setImageBitmap(image2);
        peopleImageView3.setImageBitmap(image3);

        setFocus();
    }

    public void setFocus() {

        Bitmap temp = hmap.get(cutCoverInFocus);

        if (temp == null) {

            processInFocus();

        } else {

            inFocusImageView.setImageBitmap(hmap.get(cutCoverInFocus));
            bioTextView.setText(cutBioString);

        }

    }

    public void processInFocus() {

        String actorInfo = "";
        DownloadTask task = new DownloadTask();

        try {

            actorInfo = task.execute("https://api.themoviedb.org/3/person/" + frontActorIds.get(0) + "?api_key=64d2a074e794f43cfe1b4782eac289ff&append_to_response=images").get();

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


        try {

            JSONObject jsonObject = new JSONObject(actorData);

            String bio = jsonObject.getString("biography");
            cutBioString = "";

            int maxLength = 200;

            if (bio.length() > maxLength) {

                cutBioString = bio.substring(0, maxLength) + " ...";

            } else {

                cutBioString = bio;

            }

            focus.setVisibility(VISIBLE);
            relativeLayout.setVisibility(VISIBLE);
            bioTextView.setText(cutBioString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        DownloadImage dlTask = new DownloadImage();
        Bitmap myImage = null;

        try {
            myImage = dlTask.execute(frontPeopleCovers.get(0)).get();

            String coverInFocus = frontPeopleCovers.get(0);
            cutCoverInFocus = coverInFocus.substring(30, coverInFocus.lastIndexOf("g") + 1);

            hmap.put(cutCoverInFocus, myImage);

        } catch (Exception e) {

            e.printStackTrace();

        }

        if (myImage != null) {

            inFocusImageView.setImageBitmap(myImage);

        }

    }

    public void showPeopleDetails(View view) {

        String tag = (String) view.getTag();
        int tagToInt = Integer.parseInt(tag);

        Intent intent = new Intent(getApplicationContext(), ActorInfo.class);
        intent.putExtra("id", frontActorIds.get(tagToInt));

        startActivity(intent);

    }

    public void showMovieDetails(View view) {

        String tag = (String) view.getTag();
        int tagToInt = Integer.parseInt(tag);

        getMoviesInTheaters();

        Intent intent = new Intent(getApplicationContext(), MovieInfo.class);
        intent.putExtra("name", frontMovies.get(tagToInt));
        intent.putExtra("cover", frontCovers.get(tagToInt));
        intent.putExtra("release", frontReleases.get(tagToInt));
        intent.putExtra("overview", frontOverviews.get(tagToInt));
        intent.putExtra("movieId", frontMovieIds.get(tagToInt));

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

            case R.id.about:

                Intent secondIntent = new Intent(getApplicationContext(), AboutUs.class);
                startActivity(secondIntent);

                return true;

            default:
                return false;

        }

    }

    public void getCurrentDate() {

        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT_NOW);
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

    public class DownloadPeople extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pb.setVisibility(INVISIBLE);
            processPeopleData(result);

        }
    }

}

