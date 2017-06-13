package com.Yourstylist.activity;

import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Yourstylist.Constants;
import com.Yourstylist.R;
import com.Yourstylist.adapter.ImagePagerAdapter;
import com.Yourstylist.fragment.GalleryFragment;
import com.Yourstylist.retrofit.Photo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import static android.view.View.GONE;

public class PhotoViewActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TextView txtType;
    private TextView txtColor;
    private LinearLayout topLayout;
    private LinearLayout bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        initializeViews();
    }

    private void initializeViews() {
        txtType=(TextView)findViewById(R.id.txtType);
        txtColor=(TextView)findViewById(R.id.txtColor);
        topLayout=(LinearLayout)findViewById(R.id.linearLayout1);
        bottomLayout=(LinearLayout)findViewById(R.id.linearLayout2);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(new ImagePagerAdapter(PhotoViewActivity.this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Photo photo= Constants.photoList.get(position);
                txtType.setText(photo.getType());
                txtColor.setText(photo.getColor());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(getIntent().getIntExtra(GalleryFragment.POSITION,0));
    }

    public void hideLayouts(){
        Animation animation=new AlphaAnimation(1.0f,0.0f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                topLayout.setVisibility(GONE);
                bottomLayout.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        topLayout.startAnimation(animation);
        bottomLayout.startAnimation(animation);
    }

    public void showLayouts(){
        Animation animation=new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                topLayout.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        topLayout.startAnimation(animation);
        bottomLayout.startAnimation(animation);
    }
}
