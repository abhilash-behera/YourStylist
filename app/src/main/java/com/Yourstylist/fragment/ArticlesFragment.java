package com.Yourstylist.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Yourstylist.R;
import com.Yourstylist.Utils;
import com.Yourstylist.activity.LoginScreenActivity;
import com.Yourstylist.adapter.ArticlesAdapter;
import com.Yourstylist.retrofit.Api;
import com.Yourstylist.retrofit.Article;
import com.Yourstylist.retrofit.ArticleResponse;
import com.Yourstylist.retrofit.Like;
import com.Yourstylist.retrofit.LoginRequest;
import com.Yourstylist.retrofit.LoginResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.Yourstylist.Utils.tag;

public class ArticlesFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArticlesAdapter articlesAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ArticlesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_articles, container, false);
        initializeViews();
        continueExecution();
        return view;
    }

    private void initializeViews() {
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Utils.isNetworkAvailable(getActivity(),view.findViewById(R.id.fragment_articles))){
                    fetchArticles(1);
                }
            }
        });
    }

    private void continueExecution() {
        if(Utils.isNetworkAvailable(getActivity(),view.findViewById(R.id.fragment_articles))){
            fetchArticles(0);
        }
    }

    public void fetchArticles(int type){
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        if(type==0){
            progressDialog.setMessage("Getting Articles...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        Api service=retrofit.create(Api.class);
        Call<ArticleResponse> call=service.getArticles();
        call.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, final Response<ArticleResponse> response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        if(response.isSuccessful()){
                            SharedPreferences sharedPreferences=getActivity().getSharedPreferences(Utils.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE);
                            String myEmail=sharedPreferences.getString(Utils.USER_EMAIL,"");
                            List<Article> articleList=response.body().getData();
                            ArrayList<Article> articles=new ArrayList<Article>();
                            for(int i=0;i<articleList.size();i++){
                                Article article=articleList.get(i);
                                if(containsLike(articleList.get(i).getLikes(),myEmail)){
                                    article.setLiked(true);
                                }
                                articles.add(articleList.get(i));
                            }

                            articlesAdapter=new ArticlesAdapter(articles,getActivity());
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(articlesAdapter);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar snackbar=Snackbar.make(view.findViewById(R.id.fragment_articles),"Could not get articles. Try again later.",Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
        });
    }

    private boolean containsLike(List<Like> likes, String myEmail) {
        for(int i=0;i<likes.size();i++){
            if(likes.get(i).getEmail().equalsIgnoreCase(myEmail)){
                return true;
            }
        }
        return false;
    }


}
