package com.postermaker.flyerdesigner.creator.custom_adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper.SnapListener;

import java.util.ArrayList;
import java.util.List;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.Poster_SelectBGIMGActivity;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_OnClickCallback;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Item_Click_Listener;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Snap_Info;

public class Poster_Vertical_Stickers_Adapter extends Adapter<RecyclerView.ViewHolder> implements SnapListener {
    private Activity context;
    private int flagForActivity;
    private RecyclerView mRecyclerView;

    public ArrayList<Object> mSnaps;

    public class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(View view) {
            super(view);
        }
    }

    public Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String> mSingleCallback;


    public static class VerticalStickerViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView;
        public TextView seeMoreTextView;
        public TextView snapTextView;

        public VerticalStickerViewHolder(View view) {
            super(view);
            this.snapTextView = view.findViewById(R.id.snapTextView);
            this.seeMoreTextView = view.findViewById(R.id.seeMoreTextView);
            this.recyclerView = view.findViewById(R.id.recyclerView);
        }
    }

    public class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {
        UnifiedNativeAdViewHolder(View view) {
            super(view);
        }

    }

    public Poster_Vertical_Stickers_Adapter(Activity activity, ArrayList<Object> arrayList, RecyclerView recyclerView, int i) {
        this.mSnaps = arrayList;
        this.context = activity;
        this.flagForActivity = i;
        this.mRecyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int i) {
        if (this.mSnaps.get(i) == null) {
            return 0;
        }
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case 1:
                return new VerticalStickerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_efficient_snap_adapter, viewGroup, false));
            case 2:
                return new UnifiedNativeAdViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_custom_frame_layout, viewGroup, false));
            default:
                return new LoadingHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_cure_progress_view, viewGroup, false));
        }
    }

    public void insertData(List<Object> list) {
        notifyDataSetChanged();
    }

    public void showLoadingView() {
        new Handler().post(new Runnable() {
            public void run() {
                Poster_Vertical_Stickers_Adapter.this.mSnaps.add(null);
                Poster_Vertical_Stickers_Adapter verticalStickerAdapter = Poster_Vertical_Stickers_Adapter.this;
                verticalStickerAdapter.notifyItemInserted(verticalStickerAdapter.mSnaps.size() - 1);
            }
        });
    }

    public void hideLoadingView() {
        ArrayList arrayList = this.mSnaps;
        arrayList.remove(arrayList.size() - 1);
        notifyItemRemoved(this.mSnaps.size());
    }


    public void setItemClickCallback(Poster_OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch (getItemViewType(i)) {
            case 1:
                final VerticalStickerViewHolder verticalStickerViewHolder2 = (VerticalStickerViewHolder) viewHolder;
                final Poster_Snap_Info snapInfo = (Poster_Snap_Info) this.mSnaps.get(i);
                final String str = snapInfo.getText().toUpperCase().contains("WHITE") ? "white" : "colored";
                verticalStickerViewHolder2.snapTextView.setText(snapInfo.getText().replace("white", "").toUpperCase());
                verticalStickerViewHolder2.recyclerView.setOnFlingListener(null);
                if (snapInfo.getGravity() == GravityCompat.START || snapInfo.getGravity() == GravityCompat.END) {
                    verticalStickerViewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(verticalStickerViewHolder2.recyclerView.getContext(), 0, false));
                    new GravitySnapHelper(snapInfo.getGravity(), false, this).attachToRecyclerView(verticalStickerViewHolder2.recyclerView);
                } else if (snapInfo.getGravity() == 1 || snapInfo.getGravity() == 16) {
                    verticalStickerViewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(verticalStickerViewHolder2.recyclerView.getContext(), snapInfo.getGravity() == 1 ? 0 : 1, false));
                    new LinearSnapHelper().attachToRecyclerView(verticalStickerViewHolder2.recyclerView);
                } else if (snapInfo.getGravity() == 17) {
                    verticalStickerViewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(verticalStickerViewHolder2.recyclerView.getContext(), 0, false));
                    new GravityPagerSnapHelper(GravityCompat.START).attachToRecyclerView(verticalStickerViewHolder2.recyclerView);
                } else {
                    verticalStickerViewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(verticalStickerViewHolder2.recyclerView.getContext()));
                    new GravitySnapHelper(snapInfo.getGravity()).attachToRecyclerView(verticalStickerViewHolder2.recyclerView);
                }
                if (((Poster_Snap_Info) this.mSnaps.get(i)).getBG_Images().size() > 3) {
                    verticalStickerViewHolder2.seeMoreTextView.setVisibility(View.VISIBLE);
                } else {
                    verticalStickerViewHolder2.seeMoreTextView.setVisibility(View.GONE);
                }
                ArrayList arrayList = new ArrayList();
                if (snapInfo.getBG_Images().size() >= 6) {
                    for (int i2 = 0; i2 < 6; i2++) {
                        arrayList.add(snapInfo.getBG_Images().get(i2));
                    }
                } else {
                    arrayList = snapInfo.getBG_Images();
                }
                Context context;
                boolean z;
                if (this.flagForActivity == 1) {
                    context = this.context;
                    z = snapInfo.getGravity() == GravityCompat.START || snapInfo.getGravity() == GravityCompat.END || snapInfo.getGravity() == 1;
                    verticalStickerViewHolder2.recyclerView.setAdapter(new Poster_Adapter_Stiker(context, z, snapInfo.getGravity() == 17, arrayList, this.flagForActivity, str, new Poster_On_Item_Click_Listener() {
                        public void onItemClick(int i) {
                            verticalStickerViewHolder2.seeMoreTextView.performClick();
                        }
                    }));
                } else {
                    context = this.context;
                    z = snapInfo.getGravity() == GravityCompat.START || snapInfo.getGravity() == GravityCompat.END || snapInfo.getGravity() == 1;
                    Poster_Adapter_Stiker adapterStiker = new Poster_Adapter_Stiker(context, z, snapInfo.getGravity() == 17, arrayList, this.flagForActivity, str, new Poster_On_Item_Click_Listener() {
                        public void onItemClick(int i) {
                            verticalStickerViewHolder2.seeMoreTextView.performClick();
                        }
                    });
                    verticalStickerViewHolder2.recyclerView.setAdapter(adapterStiker);
                    adapterStiker.setItemClickCallback(new Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String>() {
                        public void onClickCallBack(ArrayList<String> arrayList, ArrayList<Poster_BG_Image> arrayList2, String str, Activity activity, String str2) {
                            Poster_Vertical_Stickers_Adapter.this.mSingleCallback.onClickCallBack(null, snapInfo.getBG_Images(), str, (FragmentActivity) Poster_Vertical_Stickers_Adapter.this.context, str2);
                        }
                    });
                }
                verticalStickerViewHolder2.seeMoreTextView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (Poster_Vertical_Stickers_Adapter.this.flagForActivity == 1) {
                            ((Poster_SelectBGIMGActivity) Poster_Vertical_Stickers_Adapter.this.context).itemClickSeeMoreAdapter(snapInfo.getBG_Images(), snapInfo.getText());
                        } else {
                            Poster_Vertical_Stickers_Adapter.this.mSingleCallback.onClickCallBack(null, snapInfo.getBG_Images(), "", (FragmentActivity) Poster_Vertical_Stickers_Adapter.this.context, str);
                        }
                    }
                });
                return;
            case 2:
                UnifiedNativeAdViewHolder unifiedNativeAdViewHolder = (UnifiedNativeAdViewHolder) viewHolder;
//                new NativeAdsHelper2().loadNativeAd(this.context, unifiedNativeAdViewHolder.getAdView(), unifiedNativeAdViewHolder.getLoadingView(), false);
                return;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return this.mSnaps.size();
    }

    public void onSnap(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        stringBuilder.append("");
        Log.d("Snapped: ", stringBuilder.toString());
    }
}
