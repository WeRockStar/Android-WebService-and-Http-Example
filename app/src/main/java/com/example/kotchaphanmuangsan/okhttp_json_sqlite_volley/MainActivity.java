package com.example.kotchaphanmuangsan.okhttp_json_sqlite_volley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kotchaphanmuangsan.okhttp_json_sqlite_volley.database.RedditDAO;
import com.example.kotchaphanmuangsan.okhttp_json_sqlite_volley.model.Listing;
import com.example.kotchaphanmuangsan.okhttp_json_sqlite_volley.model.Post;
import com.google.gson.Gson;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity implements RedditAdapter.MyListItemClickListener {

    public final String REDDIT_URL = "https://www.reddit.com/r/all.json";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestQueue queue = ConnectionManager.getInstance(this);
        String url = REDDIT_URL;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        StringRequest request = new StringRequest(Request.Method.GET, REDDIT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Listing listing = new Gson().fromJson(response, Listing.class);

                List<Post> postList = listing.getPostList();

                RedditAdapter adapter = new RedditAdapter(postList, MainActivity.this, MainActivity.this);

                //
                RedditDAO.getsInstance().storePosts(MainActivity.this, postList);

                recyclerView.setAdapter(adapter);

                //Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //WHEN AN ERROR

                List<Post> postList = RedditDAO.getsInstance().getPostsFromDB(MainActivity.this);
                RedditAdapter adapter = new RedditAdapter(postList, MainActivity.this, MainActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });
        //ConnectionManager.getInstance(this).add(request);
        queue.add(request);
    }

    @Override
    public void OnItemClick(Post itemClicked) {
        //TODO Open a website with the link
        //Toast.makeText(MainActivity.this , "Item Click" + itemClicked.getTitle() , Toast.LENGTH_SHORT).show();

        Intent webIntent = new Intent(MainActivity.this, WebActivity.class);
        webIntent.putExtra("URL", itemClicked.getPermalink());
        startActivity(webIntent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
