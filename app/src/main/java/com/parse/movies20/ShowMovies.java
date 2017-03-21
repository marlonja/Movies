package com.parse.moviesdb;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import static com.parse.moviesdb.MainActivity.covers;
import static com.parse.moviesdb.MainActivity.movieIds;
import static com.parse.moviesdb.MainActivity.movies;
import static com.parse.moviesdb.MainActivity.overviews;
import static com.parse.moviesdb.MainActivity.releases;

public class ShowMovies extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_movies);

        setTitle("");

        ListView listViewSearch = (ListView)findViewById(R.id.listViewSearch);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, movies){

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };

        listViewSearch.setAdapter(arrayAdapter);

        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), MovieInfo.class);
                intent.putExtra("name", movies.get(position));
                intent.putExtra("cover", covers.get(position));
                intent.putExtra("release", releases.get(position));
                intent.putExtra("overview", overviews.get(position));
                intent.putExtra("movieId", movieIds.get(position));

                startActivity(intent);

            }
        });

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

                    SearchHandling handling = new SearchHandling(ShowMovies.this);

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
