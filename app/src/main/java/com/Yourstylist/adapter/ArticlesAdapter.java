package com.Yourstylist.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Yourstylist.R;
import com.Yourstylist.Utils;
import com.Yourstylist.activity.HomeScreenActivity;
import com.Yourstylist.fragment.ArticlesFragment;
import com.Yourstylist.retrofit.Api;
import com.Yourstylist.retrofit.Article;
import com.Yourstylist.retrofit.ArticleResponse;
import com.Yourstylist.retrofit.CommentArticleRequest;
import com.Yourstylist.retrofit.CommentArticleResponse;
import com.Yourstylist.retrofit.LikeArticleRequest;
import com.Yourstylist.retrofit.LikeArticleResponse;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.Yourstylist.Utils.tag;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyViewHolder> {
    private ArrayList<Article> articles;
    private Context context;
    private SharedPreferences sharedPreferences;
    private DisplayImageOptions defaultOptions;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgArticle;
        public TextView txtTitle;
        public TextView txtArticle;
        public TextView txtLikes;
        public TextView txtComments;
        public LinearLayout likeLayout;
        public LinearLayout commentLayout;
        public ImageView imgLike;


        public MyViewHolder(View view){
            super(view);
            imgArticle=(ImageView)view.findViewById(R.id.imgArticle);
            txtTitle=(TextView)view.findViewById(R.id.txtTitle);
            txtArticle=(TextView)view.findViewById(R.id.txtArticle);
            txtLikes=(TextView)view.findViewById(R.id.txtLikes);
            txtComments=(TextView)view.findViewById(R.id.txtComments);
            likeLayout=(LinearLayout)view.findViewById(R.id.likeLayout);
            commentLayout=(LinearLayout)view.findViewById(R.id.commentLayout);
            imgLike=(ImageView)view.findViewById(R.id.imgLike);
        }
    }

    public ArticlesAdapter(ArrayList<Article> articles, Context context){
        this.articles=articles;
        this.context=context;
        sharedPreferences=((Activity)context).getSharedPreferences(Utils.USER_SHARED_PREFERENCE,Context.MODE_PRIVATE);
        defaultOptions=new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_row_view,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Article article=articles.get(position);
        ImageLoader.getInstance().displayImage(article.getImage(),holder.imgArticle,defaultOptions);
        holder.txtTitle.setText(article.getTitle());
        holder.txtArticle.setText(article.getText());
        holder.txtLikes.setText(article.getLikes().size()+" Likes");
        holder.txtComments.setText(article.getComments().size()+" Comments");
        Log.d(tag,"is liked: "+article.isLiked());
        if(article.isLiked()){
            holder.imgLike.setImageResource(R.drawable.liked_icon);
        }
        else{
            holder.imgLike.setImageResource(R.drawable.unliked_icon);
        }
        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!article.isLiked()){
                    likeArticle(holder,article);
                }
            }
        });

        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentArticle(holder,article);
            }
        });
    }

    private void commentArticle(final MyViewHolder holder,final Article article) {
        final EditText txtComment=new EditText(context);
        txtComment.setHint("Enter your comments here...");
        txtComment.setPadding(10,10,10,10);
        txtComment.setBackgroundResource(R.drawable.frame_background);
        txtComment.setMinLines(8);
        txtComment.setGravity(Gravity.CENTER);

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Comment");
        builder.setView(txtComment);
        builder.setCancelable(false);
        builder.setPositiveButton("Comment", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!txtComment.getText().toString().trim().isEmpty()){
                    final ProgressDialog progressDialog=new ProgressDialog(context);
                    progressDialog.setMessage("Posting Comment");
                    progressDialog.show();
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
                    Call<CommentArticleResponse> call=service.commentArticle(new CommentArticleRequest(article.getId(),sharedPreferences.getString(Utils.USER_EMAIL,""),txtComment.getText().toString()));
                    call.enqueue(new Callback<CommentArticleResponse>() {
                        @Override
                        public void onResponse(Call<CommentArticleResponse> call, final Response<CommentArticleResponse> response) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    if(response.isSuccessful()){
                                        if(response.body().isSuccess()){
                                            Toast.makeText(context, "Comment done successfully", Toast.LENGTH_SHORT).show();
                                            if(context instanceof HomeScreenActivity){
                                                ((HomeScreenActivity)context).onNavigationItemSelected(new MenuItem() {
                                                    @Override
                                                    public int getItemId() {
                                                        return R.id.nav_articles;
                                                    }

                                                    @Override
                                                    public int getGroupId() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public int getOrder() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public MenuItem setTitle(CharSequence title) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setTitle(@StringRes int title) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public CharSequence getTitle() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setTitleCondensed(CharSequence title) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public CharSequence getTitleCondensed() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setIcon(Drawable icon) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setIcon(@DrawableRes int iconRes) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public Drawable getIcon() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setIntent(Intent intent) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public Intent getIntent() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setShortcut(char numericChar, char alphaChar) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setNumericShortcut(char numericChar) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public char getNumericShortcut() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public MenuItem setAlphabeticShortcut(char alphaChar) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public char getAlphabeticShortcut() {
                                                        return 0;
                                                    }

                                                    @Override
                                                    public MenuItem setCheckable(boolean checkable) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public boolean isCheckable() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public MenuItem setChecked(boolean checked) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public boolean isChecked() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public MenuItem setVisible(boolean visible) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public boolean isVisible() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public MenuItem setEnabled(boolean enabled) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public boolean isEnabled() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean hasSubMenu() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public SubMenu getSubMenu() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public ContextMenu.ContextMenuInfo getMenuInfo() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public void setShowAsAction(int actionEnum) {

                                                    }

                                                    @Override
                                                    public MenuItem setShowAsActionFlags(int actionEnum) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setActionView(View view) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setActionView(@LayoutRes int resId) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public View getActionView() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public MenuItem setActionProvider(ActionProvider actionProvider) {
                                                        return null;
                                                    }

                                                    @Override
                                                    public ActionProvider getActionProvider() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public boolean expandActionView() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean collapseActionView() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean isActionViewExpanded() {
                                                        return false;
                                                    }

                                                    @Override
                                                    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                                                        return null;
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<CommentArticleResponse> call, Throwable t) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Failed to comment. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(context, "Enter some comments first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void likeArticle(MyViewHolder holder, final Article article) {
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
        Call<LikeArticleResponse> call=service.likeArticle(new LikeArticleRequest(article.getId(),sharedPreferences.getString(Utils.USER_EMAIL,"")));
        call.enqueue(new Callback<LikeArticleResponse>() {
            @Override
            public void onResponse(Call<LikeArticleResponse> call,final Response<LikeArticleResponse> response) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response.isSuccessful()){
                            if(response.body().isSuccess()){
                                article.setLiked(true);
                                notifyDataSetChanged();
                            }
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<LikeArticleResponse> call, Throwable t) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Action failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
