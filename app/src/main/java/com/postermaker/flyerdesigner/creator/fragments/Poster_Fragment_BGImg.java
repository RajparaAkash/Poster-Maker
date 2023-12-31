package com.postermaker.flyerdesigner.creator.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
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

import com.android.billingclient.api.Purchase;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.postermaker.flyerdesigner.creator.Poster_Application;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.billing.Poster_BillingUpdatesListener;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Backgrounds_Adapter;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Bg_Vertical_Adapter;
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

public class Poster_Fragment_BGImg extends Fragment implements Poster_BillingUpdatesListener {

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private RelativeLayout rlAd;
    private float screenHeight, screenWidth;
    private Poster_Data_Provider dataProvider;
    private ViewPager pager;
    private TabLayout tabs;
    private SectionsPagerAdapter adapter;

    private Poster_RV_Load_More_Scroll scrollListener;
    private ArrayList<Object> snapData = new ArrayList();
    private ProgressBar loading_view;
    private Poster_AppPreferenceClass appPreferenceClass;
    private static final String TAG = "Fragment_BGImg";

    public static ArrayList<Poster_Main_BG_Image> thumbnail_bg;
    public Poster_Bg_Vertical_Adapter veticalViewAdapter;
    public static int[] backgroundData;
    public static Poster_On_Data_Snap_Listener onGetSnap;

    private Poster_Backgrounds_Adapter backgroundsAdapter;
    private String categoryName;
    private int totalAds, size, orientation, category, cnt = 0;

    public static Poster_Fragment_BGImg newInstance() {
        return new Poster_Fragment_BGImg();
    }

    @Override
    public void onBillingClientSetupFinished() {

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {

    }

    @Override
    public void onPurchaseVerified() {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> list = new ArrayList<>();
        List<String> TitleList = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void AddFragment(Fragment fragment, String Title) {
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

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        View inflate = layoutInflater.inflate(R.layout.poster_layout_root_fragment, viewGroup, false);

        onGetSnap = (Poster_On_Data_Snap_Listener) getActivity();
        rlAd = inflate.findViewById(R.id.rl_ad);

        loading_view = inflate.findViewById(R.id.loading_view);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = (float) displayMetrics.widthPixels;
        this.screenHeight = (float) displayMetrics.heightPixels;
        this.appPreferenceClass = new Poster_AppPreferenceClass(getActivity());

        pager = inflate.findViewById(R.id.pager);
        tabs = inflate.findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(pager);

        mRecyclerView = inflate.findViewById(R.id.overlay_artwork);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(this.mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        if (thumbnail_bg != null) {
            lordBackgroundImageData();
        } else {
            getBackgroundImageData();
        }

        return inflate;
    }




    private void Load_BG_More_Data() {
        this.veticalViewAdapter.insertLoadingView();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Poster_Fragment_BGImg.this.veticalViewAdapter.removeLoadingView();
                Poster_Fragment_BGImg.this.veticalViewAdapter.InsertData(Poster_Fragment_BGImg.this.dataProvider.getLoad_More_PosterItemsS());
                Poster_Fragment_BGImg.this.veticalViewAdapter.notifyDataSetChanged();
                Poster_Fragment_BGImg.this.scrollListener.set_Data_Loaded();
            }
        }, 3000);
    }



    public File get_BG_CacheFolder(Context context) {
        File file;
        if (Environment.getExternalStorageState().equals("mounted")) {
            file = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        } else {
            file = null;
        }
        return !file.isDirectory() ? context.getCacheDir() : file;
    }

