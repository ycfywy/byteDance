package com.ycf.handson.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ycf.handson.R;
import com.ycf.handson.model.Clip;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {


    private final Context context;
    private final List<Clip> clips;


    private static final String TAG = "GALLERY";


    public GalleryAdapter(Context context) {
        this.clips = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_clip, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        Log.d(TAG, clips.toString());
        Clip clip = clips.get(position);
        if (clip != null) holder.bind(clip);
    }

    @Override
    public int getItemCount() {
        return clips.size();
    }

    public void appenData(List<Clip> newClips) {
        if (newClips != null && !newClips.isEmpty()) {
            int startPosition = newClips.size();
            clips.addAll(newClips);
            notifyItemRangeChanged(startPosition, newClips.size());
        }
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_clip_image);
        }

        public void bind(Clip clip) {
            int type = clip.getType();

            if (type == 0) {

                Glide.with(context)
                        .load(clip.getUrl())
                        .centerCrop()
                        .placeholder(R.drawable.loading_image)
                        .error(R.drawable.error_image)
                        .into(imageView);

            }

        }
    }
}
