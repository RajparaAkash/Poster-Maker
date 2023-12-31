package com.postermaker.flyerdesigner.creator.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.stepstone.apprating.CKt;

import java.util.ArrayList;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Sticker_Adapter;
import com.postermaker.flyerdesigner.creator.custom_view.Poster_Item_Grid_Space_Decorator;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_OnClickCallback;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Data_Snap_Listener;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Load_More_Listener;
import com.postermaker.flyerdesigner.creator.listener.Poster_RV_Load_More_Scroll;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Data_Provider;

public class Poster_Fragment_More_Sticker extends Fragment {

    private ArrayList<Poster_BG_Image> category_list;
    private String color;
    private ProgressBar loading_view;
    private GridLayoutManager mLayoutManager;
    private int numColumns = 0;

    private Poster_On_Data_Snap_Listener onGetSnap;

    private RecyclerView recyclerView;
    private RelativeLayout rlAd;
    private Poster_RV_Load_More_Scroll scrollListener;
    private Poster_Sticker_Adapter stickerAdapter;
    private Poster_Data_Provider dataProvider;

    private void initializeCategory() {
        this.dataProvider = new Poster_Data_Provider();
        this.dataProvider.setStickerList(this.category_list);
        this.stickerAdapter = new Poster_Sticker_Adapter(getActivity(), this.dataProvider.get_Load_More_Sticker_Items(), getResources().getDimensionPixelSize(R.dimen.logo_image_size), getResources().getDimensionPixelSize(R.dimen.image_padding), this.color);
        this.mLayoutManager = new GridLayoutManager(getContext(), 3);
        this.recyclerView.setLayoutManager(this.mLayoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new Poster_Item_Grid_Space_Decorator(3, 40, true));
        this.loading_view.setVisibility(View.GONE);
        this.recyclerView.setAdapter(this.stickerAdapter);
        this.stickerAdapter.setItemClickCallback(new Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String>() {
            public void onClickCallBack(ArrayList<String> arrayList, ArrayList<Poster_BG_Image> arrayList2, String str, Activity activity, String str2) {
                Poster_Fragment_More_Sticker.this.onGetSnap.onSnapFilter(0, 34, str, str2);
            }
        });
        this.mLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int i) {
                switch (Poster_Fragment_More_Sticker.this.stickerAdapter.getItemViewType(i)) {
                    case 0:
                        return 1;
                    case 1:
                        return 3;
                    default:
                        return -1;
                }
            }
        });

        this.scrollListener = new Poster_RV_Load_More_Scroll(this.mLayoutManager);
        this.scrollListener.set_Data_LoadMore_Listener(new Poster_On_Load_More_Listener() {
            public void onLoadMore() {
                Poster_Fragment_More_Sticker.this.LoadExtraStickerData();
            }
        });
        this.recyclerView.addOnScrollListener(this.scrollListener);
    }

    public static Poster_Fragment_More_Sticker newInstance(ArrayList<Poster_BG_Image> arrayList, String str) {
        Poster_Fragment_More_Sticker fragmentMoreSticker = new Poster_Fragment_More_Sticker();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(CKt.DIALOG_DATA, arrayList);
        bundle.putString("color", str);
        fragmentMoreSticker.setArguments(bundle);
        return fragmentMoreSticker;
    }

    private void LoadExtraStickerData() {
        this.stickerAdapter.insertLoadingView();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Poster_Fragment_More_Sticker.this.stickerAdapter.hideLoadingView();
                Poster_Fragment_More_Sticker.this.stickerAdapter.insertData(Poster_Fragment_More_Sticker.this.dataProvider.getLoad_More_StickerItemsS());
                Poster_Fragment_More_Sticker.this.stickerAdapter.notifyDataSetChanged();
                Poster_Fragment_More_Sticker.this.scrollListener.set_Data_Loaded();
            }
        }, 3000);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.poster_layout_root_fragment, viewGroup, false);

        this.recyclerView = inflate.findViewById(R.id.overlay_artwork);
        this.onGetSnap = (Poster_On_Data_Snap_Listener) getActivity();
        this.rlAd = inflate.findViewById(R.id.rl_ad);
        this.loading_view = inflate.findViewById(R.id.loading_view);
        this.category_list = getArguments().getParcelableArrayList(CKt.DIALOG_DATA);
        this.color = getArguments().getString("color");
        this.loading_view.setVisibility(View.GONE);
        this.stickerAdapter = new Poster_Sticker_Adapter(getActivity(), this.category_list, getResources().getDimensionPixelSize(R.dimen.logo_image_size), getResources().getDimensionPixelSize(R.dimen.image_padding), this.color);

        initializeCategory();

        return inflate;
    }

}
