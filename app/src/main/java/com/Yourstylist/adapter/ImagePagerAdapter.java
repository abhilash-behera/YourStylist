package com.Yourstylist.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.Yourstylist.Constants;
import com.Yourstylist.R;
import com.Yourstylist.Utils;
import com.Yourstylist.activity.PhotoViewActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ImagePagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    private DisplayImageOptions options;
    private boolean systemUiHidden=false;

    public ImagePagerAdapter(Context context){
        this.context=context;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        return Constants.IMAGES.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View itemView=inflater.inflate(R.layout.image_pager_item,container,false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.imageView);
        ImageLoader.getInstance().displayImage(Constants.IMAGES[position],imageView,options);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(systemUiHidden){
                    Utils.showSystemUI(context);
                    if(context instanceof PhotoViewActivity){
                        ((PhotoViewActivity)context).showLayouts();
                    }
                    systemUiHidden=false;
                }else {

                    Utils.hideSystemUI(context);
                    if(context instanceof PhotoViewActivity){
                        ((PhotoViewActivity)context).hideLayouts();
                    }
                    systemUiHidden=true;
                }
            }
        });
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
