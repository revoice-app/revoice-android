package com.sar.user.smart_city;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.List;

public class LearnDragAdapter extends RecyclerView.Adapter<LearnDragAdapter.ImageHolder> {


    private List<UploadImageData> mFilesList;
    private Context context;
    private DeletePostImageListener deletePostImageListener;
    private AddImageListener addImageListener;
    private final int viewImage = 1;
    private final int viewVideo = 0;


    public LearnDragAdapter(Context context, List<UploadImageData> mFilesList, DeletePostImageListener deletePostImageListener, AddImageListener addImageListener) {
        this.mFilesList = mFilesList;
        this.context = context;
        this.deletePostImageListener = deletePostImageListener;
        this.addImageListener = addImageListener;
    }


    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == viewImage) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.image_grid_view, parent, false);
            return new ImageHolder(v);
        } else {
            //call view for video
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.video_learn_grid, parent, false);
            return new ImageHolder(v, true);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, final int position) {

        final UploadImageData imageData = mFilesList.get(position);

            Glide.with(context).load(imageData.getMediaFile().getUri()).into(holder.ivImage);
            holder.imgClose.setVisibility(View.INVISIBLE);


    }

    @Override
    public int getItemViewType(int position) {

        if (position == (mFilesList.size())) {
            return viewImage;
        }
        if (mFilesList.get(position).getMediaFile().getMediaType() == MediaFile.TYPE_VIDEO)
            return viewVideo;
        return viewImage;
    }

    @Override
    public int getItemCount() {
        return mFilesList.size();
    }


    class ImageHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView ivImage;
        private AppCompatImageView imgClose;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            imgClose = itemView.findViewById(R.id.img_close);
        }

        public ImageHolder(@NonNull View itemView, boolean t) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            imgClose = itemView.findViewById(R.id.img_close);
        }


    }
}
