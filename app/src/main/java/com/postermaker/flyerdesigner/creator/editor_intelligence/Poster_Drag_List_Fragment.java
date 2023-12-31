package com.postermaker.flyerdesigner.creator.editor_intelligence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;
import com.woxthebox.draglistview.DragListView.DragListListenerAdapter;

import java.util.ArrayList;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.PosterMakerActivity;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Item_Control_Adapter;
import com.postermaker.flyerdesigner.creator.receiver.Poster_NetworkConnectivityReceiver;

public class Poster_Drag_List_Fragment extends Fragment {

    private ArrayList<View> arrView = new ArrayList();
    private DragListView mDragListView;

    private ArrayList<Pair<Long, View>> mItemArray = new ArrayList();

    public static View HintView;
    public static RelativeLayout lay_Notext;


    public static Poster_Drag_List_Fragment newInstance() {
        return new Poster_Drag_List_Fragment();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    private static class DragListtemItem extends DragItem {
        DragListtemItem(Context context, int i) {
            super(context, i);
        }

        @Override
        public void onBindDragView(View view, View view2) {
            Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
            ((ImageView) view2.findViewById(R.id.backimg)).setImageBitmap(createBitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {

        View inflate = layoutInflater.inflate(R.layout.poster_layer_editor_list_layout, viewGroup, false);
        Poster_NetworkConnectivityReceiver.isNetConnected();

        mDragListView = inflate.findViewById(R.id.drag_list_view);
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragListListener(new DragListListenerAdapter() {
            public void onItemDragStarted(int i) {
            }

            public void onItemDragEnded(int i, int i2) {
                if (i != i2) {
                    for (i = Poster_Drag_List_Fragment.this.mItemArray.size() - 1; i >= 0; i--) {
                        ((View) ((Pair) Poster_Drag_List_Fragment.this.mItemArray.get(i)).second).bringToFront();
                    }
                    PosterMakerActivity.stickerLayout.requestLayout();
                    PosterMakerActivity.stickerLayout.postInvalidate();
                }
            }
        });

        ((TextView) inflate.findViewById(R.id.txt_Nolayers)).setTypeface(Poster_AppConstants.getTextTypeface(getActivity()));
        lay_Notext = inflate.findViewById(R.id.lay_text);
        HintView = inflate.findViewById(R.id.HintView);
        (inflate.findViewById(R.id.lay_frame)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (PosterMakerActivity.layerContainer.getVisibility() == View.VISIBLE) {
                    PosterMakerActivity.layerContainer.animate().translationX((float) (-PosterMakerActivity.layerContainer.getRight())).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            PosterMakerActivity.layerContainer.setVisibility(View.GONE);
                            PosterMakerActivity.btnLayer.setVisibility(View.VISIBLE);
                        }
                    }, 200);
                }
            }
        });
        return inflate;
    }

    public void get_Layout_Child() {
        this.arrView.clear();
        this.mItemArray.clear();
        if (PosterMakerActivity.stickerLayout.getChildCount() != 0) {
            lay_Notext.setVisibility(View.GONE);
            for (int childCount = PosterMakerActivity.stickerLayout.getChildCount() - 1; childCount >= 0; childCount--) {
                this.mItemArray.add(new Pair(Long.valueOf((long) childCount), PosterMakerActivity.stickerLayout.getChildAt(childCount)));
                this.arrView.add(PosterMakerActivity.stickerLayout.getChildAt(childCount));
            }
        } else {
            lay_Notext.setVisibility(View.VISIBLE);
        }
        setup_List_Fragment_RecyclerView();
    }

    private void setup_List_Fragment_RecyclerView() {
        this.mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mDragListView.setAdapter(new Poster_Item_Control_Adapter(getActivity(), this.mItemArray, R.layout.poster_item_list_layer_editor, R.id.layeradjust, false), true);
        this.mDragListView.setCanDragHorizontally(false);
    }
}
