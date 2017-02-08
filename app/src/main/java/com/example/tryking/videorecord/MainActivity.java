package com.example.tryking.videorecord;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final int VIDEO_CAPTURE = 1;//录制视频
    private static final int PLAY_VIDEO = 2;//播放视频
    private static final float VIDEO_QUALITY = 0.5f;//录制的视频质量
    private String dir;
    private File recordFile;

    private Button record;
    private Button view;
    private MyDatabaseHelper myDbHelper;
    private SQLiteDatabase db;
    private ImageView ivPreview;
    private String fileName;//新的存储的文件名
    private long fileSize;//录制视频的大小
    private ImageView ivPlay;
    private Toolbar toolbar;
    private TextView tvName;
    private ImageView ivEdit;
    private LinearLayout llNmae;
    private EditText modifyName;
    private String currentTime;
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        init();
    }

    private void initViews() {
        toolbar = (Toolbar) this.findViewById(R.id.toolBar);
        record = (Button) this.findViewById(R.id.record);
        view = (Button) this.findViewById(R.id.view);
        ivPreview = (ImageView) this.findViewById(R.id.iv_preview);
        ivPlay = (ImageView) this.findViewById(R.id.iv_play);
        tvName = (TextView) this.findViewById(R.id.tv_name);
        ivEdit = (ImageView) this.findViewById(R.id.iv_edit);
        llNmae = (LinearLayout) this.findViewById(R.id.ll_name);
        ivPlay.setOnClickListener(this);
        record.setOnClickListener(this);
        view.setOnClickListener(this);
        ivEdit.setOnClickListener(this);

        initToolBar();
    }

    //设置ToolBar
    private void initToolBar() {
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.parseColor("#FF4081"));
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
        Logger.e("Request:" + requestCode + "|||Result:" + resultCode);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode != RESULT_OK) {
                ivPlay.setVisibility(View.GONE);
                ivPreview.setVisibility(View.GONE);
                llNmae.setVisibility(View.GONE);
                return;
            }
            try {
                AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor
                        (data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                fileName = System.currentTimeMillis() + ".mp4";
                recordFile = new File(dir, fileName);

                FileOutputStream fos = null;
                fos = new FileOutputStream(recordFile);
                byte[] buf = new byte[1024];
                int len;
                fileSize = 0;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                    fileSize += len;
                }
                Log.d("文件总长：", fileSize + "");
                fis.close();
                fos.close();
                Logger.e("录制成功，文件名：" + recordFile.getAbsolutePath());
                //删除原来的视频文件并将新文件的信息保存到数据库
                manageOriginalFile(data.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void manageOriginalFile(Uri uri) {
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
                    Logger.e("持续时长：" + time);
                    //缩略图
                    Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver
                            (), id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                    ivPreview.setImageBitmap(bitmap);
                    ivPlay.setVisibility(View.VISIBLE);
                    ivPreview.setVisibility(View.VISIBLE);
                    llNmae.setVisibility(View.VISIBLE);
                    tvName.setText(Utils.getFileNameByPath(recordFile.getAbsolutePath()));

                    currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
                            Date(System.currentTimeMillis()));
                    //存储到数据库
                    ContentValues values = new ContentValues();
                    values.put("name", recordFile.getAbsolutePath());
                    values.put("time", currentTime);
                    values.put("duration", time);
                    values.put("size", fileSize);
                    db.insert("Video", null, values);
                }
            }
        }
        File f = new File(file);
        if (f.exists()) {
            f.delete();
            Logger.e("删除成功");
            //通知系统图库更新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" +
                    file)));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setDataAndType(Uri.parse("file://" + recordFile.getAbsolutePath()),
                        "video/mp4");
                startActivityForResult(intent1, PLAY_VIDEO);
                break;
            case R.id.record:
                //                Toast.makeText(MainActivity.this, "录制", Toast.LENGTH_SHORT)
                // .show();
                //这个类里用的是Android自带的MediaRecorder，兼容性不好
                // Intent record = new Intent(MainActivity.this, RecordActivity.class);
                Intent intent2 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, VIDEO_QUALITY);
                startActivityForResult(intent2, VIDEO_CAPTURE);
                break;
            case R.id.view:
//                Toast.makeText(MainActivity.this, "查看视频库", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(MainActivity.this, VideoDetailActivity.class);
                startActivity(intent3);
                break;
            case R.id.iv_edit://修改当前录制视频的文件名
                modifyName = (EditText) getLayoutInflater().inflate(R.layout.dialog_modify_name,
                        null);
                etName = (EditText) modifyName.findViewById(R.id.et_name);
                new AlertDialog.Builder(this)
                        .setTitle("修改文件名")
                        .setView(modifyName)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //修改文件的名字
                                String newName = recordFile.getAbsolutePath().replace
                                        (Utils.getFileNameByPath(recordFile.getAbsolutePath()),
                                                etName.getText().toString() + ".mp4");
                                Logger.e("就名字：" + recordFile.getAbsolutePath() + "|||新名字：" +
                                        newName);
                                recordFile.renameTo(new File(newName));
                                //修改数据库中的文件名，根据存储的时间，即录制时间
                                ContentValues values = new ContentValues();
                                values.put("name", newName);
                                db.update("Video", values, "time = ?", new String[]{currentTime});
                                tvName.setText(etName.getText().toString() + ".mp4");
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
                break;
        }
    }
}
