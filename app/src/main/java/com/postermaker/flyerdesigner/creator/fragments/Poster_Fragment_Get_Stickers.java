package com.postermaker.flyerdesigner.creator.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.postermaker.flyerdesigner.creator.Poster_Application;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Vertical_Stickers_Adapter;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_OnClickCallback;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Data_Snap_Listener;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Load_More_Listener;
import com.postermaker.flyerdesigner.creator.listener.Poster_RV_Load_More_Scroll;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Data_Provider;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Main_BG_Image;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Snap_Info;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Thumb_BG;

public class Poster_Fragment_Get_Stickers extends Fragment {

    private ArrayList<Object> snapData = new ArrayList();
    public static ArrayList<Poster_Main_BG_Image> thumbnail_bg;

    private static Poster_On_Data_Snap_Listener onGetSnap;

    private static final String TAG = "Fragment_Get_Stickers";
    private int totalAds, cnt = 0;
    private ProgressBar loading_view;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private RelativeLayout rlAd;
    private Poster_RV_Load_More_Scroll scrollListener;
    private Poster_Vertical_Stickers_Adapter snapAdapter;
    private Poster_Data_Provider dataProvider;
    private StickerPagerAdapter adapter;
    private ViewPager pager;
    private TabLayout tabs;

    public static Poster_Fragment_Get_Stickers newInstance() {
        return new Poster_Fragment_Get_Stickers();
    }

    private void getStickerData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Poster_AppConstants.BASE_URL_POSTER);
        stringBuilder.append("sticker");

        Poster_Application.getInstance().addToRequestQueue(new StringRequest(1, stringBuilder.toString(), new Listener<String>() {
            @Override
            public void onResponse(String str) {
                try {
                    thumbnail_bg = (new Gson().fromJson(str, Poster_Thumb_BG.class)).getThumbnail_bg();
                    Poster_Fragment_Get_Stickers.this.loadStickerData();
                } catch (JsonSyntaxException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Poster_AppConstants.BASE_URL_POSTER = Poster_AppConstants.BASE_URL_POSTER_SECOND;
                Poster_AppConstants.BASE_URL_STICKER = Poster_AppConstants.BASE_URL_STICKER_SECOND;
                Poster_AppConstants.BASE_URL_BG = Poster_AppConstants.BASE_URL_BG_SECOND;
                Poster_AppConstants.BASE_URL = Poster_AppConstants.BASE_URL_SECOND;
                Poster_AppConstants.stickerURL = Poster_AppConstants.stickerURL_SECOND;
                Poster_AppConstants.fURL = Poster_AppConstants.fURL_SECOND;
                Poster_AppConstants.bgURL = Poster_AppConstants.bgURL_SECOND;
                Poster_Application.getInstance().cancelPendingRequests(Poster_Fragment_Get_Stickers.TAG);
                String str = Poster_Fragment_Get_Stickers.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error: ");
                stringBuilder.append(volleyError.getMessage());
                Log.e(str, stringBuilder.toString());
                Poster_Fragment_Get_Stickers.this.getStickerData();
            }
        }) {

            @Override
            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("device", "1");
                return hashMap;
            }
        }, TAG);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.poster_layout_rootart_fragment, viewGroup, false);

        mRecyclerView = inflate.findViewById(R.id.overlay_artwork);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(this.mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        loading_view = inflate.findViewById(R.id.loading_view);
        onGetSnap = (Poster_On_Data_Snap_Listener) getActivity();

        rlAd = inflate.findViewById(R.id.rl_ad);

        pager = inflate.findViewById(R.id.viewpager);
        tabs = inflate.findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(pager);

        if (thumbnail_bg != null) {
            loadStickerData();
        } else {
            getStickerData();
        }
        return inflate;
    }

    public class StickerPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> list = new ArrayList<>();
        List<String> TitleList = new ArrayList<>();

        public StickerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void AddFragment(Fragment fragment, String Title) {
            list.add(fragment);
            TitleList.add(Title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return TitleList.get(position);
        }
    }


    private void SeeMoreData() {
        this.snapAdapter.showLoadingView();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Poster_Fragment_Get_Stickers.this.snapAdapter.hideLoadingView();
                Poster_Fragment_Get_Stickers.this.snapAdapter.insertData(Poster_Fragment_Get_Stickers.this.dataProvider.getLoad_More_PosterItemsS());
                Poster_Fragment_Get_Stickers.this.snapAdapter.notifyDataSetChanged();
                Poster_Fragment_Get_Stickers.this.scrollListener.set_Data_Loaded();
            }
        }, 3000);
    }

    private void loadStickerData() {
        adapter = new StickerPagerAdapter(getActivity().getSupportFragmentManager());

        for (int i = 0; i < thumbnail_bg.size(); i++) {

            Poster_fragment_stickers fragment_stickers = new Poster_fragment_stickers();
            fragment_stickers.setCategory_id(i);
            adapter.AddFragment(fragment_stickers, thumbnail_bg.get(i).getCategory_name());

            this.snapData.add(new Poster_Snap_Info(1, ((Poster_Main_BG_Image) thumbnail_bg.get(i)).getCategory_name(), (thumbnail_bg.get(i)).getCategory_list()));
        }

        pager.setAdapter(adapter);

        this.dataProvider = new Poster_Data_Provider();
        this.dataProvider.apply_Poster_List(this.snapData);
        if (this.snapData != null) {
            this.snapAdapter = new Poster_Vertical_Stickers_Adapter(getActivity(), this.dataProvider.get_Load_Poster_Items(), this.mRecyclerView, 0);
            this.mRecyclerView.setAdapter(this.snapAdapter);
            this.snapAdapter.setItemClickCallback(new Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String>() {
                public void onClickCallBack(ArrayList<String> arrayList, ArrayList<Poster_BG_Image> arrayList2, String str, Activity activity, String str2) {
                    if (str.equals("")) {
                        onGetSnap.onSnapFilter(arrayList2, 0, str2);
                    } else {
                        onGetSnap.onSnapFilter(0, 34, str, str2);
                    }
                }
            });
            this.scrollListener = new Poster_RV_Load_More_Scroll(this.mLinearLayoutManager);
            this.scrollListener.set_Data_LoadMore_Listener(new Poster_On_Load_More_Listener() {
                public void onLoadMore() {
                    Poster_Fragment_Get_Stickers.this.SeeMoreData();
                }
            });
            this.mRecyclerView.addOnScrollListener(this.scrollListener);
        }
    }

}
