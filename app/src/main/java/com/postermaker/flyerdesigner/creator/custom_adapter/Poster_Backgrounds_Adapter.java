package com.postermaker.flyerdesigner.creator.custom_adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.Poster_IntegerVersionSignature;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_OnClickCallback;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;

public class Poster_Backgrounds_Adapter extends Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> {

    private static final String TAG = "Backgrounds_Adapter";

    ArrayList<Poster_BG_Image> category_list;
    Activity context;
    boolean isDownloadProgress = true;

    public Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String> mSingleCallback;

    public class TempLoadingHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TempLoadingHolder(View view) {
            super(view);
        }
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    public Poster_Backgrounds_Adapter(Activity activity, ArrayList<Poster_BG_Image> arrayList) {
        this.context = activity;
        this.category_list = arrayList;
    }

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        ProgressBar downloadProgress;
        ImageView imageView;
        RelativeLayout imgDownload;
        LinearLayout layout;

        public ViewHolder(View view) {
            super(view);
            this.imgDownload = (RelativeLayout) view.findViewById(R.id.imgDownload);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail_image);
            this.layout = (LinearLayout) view.findViewById(R.id.main);
            this.downloadProgress = (ProgressBar) view.findViewById(R.id.downloadProgress);
        }
    }

    @Override
    public int getItemCount() {
        ArrayList arrayList = this.category_list;
        return arrayList == null ? 0 : arrayList.size();
    }

    @Override
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_row_item_iv_bg, viewGroup, false));
        }
        return new TempLoadingHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_cure_progress_view, viewGroup, false));
    }

    @Override
    public int getItemViewType(int i) {
        return this.category_list.get(i) == null ? 1 : 0;
    }

    public void insertdata(List<Poster_BG_Image> list) {
        notifyDataSetChanged();
    }

    public void hideLoadingView() {
        ArrayList arrayList = this.category_list;
        arrayList.remove(arrayList.size() - 1);
        notifyItemRemoved(this.category_list.size());
    }

    public void showLoadingView() {
        new Handler().post(new Runnable() {
            public void run() {
                Poster_Backgrounds_Adapter.this.category_list.add(null);
                Poster_Backgrounds_Adapter backgroundsAdapter = Poster_Backgrounds_Adapter.this;
                backgroundsAdapter.notifyItemInserted(backgroundsAdapter.category_list.size() - 1);
            }
        });
    }

    @Override
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == 0) {
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            viewHolder2.downloadProgress.setVisibility(View.GONE);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Poster_AppConstants.BASE_URL_BG);
            stringBuilder.append("/");
            stringBuilder.append(((Poster_BG_Image) this.category_list.get(i)).getBGImage_url());


            final String stringBuilder2 = stringBuilder.toString();
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(Poster_AppConstants.BASE_URL_BG);
            stringBuilder3.append("/");
            stringBuilder3.append(((Poster_BG_Image) this.category_list.get(i)).getThumb_url());
            String stringBuilder4 = stringBuilder3.toString();
            viewHolder2.imgDownload.setVisibility(View.GONE);
            Glide.with(this.context).load(stringBuilder4).thumbnail(0.1f).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new Poster_IntegerVersionSignature(Poster_AppConstants.getAPPVersionInfo())).dontAnimate().override(200, 200).fitCenter().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).into(viewHolder2.imageView);
            viewHolder2.layout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    try {
                        Poster_Backgrounds_Adapter.this.mSingleCallback.onClickCallBack(null, Poster_Backgrounds_Adapter.this.category_list, stringBuilder2, Poster_Backgrounds_Adapter.this.context, "");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setItemClickCallback(Poster_OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }
}
