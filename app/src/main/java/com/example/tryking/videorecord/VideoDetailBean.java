package com.example.tryking.videorecord;

import android.graphics.Bitmap;

/**
 * Created by Tryking on 2017/2/7.
 */

public class VideoDetailBean {
    private String name;
    private String duration;
    private String size;
    private String time;
    private Bitmap preview;

    public VideoDetailBean(String name, String duration, String size, String time, Bitmap preview) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.time = time;
        this.preview = preview;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public void setPreview(Bitmap preview) {
        this.preview = preview;
    }

    @Override
    public String toString() {
        return "VideoDetailBean{" +
                "name='" + name + '\'' +
                ", duration='" + duration + '\'' +
                ", size='" + size + '\'' +
                ", time='" + time + '\'' +
                ", preview=" + preview +
                '}';
    }
}
