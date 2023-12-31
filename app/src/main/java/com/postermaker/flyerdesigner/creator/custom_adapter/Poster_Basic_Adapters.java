package com.postermaker.flyerdesigner.creator.custom_adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.Poster_SelectBGIMGActivity;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_OnClickCallback;
import com.postermaker.flyerdesigner.creator.imageloader.Poster_Quick_Glide_IMG_Loader1;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Item_Click_Listener;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;

public class Poster_Basic_Adapters extends Adapter<Poster_Basic_Adapters.ViewHolder> {

    private ArrayList<Poster_BG_Image> BG_Images;

    public Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String> mSingleCallback;

    Context context;

    private boolean mHorizontal, mPager;
    private int index, flagForActivity;

    Poster_On_Item_Click_Listener listener;
    SharedPreferences preferences;

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {

        ImageView ivLock, imageView;
        ProgressBar mProgressBar;
        TextView ratingTextView, nameTextView;
        RelativeLayout rl_see_more;

        public void onClick(View view) {
        }

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.imageView = view.findViewById(R.id.imageView);
            this.ivLock = view.findViewById(R.id.iv_lock);
            this.nameTextView = view.findViewById(R.id.nameTextView);
            this.ratingTextView = view.findViewById(R.id.ratingTextView);
            this.mProgressBar = view.findViewById(R.id.progressBar1);
            this.rl_see_more = view.findViewById(R.id.rl_see_more);
        }
    }

    public Poster_Basic_Adapters(Context context, boolean z, boolean z2, ArrayList<Poster_BG_Image> arrayList, int i, int i2, Poster_On_Item_Click_Listener onItemClickListener) {
        this.mHorizontal = z;
        this.BG_Images = arrayList;
        this.mPager = z2;
        this.context = context;
        this.flagForActivity = i;
        this.index = i2;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.listener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (this.mPager) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_list_item_pager_adapter, viewGroup, false));
        }
        ViewHolder viewHolder;
        if (this.mHorizontal) {
            viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_customized_adapter, viewGroup, false));
        } else {
            viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_layout_vertical_adapter, viewGroup, false));
        }
        return viewHolder;
    }


    public void setItemClickCallback(Poster_OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }

    @Override
    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    @Override
    public int getItemCount() {
        return this.BG_Images.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        Poster_BG_Image BG_Image = (Poster_BG_Image) this.BG_Images.get(i);
        if (i > 4) {
            viewHolder.ivLock.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.INVISIBLE);
            viewHolder.rl_see_more.setVisibility(View.VISIBLE);
            viewHolder.rl_see_more.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (Poster_Basic_Adapters.this.listener != null) {
                        Poster_Basic_Adapters.this.listener.onItemClick(i);
                    }
                }
            });
            return;
        }
        RequestOptions priority;
        if (this.index > 1) {
            priority = new RequestOptions().priority(Priority.HIGH);
        } else {
            priority = new RequestOptions().priority(Priority.HIGH);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Poster_AppConstants.BASE_URL_BG);
        stringBuilder.append("/");
        stringBuilder.append((this.BG_Images.get(i)).getBGImage_url());
        final String stringBuilder2 = stringBuilder.toString();
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(Poster_AppConstants.BASE_URL_BG);
        stringBuilder3.append("/");
        stringBuilder3.append((this.BG_Images.get(i)).getThumb_url());
        new Poster_Quick_Glide_IMG_Loader1(viewHolder.imageView, viewHolder.mProgressBar).loadImageFromStr(stringBuilder3.toString(), priority);
        if (i <= 11 || this.preferences.getBoolean("isAdsDisabled", false)) {
            viewHolder.ivLock.setVisibility(View.GONE);
        } else {
            viewHolder.ivLock.setVisibility(View.GONE);
        }
        viewHolder.imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (Poster_Basic_Adapters.this.flagForActivity == 1) {
                    ((Poster_SelectBGIMGActivity) Poster_Basic_Adapters.this.context).performNextTask(stringBuilder2);
                } else {
                    Poster_Basic_Adapters.this.mSingleCallback.onClickCallBack(null, Poster_Basic_Adapters.this.BG_Images, stringBuilder2, (FragmentActivity) Poster_Basic_Adapters.this.context, "");
                }
            }
        });
    }

}
