package com.example.tryking.practice;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public class VideoDetailActivity extends Activity {

    private RecyclerView rvShow;
    private TextView tvNothing;
    private Toolbar toolbar;
    private MyDatabaseHelper myDbHelper;
    private SQLiteDatabase db;
    private ArrayList<VideoDetailBean> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        init();
        initViews();
    }

    private void init() {
        data = new ArrayList<>();
        //加载数据库
        myDbHelper = new MyDatabaseHelper(this, this.getResources().getString(R.string.app_name)
                + ".db", null, 1);
        db = myDbHelper.getWritableDatabase();
        Cursor cursor = db.query("Video", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                Logger.e("name:" + name + "||time:" + time + "|||duration:" + duration +
                        "|||size:" + size);
                Bitmap bitmap = Utils.getVideoThumbnail(name, 100, 100, MediaStore.Video
                        .Thumbnails.MINI_KIND);
                VideoDetailBean videoDetail = new VideoDetailBean(name, Utils.formatTimeByMss(Long
                        .parseLong(duration)), Utils.formatFileSize(Long.parseLong(size)), time,
                        bitmap);
                data.add(videoDetail);
                Long.parseLong(duration);
            } while (cursor.moveToNext());
        }
    }

    private void initViews() {
        rvShow = (RecyclerView) this.findViewById(R.id.rv_show);
        tvNothing = (TextView) this.findViewById(R.id.tv_nothing);
        toolbar = (Toolbar) this.findViewById(R.id.toolBar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        initToolBar();

        if (data.size() == 0) {
            tvNothing.setVisibility(View.VISIBLE);
            rvShow.setVisibility(View.GONE);
        } else {
            rvShow.setVisibility(View.VISIBLE);
            tvNothing.setVisibility(View.GONE);
        }
        rvShow.setLayoutManager(new GridLayoutManager(this, 1));
        rvShow.addItemDecoration(new DividerGridItemDecoration(this));//条目分割线
        rvShow.setAdapter(new VideoDetailAdapter(this, data));
    }

    //ToolBar设置
    private void initToolBar() {
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor("#FF4081"));
    }
}