    public void getBackgroundImageData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Poster_AppConstants.BASE_URL_POSTER);
        stringBuilder.append("backgroundlatest");
        Poster_Application.getInstance().addToRequestQueue(new StringRequest(1, stringBuilder.toString(), new Listener<String>() {
            public void onResponse(String str) {
                try {
                    Poster_Fragment_BGImg.this.thumbnail_bg = ( new Gson().fromJson(str, Poster_Thumb_BG.class)).getThumbnail_bg();
                    Poster_Fragment_BGImg.this.lordBackgroundImageData();
                } catch (JsonSyntaxException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Poster_AppConstants.BASE_URL_POSTER = Poster_AppConstants.BASE_URL_POSTER_SECOND;
                Poster_AppConstants.BASE_URL_STICKER = Poster_AppConstants.BASE_URL_STICKER_SECOND;
                Poster_AppConstants.BASE_URL_BG = Poster_AppConstants.BASE_URL_BG_SECOND;
                Poster_AppConstants.BASE_URL = Poster_AppConstants.BASE_URL_SECOND;
                Poster_AppConstants.stickerURL = Poster_AppConstants.stickerURL_SECOND;
                Poster_AppConstants.fURL = Poster_AppConstants.fURL_SECOND;
                Poster_AppConstants.bgURL = Poster_AppConstants.bgURL_SECOND;
                Poster_Application.getInstance().cancelPendingRequests(Poster_Fragment_BGImg.TAG);
                String str = Poster_Fragment_BGImg.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error: ");
                stringBuilder.append(volleyError.getMessage());
                Log.e(str, stringBuilder.toString());
                Poster_Fragment_BGImg.this.getBackgroundImageData();
            }
        }) {

            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("device", "1");
                return hashMap;
            }
        }, TAG);
    }

    private void lordBackgroundImageData() {

        adapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        for (int i = 0; i < thumbnail_bg.size(); i++) {
            Poster_fragment_backgrounds fragment_backgrounds = new Poster_fragment_backgrounds();
            fragment_backgrounds.setCategory_id(i);
            adapter.AddFragment(fragment_backgrounds, thumbnail_bg.get(i).getCategory_name());

            this.snapData.add(new Poster_Snap_Info(1, thumbnail_bg.get(i).getCategory_name(), thumbnail_bg.get(i).getCategory_list()));
        }

        pager.setAdapter(adapter);

        dataProvider = new Poster_Data_Provider();
        dataProvider.apply_Poster_List(this.snapData);

        if (snapData != null) {
            this.veticalViewAdapter = new Poster_Bg_Vertical_Adapter(getActivity(), this.dataProvider.get_Load_Poster_Items(), this.mRecyclerView, 0);
            this.mRecyclerView.setAdapter(this.veticalViewAdapter);
            this.scrollListener = new Poster_RV_Load_More_Scroll(this.mLinearLayoutManager);
            this.scrollListener.set_Data_LoadMore_Listener(new Poster_On_Load_More_Listener() {
                public void onLoadMore() {
                    Poster_Fragment_BGImg.this.Load_BG_More_Data();
                }
            });
            this.mRecyclerView.addOnScrollListener(this.scrollListener);
        }

        mRecyclerView.setAdapter(this.veticalViewAdapter);
        veticalViewAdapter.setItemClickCallback(new Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String>() {
            public void onClickCallBack(ArrayList<String> arrayList, ArrayList<Poster_BG_Image> arrayList2, String str, Activity activity, String str2) {
                if (str.equals("")) {
                    onGetSnap.onSnapFilter(arrayList2, 1, "");
                    return;
                }
                final ProgressDialog progressDialog = new ProgressDialog(Poster_Fragment_BGImg.this.getContext());
                progressDialog.setMessage(Poster_Fragment_BGImg.this.getResources().getString(R.string.plzwait));
                progressDialog.setCancelable(false);
                progressDialog.show();
                Poster_Fragment_BGImg fragmentBGImages = Poster_Fragment_BGImg.this;
                final File cacheFolder = fragmentBGImages.get_BG_CacheFolder(fragmentBGImages.getContext());
                Poster_Application.getInstance().addToRequestQueue(new ImageRequest(str, new Listener<Bitmap>() {
                    public void onResponse(Bitmap bitmap) {
                        try {
                            progressDialog.dismiss();
                            try {
                                File file = new File(cacheFolder, "localFileName.png");
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
                                fileOutputStream.flush();
                                fileOutputStream.close();
                                try {
                                    onGetSnap.onSnapFilter(0, 104, file.getAbsolutePath(), "");
                                } catch (Exception e) {
                                    try {
                                        e.printStackTrace();
                                    } catch (NullPointerException e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            } catch (FileNotFoundException e3) {
                                e3.printStackTrace();
                            } catch (IOException e4) {
                                e4.printStackTrace();
                            }
                        } catch (Exception e5) {
                            e5.printStackTrace();
                        }
                    }
                }, 0, 0, null, new ErrorListener() {
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                    }
                }));
            }
        });
    }

}
