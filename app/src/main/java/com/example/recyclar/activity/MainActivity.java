package com.example.recyclar.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import com.example.recyclar.fragment.CalendarFragment;
import com.example.recyclar.fragment.HomeFragment;
import com.example.recyclar.R;
import com.example.recyclar.fragment.TimerFragment;
import com.example.recyclar.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    //private FloatingActionButton fab;
    //haredPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
    //boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fab = findViewById(R.id.fab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("NowOrNever");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        loadFragment(new HomeFragment());

        // 设置 BottomNavigationView 点击事件
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.nav_calendar:
                    fragment = new CalendarFragment();
                    break;
                case R.id.nav_timer:
                    fragment = new TimerFragment();
                    break;
                case R.id.nav_user:
                    fragment = new UserFragment();
                    break;
                default:
                    fragment = new HomeFragment();
            }
            return loadFragment(fragment);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        /*assert mSearchView != null;
        mSearchView.setIconified(false);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.onActionViewExpanded();*/
        return true;
    }

    /**
     * 加载 Fragment 到 FrameLayout
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}