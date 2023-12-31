package com.postermaker.flyerdesigner.creator.custom_adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.imageloader.Poster_Custom_Glide_IMG_Loader;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Full_Poster_Thumb;
import com.southernbox.parallaxrecyclerview.ParallaxRecyclerView;

public class Poster_View_More_Adapter extends Adapter<ViewHolder> {

    public Activity context;

    private ArrayList<Object> posterDatas;
    private Poster_AppPreferenceClass appPreferenceClass;

    ParallaxRecyclerView recyclerView;
    int catID;
    String ratio;

    public class LoadingHolder extends ViewHolder {
        public LoadingHolder(View view) {
            super(view);
        }
    }


    public class UnifiedNativeAdViewHolder extends ViewHolder {

        UnifiedNativeAdViewHolder(View view) {
            super(view);
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public Poster_View_More_Adapter(int i, ArrayList<Object> arrayList, String str, Activity activity, ParallaxRecyclerView recyclerView) {
        this.posterDatas = arrayList;
        this.context = activity;
        this.catID = i;
        this.ratio = str;
        this.appPreferenceClass = new Poster_AppPreferenceClass(activity);
        this.recyclerView = recyclerView;
    }


    public static class MorePosterViewHolder extends ViewHolder {
        CardView cardView;
        ImageView ivImage;
        ImageView ivLock;
        ImageView ivRateUs;
        ProgressBar mProgressBar;

        public MorePosterViewHolder(View view) {
            super(view);
            this.ivImage = view.findViewById(R.id.iv_image);
            this.ivRateUs = view.findViewById(R.id.iv_rate_us);
            this.ivLock = view.findViewById(R.id.iv_lock);
            this.cardView = view.findViewById(R.id.cv_image);
            this.mProgressBar = view.findViewById(R.id.progressBar1);
        }
    }

    public void insertLoadingView() {
        new Handler().post(new Runnable() {
            public void run() {
                Poster_View_More_Adapter.this.posterDatas.add(null);
                Poster_View_More_Adapter viewMorePostersAdapter = Poster_View_More_Adapter.this;
                viewMorePostersAdapter.notifyItemInserted(viewMorePostersAdapter.posterDatas.size() - 1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return new LoadingHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_cure_progress_view, viewGroup, false));
        }
        if (i == 1) {
            return new MorePosterViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_list_item_more_posters, viewGroup, false));
        }
        return i == 2 ? new UnifiedNativeAdViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_custom_frame_layout, viewGroup, false)) : null;
    }

    @Override
    public int getItemCount() {
        return this.posterDatas.size();
    }

    @Override
    public int getItemViewType(int i) {
        if (this.posterDatas.get(i) == null) {
            return 0;
        }
        return 1;
    }

    public void insertData(List<Object> list) {
        notifyDataSetChanged();
    }

    public void removeLoadingView() {
        ArrayList arrayList = posterDatas;
        arrayList.remove(arrayList.size() - 1);
        notifyItemRemoved(posterDatas.size());
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        switch (getItemViewType(i)) {
            case 1:
                MorePosterViewHolder morePosterViewHolder = (MorePosterViewHolder) viewHolder;
                final Poster_Full_Poster_Thumb fullPosterThumb = (Poster_Full_Poster_Thumb) this.posterDatas.get(i);
                new Poster_Custom_Glide_IMG_Loader(morePosterViewHolder.ivImage, morePosterViewHolder.mProgressBar).loadImgFromUrl(fullPosterThumb.getPost_thumb(), new RequestOptions().fitCenter().priority(Priority.HIGH));
                if (i <= 4 || this.appPreferenceClass.getInt(Poster_AppConstants.isRated, 0) != 0) {
                    morePosterViewHolder.ivLock.setVisibility(View.GONE);
                } else {
                    morePosterViewHolder.ivLock.setVisibility(View.GONE);
                }
                morePosterViewHolder.cardView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                    }
                });
                return;
            case 2:
                return;
            default:
        }
    }

}
