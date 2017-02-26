package com.example.android.moviedb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.i_sort_by:
                showMenu(findViewById(R.id.i_sort_by));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.i_highest_rated:
                Toast.makeText(MainActivity.this, "Highest Rated", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.i_most_popular:
                Toast.makeText(MainActivity.this, "Most Popular", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    /**
     * Helper Method to display the pop-up menu
     * @param v is the view to which the pop-up will be attached
     */
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(MainActivity.this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.sortby_menu);
        popup.show();
    }
}
