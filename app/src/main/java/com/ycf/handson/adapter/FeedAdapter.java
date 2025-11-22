package com.ycf.handson.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ycf.handson.R;
import com.ycf.handson.manager.LikeStatusManager;
import com.ycf.handson.model.Author;
import com.ycf.handson.model.Clip;
import com.ycf.handson.model.Post;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PostViewHolder> {
    private final Context context;
    private final List<Post> postList;

    private final LikeStatusManager likeStatusManager;

    public FeedAdapter(Context context) {
        this.context = context;
        this.postList = new ArrayList<>();
        this.likeStatusManager = new LikeStatusManager(context);
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
        post.setLiked(likeStatusManager.isLiked(post.getPost_id()));
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

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // 绑定媒体和标题
            ivCover = itemView.findViewById(R.id.post_media_image);
            tvTitle = itemView.findViewById(R.id.post_title);

            // 绑定作者信息和点赞 (ID 必须与 item_post_card.xml 中定义的 ID 匹配)
            tvAuthorNickname = itemView.findViewById(R.id.author_nickname); // 原来的昵称TextView
            ivAuthorAvatar = itemView.findViewById(R.id.author_avatar);     // 新增的头像ImageView
            tvLikeCount = itemView.findViewById(R.id.like_count);           // 新增的点赞数量TextView
        }

        public void bind(Post post) {

            // 1. 绑定title
            tvTitle.setText(post.getTitle());

            // 2. 绑定作者信息和点赞数
            if (post.getAuthor() != null) {
                Author author = post.getAuthor();
                tvAuthorNickname.setText(author.getNickname());
                // 似乎没有点赞数目 这里random一下
                tvLikeCount.setText(String.valueOf(post.getLike_count()));

                // 加载头像 (使用 Glide)
                Glide.with(itemView.getContext())
                        .load(author.getAvatar())
                        .into(ivAuthorAvatar);

            }
            // 3. 处理item中的主图 clip
            if (post.getClips() != null && !post.getClips().isEmpty()) {
                Clip first = post.getClips().get(0);
                String coverUrl = first.getUrl();
                int width = first.getWidth();
                int height = first.getHeight();
                // TODO type先不管

                if (width > 0 && height > 0) {

                    // 获取当前屏幕的信息
                    DisplayMetrics dm = itemView.getContext().getResources().getDisplayMetrics();
                    // 保持纵横比
                    int itemWidth = (dm.widthPixels / 2) - 20;
                    int viewHeight = (int) ((float) height / width * itemWidth);
                    // 设置 ImageView
                    // TODO 不懂
                    ViewGroup.LayoutParams params = ivCover.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = viewHeight;
                    ivCover.setLayoutParams(params);
                }
                Glide.with(itemView.getContext())
                        .load(coverUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                        .into(ivCover);
            } else {
                // TODO 这里设置为不可见
                ivCover.setVisibility(View.GONE);
            }

            // 4. 新增：处理点赞的逻辑(点赞的heart图片放在 text view里面 不是很懂)
            updateLikeUI(post.isLiked(), post.getLike_count());

            tvLikeCount.setOnClickListener(v -> {

                boolean newLiked = !post.isLiked();
                likeStatusManager.toggleLike(post.getPost_id(), newLiked);
                int newCount = post.getLike_count() + ((newLiked) ? 1 : -1);
                post.setLike_count(newCount);
                post.setLiked(newLiked);
                updateLikeUI(newLiked, newCount);

            });


        }

        public void updateLikeUI(boolean isLiked, int count) {
            Drawable heartIcon = isLiked ? ContextCompat.getDrawable(
                    itemView.getContext(), R.drawable.ic_heart_liked
            ) : ContextCompat.getDrawable(
                    itemView.getContext(), R.drawable.ic_heart_unliked
            );

            if (heartIcon != null) {
                tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                        heartIcon, null, null, null
                );
            }
            tvLikeCount.setText(String.valueOf(count));

        }
    }

}
