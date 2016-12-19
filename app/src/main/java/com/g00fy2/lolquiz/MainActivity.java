package com.g00fy2.lolquiz;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g00fy2.lolquiz.exception.ApiException;
import com.g00fy2.lolquiz.rest.RestApi;
import com.g00fy2.lolquiz.rest.RetroClient;
import com.g00fy2.lolquiz.riotapi.FetchAndStoreCallbackInterface;
import com.g00fy2.lolquiz.riotapi.StaticDataChampion;
import com.g00fy2.lolquiz.riotapi.staticdata.ChampionListDto;
import com.g00fy2.lolquiz.riotapi.staticdata.FetchAndStoreResult;
import com.g00fy2.lolquiz.sqlite.ChampionsDataSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FetchAndStoreCallbackInterface {

    private Map<String, String> apiValues = new HashMap<>();
    TextView mTestTxt;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);

        //imageView = (ImageView) findViewById(R.id.randomStartImg);
        //setRandomMainWallpaper();


        mTestTxt = (TextView) findViewById(R.id.textViewMain2);

        apiValues.put("urlHost", ".api.pvp.net");
        apiValues.put("urlPath", "/api/lol/");
        //TODO: Nicht ins REPO!
        apiValues.put("apiKey", "ENTER API KEY HERE");
        apiValues.put("region", "euw");

        getData();
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
                mTestTxt.setText("");
                getData();
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
        Intent intent = new Intent(this, QuestionsActivity.class);
        startActivity(intent);
        //finish();
    }

    private void getData() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            try {
                new StaticDataChampion(apiValues, this, getApplicationContext()).execute();
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

    private void getDataRetroAsync() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            try {
                StaticDataChampion champApi = new StaticDataChampion(apiValues, this, getApplicationContext());
                RestApi restApi = RetroClient.getRestApi(champApi.buildStaticDataBaseUrl());

                Call<ChampionListDto> call = restApi.getChampionList("true", "stats", apiValues.get("apiKey"));

                call.enqueue(new Callback<ChampionListDto>() {
                    @Override
                    public void onResponse(Call<ChampionListDto> call, Response<ChampionListDto> response) {
                        if (response.isSuccessful()) {
                            ChampionListDto champListDto = response.body();
                            mTestTxt.setText(champListDto.version);

                            //TEST
                            ChampionsDataSource storedata = new ChampionsDataSource(getApplicationContext());
                            storedata.openWriteable();
                            int count = storedata.insertChampionsTransaction(champListDto);
                            storedata.close();
                            Snackbar.make(findViewById(android.R.id.content), Integer.toString(count) + " Champions eingelesen", Snackbar.LENGTH_LONG)
                                    //.setAction("Undo", mOnClickListener)
                                    //.setActionTextColor(Color.RED)
                                    .show();

                        } else {
                            mTestTxt.setText(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ChampionListDto> call, Throwable t) {
                        mTestTxt.setText(t.toString());
                    }
                });

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
        Snackbar.make(findViewById(android.R.id.content),
                Integer.toString(result.getDataCount()) + " Champions eingelesen (" + Integer.toString(result.getImgDateCount()) + " Images)", Snackbar.LENGTH_SHORT)
                //.setAction("Undo", mOnClickListener)
                //.setActionTextColor(Color.RED)
                .show();
        mTestTxt.setText(result.getVersion());
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
