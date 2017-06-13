package com.Yourstylist.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Yourstylist.Constants;
import com.Yourstylist.R;
import com.Yourstylist.Utils;
import com.Yourstylist.activity.PhotoViewActivity;
import com.Yourstylist.retrofit.Api;
import com.Yourstylist.retrofit.ArticleResponse;
import com.Yourstylist.retrofit.Photo;
import com.Yourstylist.retrofit.PhotolistResponse;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GalleryFragment extends Fragment {
    private View view;
    private GridView gridView;
    private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
    public static final int INDEX = 1;
    public static final String POSITION="position";
    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_galary, container, false);
        File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
        if (!testImageOnSdCard.exists()) {
            copyTestImageToSdCard(testImageOnSdCard);
        }
        gridView=(GridView)view.findViewById(R.id.grid);
        fetchAllPhotos();
        return view;
    }

    private void fetchAllPhotos() {
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle("Fetching Photos");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
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
        Call<PhotolistResponse> call=service.getAllPhotos();
        call.enqueue(new Callback<PhotolistResponse>() {
            @Override
            public void onResponse(Call<PhotolistResponse> call, final Response<PhotolistResponse> response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            if(response.body().getSuccess()){
                                List<Photo> photos=response.body().getData();
                                if(photos.size()!=0){
                                    Constants.photoList=response.body().getData();
                                    Constants.IMAGES=new String[photos.size()];
                                    for(int i=0;i<photos.size();i++){
                                        Photo photo=photos.get(i);
                                        Constants.IMAGES[i]=photo.getUrl();
                                        Log.d(Utils.tag,"Image url:"+photo.getUrl());
                                    }
                                    gridView.setAdapter(new ImageAdapter(getActivity()));
                                }
                                else{
                                    Toast.makeText(getActivity(), "No photos present in our database.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "Unable to fetch photos. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<PhotolistResponse> call, Throwable t) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Unable to fetch photos. Please try again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void copyTestImageToSdCard(final File testImageOnSdCard) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = getActivity().getAssets().open(TEST_FILE_NAME);
                    FileOutputStream fos = new FileOutputStream(testImageOnSdCard);
                    byte[] buffer = new byte[8192];
                    int read;
                    try {
                        while ((read = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                    } finally {
                        fos.flush();
                        fos.close();
                        is.close();
                    }
                } catch (IOException e) {
                    L.w("Can't copy test image onto SD card");
                }
            }
        }).start();
    }

    private static class ImageAdapter extends BaseAdapter {

        private static final String[] IMAGE_URLS = Constants.IMAGES;

        private LayoutInflater inflater;

        private DisplayImageOptions options;
        private Context context;

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            this.context=context;
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.image);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, PhotoViewActivity.class);
                    intent.putExtra(POSITION,position);
                    ((Activity)context).startActivity(intent);
                }
            });

            ImageLoader.getInstance()
                    .displayImage(IMAGE_URLS[position], holder.imageView, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });

            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
}
