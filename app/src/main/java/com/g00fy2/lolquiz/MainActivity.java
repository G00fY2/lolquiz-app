package com.g00fy2.lolquiz;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.g00fy2.lolquiz.exception.ApiException;
import com.g00fy2.lolquiz.riotapi.FetchAndStoreCallbackInterface;
import com.g00fy2.lolquiz.riotapi.StaticDataChampion;
import com.g00fy2.lolquiz.riotapi.StaticDataVersion;
import com.g00fy2.lolquiz.riotapi.staticdata.FetchAndStoreResult;
import com.konifar.fab_transformation.FabTransformation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements FetchAndStoreCallbackInterface {

    private Map<String, String> apiValues = new HashMap<>();
    private String latestVersion;
    TextView localVersionText;
    TextView latestVersionText;
    ProgressBar fetchingBar;
    ImageView imageView;
    Boolean fetchingData;
    View sheet;
    View overlay;
    FloatingActionButton fab;

    public void onClickFab(View view) {
        if (fab.getVisibility() == View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformTo(sheet);
        }
        Timber.d("clicked");
    }

    @Override
    public void onBackPressed() {
        if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).setOverlay(overlay).transformFrom(sheet);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_page);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        //fab.startAnimation(anim);


        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);

        //imageView = (ImageView) findViewById(R.id.randomStartImg);
        //setRandomMainWallpaper()

        localVersionText = (TextView) findViewById(R.id.textViewMain2);
        latestVersionText = (TextView) findViewById(R.id.textViewMain4);
        fetchingBar = (ProgressBar) findViewById(R.id.progressBar);
        sheet = findViewById(R.id.sheet);
        overlay = findViewById(R.id.overlay);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);


        apiValues.put("urlHost", ".api.pvp.net");
        apiValues.put("urlPath", "/api/lol/");
        //TODO: Nicht ins REPO!
        apiValues.put("apiKey", "ENTER API KEY HERE");
        apiValues.put("region", "euw");

        getLatestVersion();
        //getData();
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miRefresh:
                localVersionText.setText("");
                getLatestData();
                return true;
            case R.id.miSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStartQuizClick(View view) {
        if (!fetchingData) {
            Intent intent = new Intent(this, QuestionsActivity.class);
            startActivity(intent);
            //finish();
        } else {
            Toast.makeText(getApplicationContext(), "Waiting for data beeing loaded.", Toast.LENGTH_SHORT).show();
        }
    }
    private void getLatestVersion() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            try {
                new StaticDataVersion(apiValues, this).execute();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "No network connection", Snackbar.LENGTH_LONG)
                    //.setAction("Retry", mOnClickListener)
                    //.setActionTextColor(Color.RED)
                    .show();
        }

    }

    private void getLatestData() {
        fetchingData = true;
        fetchingBar.setVisibility(View.VISIBLE);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean catchImages = true;

        if (isConnected) {
            try {
                new StaticDataChampion(apiValues, latestVersion, this, catchImages, getApplicationContext()).execute();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "No network connection", Snackbar.LENGTH_LONG)
                    //.setAction("Retry", mOnClickListener)
                    //.setActionTextColor(Color.RED)
                    .show();
        }
    }

    @Override
    public void fetchAndStoreCallback(FetchAndStoreResult result) {
        if (result.getVersionResult()) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Latest version is " + result.getVersion(), Snackbar.LENGTH_SHORT)
                    .show();
            latestVersion = result.getVersion();
            latestVersionText.setText(latestVersion);
        }
        else {
            fetchingBar.setVisibility(View.GONE);
            fetchingData = false;

            Snackbar.make(findViewById(android.R.id.content),
                    Integer.toString(result.getDataCount()) + " Champions read in (" + Integer.toString(result.getImgDateCount()) + " Images)", Snackbar.LENGTH_SHORT)
                    .show();
            localVersionText.setText(result.getVersion());
        }
    }

    private Drawable getRandomWallpaper() {
        Field[] drawables = com.g00fy2.lolquiz.R.drawable.class.getFields();
        List<Integer> wallpapers = new ArrayList<>();
        for (Field f : drawables) {
            try {
                String imageFile = f.getName();
                if (imageFile.startsWith("wallpaper_")) {
                    if (f.getType().equals(int.class)) {
                        wallpapers.add(f.getInt(null));
                        //Log.e(this.getClass().getCanonicalName(), "R.drawable." + imageFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (wallpapers.isEmpty()) {
            return null;
        } else {
            int imgId = wallpapers.get(new Random().nextInt(wallpapers.size()));
            return ResourcesCompat.getDrawable(getResources(), imgId, null);
        }
    }

    private void setRandomMainWallpaper() {
            Drawable image = getRandomWallpaper();
            if (image != null && !image.getConstantState().equals(imageView.getDrawable().getConstantState()) ) {
                imageView.setImageDrawable(image);
                //Log.e(this.getClass().getCanonicalName(), image.toString());
            }
    }

}
