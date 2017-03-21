package com.parse.movies20;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.parse.movies20.MainActivity.actorIds;
import static com.parse.movies20.MainActivity.covers;
import static com.parse.movies20.MainActivity.frontActorIds;
import static com.parse.movies20.MainActivity.frontCovers;
import static com.parse.movies20.MainActivity.frontMovieIds;
import static com.parse.movies20.MainActivity.frontMovies;
import static com.parse.movies20.MainActivity.frontOverviews;
import static com.parse.movies20.MainActivity.frontPeopleCovers;
import static com.parse.movies20.MainActivity.frontReleases;
import static com.parse.movies20.MainActivity.movieIds;
import static com.parse.movies20.MainActivity.movies;
import static com.parse.movies20.MainActivity.overviews;
import static com.parse.movies20.MainActivity.peopleCovers;
import static com.parse.movies20.MainActivity.releases;
import static com.parse.movies20.MainActivity.searchResults;

public class SearchHandling {

    private Context context;

    public SearchHandling(Context context) {
        this.context = context;
    }

    public void search(String query) {

        DownloadTask task = new DownloadTask();
        String person = "";
        String formattedQuery;
        boolean personExists;

        formattedQuery = query.replace(" ", "+");

        try {

            person = task.execute("https://api.themoviedb.org/3/search/person?api_key=64d2a074e794f43cfe1b4782eac289ff&query=" + formattedQuery).get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        personExists = checkPerson(person);

        if (personExists) {

            processPersonData(person);

        } else {

            searchForMovie(formattedQuery);

        }

    }

    public boolean checkPerson(String person) {

        String profile = "";
        String results = "";

        try {

            JSONObject jsonObject = new JSONObject(person);

            results = jsonObject.getString("results");

            JSONArray arr = new JSONArray(results);


            for (int i = 0; i < arr.length(); i++) {

                JSONObject jsonPart = arr.getJSONObject(i);

                profile = jsonPart.getString("name");

            }

            if (profile == "" || profile.isEmpty()) {

                return false;
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }

        return true;
    }

    private void processPersonData(String result) {
        String credits = "";
        String actorId = "";
        String name = "";
        searchResults = new ArrayList<>();
        actorIds = new ArrayList<>();

        if (result.contains("results")) {

            try {

                JSONObject jsonObject = new JSONObject(result);

                String results = jsonObject.getString("results");

                JSONArray arr = new JSONArray(results);


                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    actorId = jsonPart.getString("id");
                    actorIds.add(actorId);

                    name = jsonPart.getString("name");
                    searchResults.add(name);

                }

                if (searchResults != null) {

                    Intent intent = new Intent(context, SearchResults.class);
                    context.startActivity(intent);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void searchForMovie(String query) {

        DownloadTask task = new DownloadTask();
        String result = "";

        try {

            result = task.execute("https://api.themoviedb.org/3/search/movie?api_key=64d2a074e794f43cfe1b4782eac289ff&query=" + query).get();

            if (result != "") {

                processCreditsAndPrint(result);

            } else {

                Toast.makeText(context, "No results, try again.", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(context, "No results, try again.", Toast.LENGTH_SHORT).show();

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

    public void processMoviesFrontPage(String result) {
        frontMovies = new ArrayList<>();
        frontCovers = new ArrayList<>();
        frontReleases = new ArrayList<>();
        frontOverviews = new ArrayList<>();
        frontMovieIds = new ArrayList<>();

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
                frontMovies.add(title);

                cover = "http://image.tmdb.org/t/p/w780" + jsonPart.getString("poster_path");
                frontCovers.add(cover);

                release = jsonPart.getString("release_date");
                frontReleases.add(release);

                overview = jsonPart.getString("overview");
                frontOverviews.add(overview);

                movieId = jsonPart.getString("id");
                frontMovieIds.add(movieId);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void processPeopleCredits(String result) {
        frontPeopleCovers = new ArrayList<>();
        frontActorIds = new ArrayList<>();
        String peopleCover;
        String actorId;


        try {

            JSONObject jsonObject = new JSONObject(result);

            String results = jsonObject.getString("results");

            JSONArray arr = new JSONArray(results);

            for (int i = 0; i < arr.length(); i++) {

                JSONObject jsonPart = arr.getJSONObject(i);

                peopleCover = "http://image.tmdb.org/t/p/w780" + jsonPart.getString("profile_path");
                frontPeopleCovers.add(peopleCover);

                actorId = jsonPart.getString("id");
                frontActorIds.add(actorId);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showSearchResults() {

        Intent intent = new Intent(context, ShowMovies.class);
        context.startActivity(intent);

    }

}
