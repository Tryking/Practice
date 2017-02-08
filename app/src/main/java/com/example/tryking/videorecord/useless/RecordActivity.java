package com.example.tryking.videorecord.useless;

import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tryking.videorecord.R;

import java.io.File;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView svView;
    private Button btStart;
    private Button btStop;
    private File recordFile;
    private MediaRecorder mRecorder;
    private boolean isRecording;
    private String dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initViews();
        init();
    }

    private void initViews() {
        svView = (SurfaceView) this.findViewById(R.id.sv_view);
        btStart = (Button) this.findViewById(R.id.start);
        btStop = (Button) this.findViewById(R.id.stop);
        btStart.setOnClickListener(this);
        btStop.setOnClickListener(this);
        btStop.setEnabled(false);
        isRecording = false;
    }

    private void init() {
        //Surface不需要自己维护缓冲区。this is ignored, this value is set automatically when needed.
        svView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //分辨率
        svView.getHolder().setFixedSize(320, 280);
        //保持屏幕不关闭
        svView.getHolder().setKeepScreenOn(true);

        //初始化文件目录
        dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name);
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
//                Toast.makeText(RecordActivity.this, "开始录制", Toast.LENGTH_SHORT).show();
                try {
                    recordFile = new File(dir, System.currentTimeMillis() + ".mp4");
                    mRecorder = new MediaRecorder();
                    mRecorder.reset();
                    //从麦克风采集声音
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    //从摄像头采集图像
                    mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    //在设置声音编码格式、图像编码格式前设置文件的输出格式（必须）
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    //声音编码格式
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    //图像编码格式
                    mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                    mRecorder.setVideoSize(320, 280);
                    //每秒15帧
                    mRecorder.setVideoFrameRate(15);
                    mRecorder.setOutputFile(recordFile.getAbsolutePath());
                    //预览视频
                    mRecorder.setPreviewDisplay(svView.getHolder().getSurface());
                    mRecorder.prepare();

                    //开始录制
                    mRecorder.start();
                    isRecording = true;
                    btStart.setEnabled(false);
                    btStop.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop:
                if (isRecording) {
                    Toast.makeText(RecordActivity.this, "结束录制", Toast.LENGTH_SHORT).show();
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    btStop.setEnabled(false);
                    btStart.setEnabled(true);
                }
                break;
        }
    }
}
