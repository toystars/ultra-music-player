package com.bmustapha.ultramediaplayer.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.fragments.MusicFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                displayView((String) menuItem.getTitle());
                // Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView("Music");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.playlist_add:
                Toast.makeText(this, "Playlist Added", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayView(String title) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (title) {
            case "Music":
                fragment = new MusicFragment();
                // PlayListSync.updateAdapter(null);
                break;
            case "Videos":
                // fragment = new FavouritesFragment();
                // PlayListSync.updateAdapter(null);
                break;
            case "Images":
                // fragment = new PlayListFragment();
                break;
            case "Deezer":
                // fragment = new VideoFragment();
                // PlayListSync.updateAdapter(null);
                break;
            case "Youtube":
                // fragment = new VideoFragment();
                // PlayListSync.updateAdapter(null);
                break;
            default:
                break;
        }

        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            // close drawer
            mDrawerLayout.closeDrawers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
