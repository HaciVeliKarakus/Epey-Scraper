package com.example.epey_scraper.info;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.epey_scraper.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InfoActivity extends AppCompatActivity {

    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        String infoPageUrl = getIntent().getStringExtra("url");
        String name = getIntent().getStringExtra("name");

        Toolbar toolbar = findViewById(R.id.toolbar_info);
        setSupportActionBar(toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title_info);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mTitle.setText(name);

        new EpeyFetcher(infoPageUrl).execute();
    }

    @SuppressLint("StaticFieldLeak")
    public class EpeyFetcher extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "EpeyFetcher";

        Document document;

        List<String> catagoryTitles = new ArrayList<>();
        List<String> catagoryDetails = new ArrayList<String>();
        List<String> catagoryImageUrl = new ArrayList<>();

        private ProgressDialog progressDialog;
        private String mUrl;

        EpeyFetcher(String mUrl) {
            this.mUrl = mUrl;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(InfoActivity.this);
            progressDialog.setTitle("Lütfen Bekleyin");
            progressDialog.setMessage("Bilgiler alınıyor...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                document = Jsoup.connect(mUrl).get();

                Elements ele = document.select("div[id=bilgiler]").select("div[id=grup]");
                for (int i = 0; i < ele.size(); i++) {

                    String title = ele.get(i).select("h3 > span").text();
                    catagoryTitles.add(title);

                    catagoryDetails.add(ele.get(i).select("ul").html());

                    catagoryImageUrl.add(ele.get(i).select("h3 > img").attr("src"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            RecyclerView recyclerView = findViewById(R.id.recycler_view_content_info);
            recyclerView.setLayoutManager(new LinearLayoutManager(InfoActivity.this));
            recyclerView.setAdapter(new InfoAdapter(InfoActivity.this, recyclerView, catagoryImageUrl, catagoryTitles, catagoryDetails));

            progressDialog.dismiss();
        }

    }

}
