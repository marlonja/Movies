package com.parse.movies20;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;



public class MovieInfo extends AppCompatActivity {

    TextView synopsisTextView;
    TextView movieTitle;
    ImageView imageView;
    TextView releaseTextView;
    String movieId;
    String title;
    String cover;
    String release;
    String overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        setTitle("Movie info");

        movieTitle = (TextView) findViewById(R.id.movieTitle);
        imageView = (ImageView) findViewById(R.id.imageView);
        releaseTextView = (TextView) findViewById(R.id.releaseTextView);
        synopsisTextView = (TextView) findViewById(R.id.synopsisTextView);

        Intent intent = getIntent();
        title = intent.getStringExtra("name");
        cover = intent.getStringExtra("cover");
        release = intent.getStringExtra("release");
        overview = intent.getStringExtra("overview");
        movieId = intent.getStringExtra("movieId");

        movieTitle.setText(title);
        releaseTextView.setText(release);
        synopsisTextView.setText(overview);

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

            default:
                return false;

        }

    }

}