package com.Yourstylist;

import com.Yourstylist.retrofit.Photo;

import java.util.List;

public class Constants {
    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }

    public static String[] IMAGES=new String[]{};

    public static class Extra {
        public static final String FRAGMENT_INDEX = "com.nostra13.example.universalimageloader.FRAGMENT_INDEX";
        public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
    }

    public static List<Photo> photoList;
}
