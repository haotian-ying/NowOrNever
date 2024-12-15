package com.example.recyclar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

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