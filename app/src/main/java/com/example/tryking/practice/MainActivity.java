package com.example.tryking.practice;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 1;
    private String dir;
    private File recordFile;

    private Button record;
    private Button view;
    private MyDatabaseHelper myDbHelper;
    private SQLiteDatabase db;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        init();
    }

    private void initViews() {
        record = (Button) this.findViewById(R.id.record);
        view = (Button) this.findViewById(R.id.view);
        image = (ImageView) this.findViewById(R.id.image);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "录制", Toast.LENGTH_SHORT).show();
                //这个类里用的是Android自带的MediaRecorder，兼容性不好
//                Intent record = new Intent(MainActivity.this, RecordActivity.class);
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
                startActivityForResult(intent, VIDEO_CAPTURE);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "查看文件", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        //初始化文件目录
        dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R
                .string.app_name);
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }

        //初始化数据库
        myDbHelper = new MyDatabaseHelper(MainActivity.this, this.getResources().getString(R
                .string.app_name) + ".db", null, 1);
        db = myDbHelper.getWritableDatabase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode != RESULT_OK) {
                finish();
                return;
            }
            try {
                AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor
                        (data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                recordFile = new File(dir, System.currentTimeMillis() + ".mp4");

                FileOutputStream fos = null;
                fos = new FileOutputStream(recordFile);
                byte[] buf = new byte[1024];
                int len;
                long allLen = 0;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                    allLen += len;
                }
                Log.d("文件总长：", allLen + "");
                fis.close();
                fos.close();
                Toast.makeText(MainActivity.this, "录制成功，文件名：" + recordFile.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
                //删除原来的视频文件
                deleteOriginalFile(data.getData());
                //添加到数据库
                // TODO: 2017/2/7  录制时间，时长，大小，名字
                addToDatabase(recordFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteOriginalFile(Uri uri) {
        String file = null;
        if (uri != null) {
            if (uri.getScheme().toString().equals("content")) {
                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    file = cursor.getString(columnIndex);
                    //缩略图的id
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns
                            ._ID));
                    //视频的时长
                    int time = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media
                            .DURATION));
                    Log.d("持续时长：", time + "");
                    //缩略图
                    Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver
                            (), id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
//                    if (!file.startsWith("/mnt")) {
//                        file = "/mnt/" + file;
//                    }
                    Log.d("fileName", file);
                    image.setImageBitmap(bitmap);
                }
            }
        }
        File f = new File(file);
        if (f.exists()) {
            f.delete();
            Log.d("delete", "删除成功");
        }
    }

    private void addToDatabase(File recordFile) {

    }
}
