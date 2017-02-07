package com.example.tryking.practice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tryking on 2017/2/7.
 */

public class VideoDetailAdapter extends RecyclerView.Adapter<VideoDetailAdapter
        .VideoDetailViewHolder> {
    private Context mContext;
    private List<VideoDetailBean> mData;

    public VideoDetailAdapter(Context context, List<VideoDetailBean> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public VideoDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VideoDetailViewHolder holder = new VideoDetailViewHolder(LayoutInflater.from
                (mContext).inflate(R.layout.item_video_detail, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoDetailViewHolder holder, final int position) {
        holder.ivPreview.setImageBitmap(mData.get(position).getPreview());
        holder.tvName.setText("视频名称：" + Utils.getFileNameByPath(mData.get(position).getName()));
        holder.tvSize.setText("文件大小：" + mData.get(position).getSize());
        holder.tvTime.setText("录制时间：" + mData.get(position).getTime());
        holder.tvDuration.setText("视频时长：" + mData.get(position).getDuration());
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + mData.get(position).getName()),
                        "video/mp4");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class VideoDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPreview;
        TextView tvName;
        TextView tvDuration;
        TextView tvTime;
        TextView tvSize;
        ImageView ivPlay;

        public VideoDetailViewHolder(View itemView) {
            super(itemView);
            ivPreview = (ImageView) itemView.findViewById(R.id.iv_preview);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvSize = (TextView) itemView.findViewById(R.id.tv_size);
            ivPlay = (ImageView) itemView.findViewById(R.id.iv_play);
        }
    }
}
