package com.ycf.handson.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ycf.handson.R;
import com.ycf.handson.fragment.FeedFragment;
import com.ycf.handson.manager.LikeStatusManager;
import com.ycf.handson.manager.MediaPreloadManager;
import com.ycf.handson.model.Author;
import com.ycf.handson.model.Clip;
import com.ycf.handson.model.Post;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PostViewHolder> {
    private final Context context;

    @Getter
    private final List<Post> postList;

    private final LikeStatusManager likeStatusManager;


    private final FeedFragment parentFragment;


    private static final String TAG = "FeedAdapter";


    public FeedAdapter(Context context, MediaPreloadManager mediaPreloadManager, FeedFragment feedFragment) {

        this.context = context;
        this.postList = new ArrayList<>();
        this.likeStatusManager = new LikeStatusManager(context);
        this.parentFragment = feedFragment;
    }

    /**
     *
     * 当 RecyclerView 需要一个新的列表项视图时，会调用这个方法来创建它。
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return 一个view holder用于绑定数据
     */
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载成view 并且不要直接附在recycle layout(因为他自己会处理view )
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_card, parent, false);
        return new PostViewHolder(view);
    }

    /**
     * 用于将数据绑定到item view上
     *
     * @param holder   一个复用的 ViewHolder 实例
     * @param position 当前 Item 在数据列表中的索引位置
     */
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        // 从pref中获取isLiked
        post.setLiked(likeStatusManager.isLiked(post.getPostId()));
        holder.bind(post);

    }

    /**
     * 告诉 RecyclerView 数据列表中总共有多少个 Item。
     *
     * @return 数据列表中Item数目
     */
    @Override
    public int getItemCount() {
        return postList.size();
    }


    /**
     * 首次加载或下拉刷新时调用
     */


    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }

    /**
     * 上拉加载更多时调用
     */
    public void appendData(List<Post> morePosts) {
        if (morePosts != null && !morePosts.isEmpty()) {
            int startPosition = postList.size();
            postList.addAll(morePosts);
            notifyItemRangeInserted(startPosition, morePosts.size());
        }
    }

    private void startDetailActivity(Post post, int position) {

        parentFragment.startDetailActivityForUpdate(post, position);
    }

    public void updatePostLike(int position, int newLikeCount) {

        Post post = postList.get(position);
        post.setLike_count(newLikeCount);
        notifyItemChanged(position);

    }

    // ViewHolder是干嘛的 ？ 缓存每个列表项视图（item_post_card.xml）
    // 中的所有子控件（TextView, ImageView），避免每次滚动时都去调用耗费资源的 findViewById。
    // 说人话就是 view holder可以复用(当我刷走的时候 )
    public class PostViewHolder extends RecyclerView.ViewHolder {

        // 媒体内容
        ImageView ivCover;
        TextView tvTitle;

        TextView tvAuthorNickname; // 昵称

        ImageView ivAuthorAvatar;  // 作者头像
        TextView tvLikeCount;      // 点赞数量

        ImageView ivLikeIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // 绑定媒体和标题
            ivCover = itemView.findViewById(R.id.post_media_image);
            tvTitle = itemView.findViewById(R.id.post_title);

            // 绑定作者信息和点赞
            tvAuthorNickname = itemView.findViewById(R.id.author_nickname);
            ivAuthorAvatar = itemView.findViewById(R.id.author_avatar);
            tvLikeCount = itemView.findViewById(R.id.like_count);
            ivLikeIcon = itemView.findViewById(R.id.like_icon);


            // 设置点击跳转事件
            itemView.setOnClickListener(v -> {

                int curPosition = getAbsoluteAdapterPosition();
                if (curPosition != RecyclerView.NO_POSITION) {
                    Post clickedPost = postList.get(curPosition);
                    FeedAdapter.this.startDetailActivity(clickedPost, curPosition);

                }

            });
        }

        public void bind(Post post) {

            tvTitle.setText(post.getTitle());

            if (post.getAuthor() != null) {
                Author author = post.getAuthor();
                tvAuthorNickname.setText(author.getNickname());
                tvLikeCount.setText(String.valueOf(post.getLike_count()));
                Glide.with(itemView.getContext())
                        .load(author.getAvatar())
                        .into(ivAuthorAvatar);

            }

            int defaultHeight = (int) (((itemView.getContext().getResources().getDisplayMetrics().widthPixels / 2) - 20) / (4.0f / 3.0f));
            int viewHeight = defaultHeight;


            DisplayMetrics dm = itemView.getContext().getResources().getDisplayMetrics();

            int targetWidth = (dm.widthPixels / 2) - 20;


            final float MIN_RATIO = 3.0f / 4.0f;
            final float MAX_RATIO = 4.0f / 3.0f;


            if (post.getClips() != null && !post.getClips().isEmpty()) {
                Clip first = post.getClips().get(0);

                String coverUrl = first.getUrl();
                int originalWidth = first.getWidth();
                int originalHeight = first.getHeight();

                Log.d(TAG, first.toString());

                if (originalWidth > 0 && originalHeight > 0) {

                    float originalRatio = (float) originalWidth / originalHeight;
                    float finalRatio;
                    if (originalRatio < MIN_RATIO) {
                        finalRatio = MIN_RATIO;
                    } else if (originalRatio > MAX_RATIO) {
                        finalRatio = MAX_RATIO;
                    } else {
                        finalRatio = originalRatio;
                    }

                    viewHeight = (int) (targetWidth / finalRatio);

                    ViewGroup.LayoutParams params = ivCover.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = viewHeight;
                    ivCover.setLayoutParams(params);

                    Glide.with(itemView.getContext())
                            .load(coverUrl)
                            .centerCrop()
                            .error(R.drawable.error_image)
                            .into(ivCover);

                } else {
                    Log.d(TAG, "Clip data invalid or empty. Showing error image with default ratio.");
                    viewHeight = (int) (targetWidth / MAX_RATIO);

                    ViewGroup.LayoutParams params = ivCover.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = viewHeight;
                    ivCover.setLayoutParams(params);
                    ivCover.setImageResource(R.drawable.error_image);
                    ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            } else {
                Log.d(TAG, "No clips data. Showing error image with default ratio.");
                viewHeight = (int) (targetWidth / MAX_RATIO);
                ViewGroup.LayoutParams params = ivCover.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = viewHeight;
                ivCover.setLayoutParams(params);

                ivCover.setImageResource(R.drawable.error_image);
                ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            updateLikeUI(post.isLiked(), post.getLike_count());
            ivLikeIcon.setOnClickListener(v -> {

                boolean newLiked = !post.isLiked();
                likeStatusManager.toggleLike(post.getPostId(), newLiked);
                int newCount = post.getLike_count() + ((newLiked) ? 1 : -1);
                post.setLike_count(newCount);
                post.setLiked(newLiked);
                updateLikeUI(newLiked, newCount);

            });


        }

        public void updateLikeUI(boolean isLiked, int count) {
            int heartIcon = isLiked ? R.drawable.ic_heart_liked
                    : R.drawable.ic_heart_unliked;


            ivLikeIcon.setImageResource(heartIcon);
            tvLikeCount.setText(String.valueOf(count));

        }
    }

}
