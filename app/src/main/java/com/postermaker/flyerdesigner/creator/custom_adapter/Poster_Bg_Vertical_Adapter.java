package com.postermaker.flyerdesigner.creator.custom_adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
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

public class Poster_Bg_Vertical_Adapter extends Adapter<RecyclerView.ViewHolder> implements SnapListener {

    private int flagForActivity;
    private ArrayList<Object> mSnaps;
    private RecyclerView recyclerView;

    public Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String> mSingleCallback;

    Activity context;

    public class LoadingHolder extends RecyclerView.ViewHolder {
        LoadingHolder(View view) {
            super(view);
        }
    }


    public static class BGVerticalViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView seeMoreTextView;
        TextView snapTextView;

        public BGVerticalViewHolder(View view) {
            super(view);
            this.snapTextView = view.findViewById(R.id.snapTextView);
            this.seeMoreTextView = view.findViewById(R.id.seeMoreTextView);
            this.recyclerView = view.findViewById(R.id.recyclerView);
        }
    }

    public void onSnap(int i) {
    }

    public Poster_Bg_Vertical_Adapter(Activity activity, ArrayList<Object> arrayList, RecyclerView recyclerView, int i) {
        this.mSnaps = arrayList;
        this.context = activity;
        this.flagForActivity = i;
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int i) {
        if (this.mSnaps.get(i) == null) {
            return 0;
        }
        return 1;
    }

    public void InsertData(List<Object> list) {
        notifyDataSetChanged();
    }

    public void insertLoadingView() {
        new Handler().post(new Runnable() {
            public void run() {
                mSnaps.add(null);
                Poster_Bg_Vertical_Adapter bgVerticalAdapter = Poster_Bg_Vertical_Adapter.this;
                notifyItemInserted(bgVerticalAdapter.mSnaps.size() - 1);
            }
        });
    }

    public class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {

        UnifiedNativeAdViewHolder(View view) {
            super(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int i2 = i;
        switch (getItemViewType(i2)) {
            case 1:
                ArrayList arrayList;
                final BGVerticalViewHolder BGVerticalViewHolder2 = (BGVerticalViewHolder) viewHolder;
                final Poster_Snap_Info snapInfo = (Poster_Snap_Info) this.mSnaps.get(i2);
                boolean contains = snapInfo.getText().toUpperCase().contains("WHITE");
                BGVerticalViewHolder2.snapTextView.setText(snapInfo.getText().replace("white", "").toUpperCase());
                BGVerticalViewHolder2.recyclerView.setOnFlingListener(null);
                boolean z = true;
                if (snapInfo.getGravity() == GravityCompat.START || snapInfo.getGravity() == GravityCompat.END) {
                    BGVerticalViewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(BGVerticalViewHolder2.recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
                    new GravitySnapHelper(snapInfo.getGravity()).attachToRecyclerView(BGVerticalViewHolder2.recyclerView);
                } else if (snapInfo.getGravity() == 1 || snapInfo.getGravity() == 16) {
                    BGVerticalViewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(BGVerticalViewHolder2.recyclerView.getContext(), snapInfo.getGravity() == 1 ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL, false));
                    new LinearSnapHelper().attachToRecyclerView(BGVerticalViewHolder2.recyclerView);
                } else if (snapInfo.getGravity() == 17) {
                    BGVerticalViewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(BGVerticalViewHolder2.recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
                    new GravityPagerSnapHelper(GravityCompat.START).attachToRecyclerView(BGVerticalViewHolder2.recyclerView);
                } else {
                    BGVerticalViewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(BGVerticalViewHolder2.recyclerView.getContext()));
                    new GravitySnapHelper(snapInfo.getGravity()).attachToRecyclerView(BGVerticalViewHolder2.recyclerView);
                }
                if (snapInfo.getBG_Images().size() > 3) {
                    BGVerticalViewHolder2.seeMoreTextView.setVisibility(View.VISIBLE);
                } else {
                    BGVerticalViewHolder2.seeMoreTextView.setVisibility(View.GONE);
                }
                ArrayList arrayList2 = new ArrayList();
                if (snapInfo.getBG_Images().size() >= 6) {
                    for (int i3 = 0; i3 < 6; i3++) {
                        arrayList2.add(snapInfo.getBG_Images().get(i3));
                    }
                    arrayList = arrayList2;
                } else {
                    arrayList = snapInfo.getBG_Images();
                }
                Context context;
                boolean z2;
                if (this.flagForActivity == 1) {
                    context = this.context;
                    z2 = snapInfo.getGravity() == GravityCompat.START || snapInfo.getGravity() == GravityCompat.END || snapInfo.getGravity() == 1;
                    if (snapInfo.getGravity() != 17) {
                        z = false;
                    }
                    BGVerticalViewHolder2.recyclerView.setAdapter(new Poster_Basic_Adapters(context, z2, z, arrayList, this.flagForActivity, i, new Poster_On_Item_Click_Listener() {
                        public void onItemClick(int i) {
                            BGVerticalViewHolder2.seeMoreTextView.performClick();
                        }
                    }));
                } else {
                    context = this.context;
                    z2 = snapInfo.getGravity() == GravityCompat.START || snapInfo.getGravity() == GravityCompat.END || snapInfo.getGravity() == 1;
                    if (snapInfo.getGravity() != 17) {
                        z = false;
                    }
                    Poster_Basic_Adapters basicAdapters = new Poster_Basic_Adapters(context, z2, z, arrayList, this.flagForActivity, i, new Poster_On_Item_Click_Listener() {
                        public void onItemClick(int i) {
                            BGVerticalViewHolder2.seeMoreTextView.performClick();
                        }
                    });
                    BGVerticalViewHolder2.recyclerView.setAdapter(basicAdapters);
                    basicAdapters.setItemClickCallback(new Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String>() {
                        public void onClickCallBack(ArrayList<String> arrayList, ArrayList<Poster_BG_Image> arrayList2, String str, Activity activity, String str2) {
                            Poster_Bg_Vertical_Adapter.this.mSingleCallback.onClickCallBack(null, arrayList2, str, Poster_Bg_Vertical_Adapter.this.context, "");
                        }
                    });
                }
                BGVerticalViewHolder2.seeMoreTextView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (Poster_Bg_Vertical_Adapter.this.flagForActivity == 1) {
                            ((Poster_SelectBGIMGActivity) Poster_Bg_Vertical_Adapter.this.context).itemClickSeeMoreAdapter(snapInfo.getBG_Images(), snapInfo.getText());
                        } else {
                            Poster_Bg_Vertical_Adapter.this.mSingleCallback.onClickCallBack(null, snapInfo.getBG_Images(), "", Poster_Bg_Vertical_Adapter.this.context, "");
                        }
                    }
                });
                return;
            case 2:
                UnifiedNativeAdViewHolder unifiedNativeAdViewHolder = (UnifiedNativeAdViewHolder) viewHolder;
                return;
            default:
        }
    }


    public void removeLoadingView() {
        ArrayList arrayList = this.mSnaps;
        arrayList.remove(arrayList.size() - 1);
        notifyItemRemoved(this.mSnaps.size());
    }

    public void setItemClickCallback(Poster_OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case 1:
                return new BGVerticalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_efficient_snap_adapter, viewGroup, false));
            case 2:
                return new UnifiedNativeAdViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_custom_frame_layout, viewGroup, false));
            default:
                return new LoadingHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_cure_progress_view, viewGroup, false));
        }
    }

    @Override
    public int getItemCount() {
        return this.mSnaps.size();
    }
}
