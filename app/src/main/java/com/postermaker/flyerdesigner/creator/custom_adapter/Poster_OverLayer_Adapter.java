package com.postermaker.flyerdesigner.creator.custom_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.postermaker.flyerdesigner.creator.R;

public class Poster_OverLayer_Adapter extends Adapter<Poster_OverLayer_Adapter.OverLayViewHolder> {

    Context context;

    private int selected_position = 500;
    private int[] makeUpEditImage;

    public Poster_OverLayer_Adapter(Context context, int[] iArr) {
        this.context = context;
        this.makeUpEditImage = iArr;
    }

    public static class OverLayViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView imageView, viewImage;

        public OverLayViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.item_image);
            this.viewImage = view.findViewById(R.id.view_image);
            this.layout = view.findViewById(R.id.lay);
        }
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return this.makeUpEditImage.length;
    }

    @Override
    public void onBindViewHolder(OverLayViewHolder overLayViewHolder, final int i) {
        Glide.with(this.context).load(this.makeUpEditImage[i]).thumbnail(0.1f).apply(new RequestOptions().dontAnimate().centerCrop().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).into(overLayViewHolder.imageView);
        if (this.selected_position == i) {
            overLayViewHolder.viewImage.setVisibility(View.VISIBLE);
        } else {
            overLayViewHolder.viewImage.setVisibility(View.INVISIBLE);
        }
        overLayViewHolder.layout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Poster_OverLayer_Adapter overLayAdapter = Poster_OverLayer_Adapter.this;
                overLayAdapter.notifyItemChanged(overLayAdapter.selected_position);
                overLayAdapter = Poster_OverLayer_Adapter.this;
                overLayAdapter.selected_position = i;
                overLayAdapter.notifyItemChanged(overLayAdapter.selected_position);
            }
        });
    }

    @Override
    public OverLayViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        OverLayViewHolder overLayViewHolder = new OverLayViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_layout_recyleview_adapter, viewGroup, false));
        viewGroup.setId(i);
        viewGroup.setFocusable(false);
        viewGroup.setFocusableInTouchMode(false);
        return overLayViewHolder;
    }
}
