package com.example.epey_scraper.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.epey_scraper.EpeyUtils.EpeyElement;
import com.example.epey_scraper.EpeyUtils.LastUrl;
import com.example.epey_scraper.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private List<String> productNameList;
    private boolean isScrolling;
    private LastUrl lastUrl = new LastUrl();
    private MainAdapter mainAdapter;
    private List<EpeyElement> epeyElementList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private boolean pressedOnceForExit;
    private DrawerLayout drawer;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get permission for policy to getting image from url
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);

        initComponents();

        lastUrl.setDomain("https://www.epey.com/laptop/");
        setTitle("Laptop");
        new EpeyFetcher(lastUrl.getDomain(), true).execute();

    }


    @SuppressLint("ClickableViewAccessibility")
    private void initComponents() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        Spinner productSpinner = findViewById(R.id.content_spinner);
        mainAdapter = new MainAdapter(MainActivity.this, epeyElementList);

        drawer = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        RecyclerView recyclerElements = findViewById(R.id.recycler_view_content_main);
        recyclerElements.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        recyclerElements.setAdapter(mainAdapter);
        recyclerElements.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLastItemDisplay(recyclerView)) {
                    isScrolling = false;
                    new EpeyFetcher(lastUrl.nextPage(), false).execute();
                }
            }
        });

        navigationView = findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                lastUrl.setDomain("https://www.epey.com/" + menuItem.getTitleCondensed().toString());
                setTitle(menuItem.getTitle());
                new EpeyFetcher(lastUrl.getDomain(), true).execute();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private boolean isLastItemDisplay(RecyclerView recyclerView) {

        if (Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0) {
            int lastItemPos = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();

            return lastItemPos != RecyclerView.NO_POSITION && lastItemPos == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else {

            if (pressedOnceForExit)
                super.onBackPressed();

            pressedOnceForExit = true;
            Toast.makeText(this, "Çıkmak için tekrar basınız!", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pressedOnceForExit = false;
                }
            }, 2000);
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class EpeyFetcher extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "EpeyFetcher";
        private String mUrl;
        private boolean newPage;

        EpeyFetcher(String mUrl, boolean newPage) {
            this.mUrl = mUrl;
            Log.d(TAG, "EpeyFetcher: " + mUrl);
            this.newPage = newPage;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Lütfen Bekleyin");
            progressDialog.setMessage("Bilgiler alınıyor...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (newPage) {
                mainAdapter.clearElementList();
                epeyElementList = new ArrayList<>();
            }
            productNameList = new ArrayList<>();

            try {
                Document selectedPageSource = Jsoup.connect(mUrl).get();

                Elements phoneNames = selectedPageSource
                        .select("ul[class=filter markaliste max-h]")
                        .select("li");

                for (int index = 0; index < phoneNames.size(); index++)
                    productNameList.add(phoneNames.get(index).text());

                Elements elementRow = selectedPageSource.select("ul[class=metin row]");

                for (int index = 0; index < elementRow.size(); index++) {
                    String imUrl = elementRow
                            .get(index)
                            .select("div[class=resim cell] > a > img")
                            .attr("src");

                    String name = elementRow
                            .get(index)
                            .select("li[class=adi cell] > div.detay.cell > a")
                            .text();

                    String infoPageUrl = elementRow
                            .get(index)
                            .select("a[class=urunadi]")
                            .attr("href");

                    String price = elementRow
                            .get(index)
                            .select("li[class=fiyat cell] > a")
                            .text()
                            .split("TL")[0];

                    epeyElementList.add(new EpeyElement(imUrl, infoPageUrl, name, price));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mainAdapter.add2ElementList(epeyElementList);
            mainAdapter.notifyDataSetChanged();

            spinnerUpdate();

            progressDialog.dismiss();
        }

        private void spinnerUpdate() {

            Spinner spinner = (Spinner) navigationView.getMenu().findItem(R.id.nav_spinner).getActionView();
            spinner.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, productNameList));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                        new EpeyFetcher(lastUrl.gotoCategoryPage(productNameList.get(position)), true).execute();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
}
