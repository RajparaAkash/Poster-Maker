package com.postermaker.flyerdesigner.creator.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.postermaker.flyerdesigner.creator.Poster_Application;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.Poster_IntegerVersionSignature;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.postermaker.flyerdesigner.creator.billing.Poster_BillingUpdatesListener;
import com.postermaker.flyerdesigner.creator.billing.Poster_SubscriptionsUtil;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Poster_fragment_backgrounds extends Fragment implements Poster_BillingUpdatesListener {

    private ArrayList<Poster_BG_Image> BG_Images;

    private RecyclerView RvItems;
    private Activity activity;

    private int category_id = 0;

    boolean isActive;

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public static Poster_fragment_backgrounds newInstance() {
        return new Poster_fragment_backgrounds();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();

//        initAppBilling();

        return inflater.inflate(R.layout.poster_layout_fragment_bg_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            Init(view);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
//        if (mBillingManager != null) {
//            mBillingManager.destroy();
//        }
        super.onDestroy();
    }

    private void Init(View view) {

        RvItems = view.findViewById(R.id.RvItems);
        RvItems.setLayoutManager(new GridLayoutManager(activity, 3));

        BG_Images = Poster_Fragment_BGImg.thumbnail_bg.get(category_id).getCategory_list();

        if (BG_Images != null) {
            StickerAdapter emojiAdapter = new StickerAdapter();
            RvItems.setAdapter(emojiAdapter);
        }
    }

    @Override
    public void onBillingClientSetupFinished() {

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        isActive = Poster_SubscriptionsUtil.isSubscriptionActive(purchases);
    }

    @Override
    public void onPurchaseVerified() {

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_item_row_backgorunds, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            Glide.with(activity).load("https://postermaker.letsappbuilder.com/uploads/" + BG_Images.get(position).getBGImage_url()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new Poster_IntegerVersionSignature(Poster_AppConstants.getAPPVersionInfo())).dontAnimate().override(200, 200).fitCenter().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).into(holder.IvStickerImage);

            PushDownAnim.setPushDownAnimTo(holder.IvStickerImage);
            holder.IvStickerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isActive) {

                        new InterstitialAds().Show_Ads(getActivity(), new InterstitialAds.AdCloseListener() {
                            @Override
                            public void onAdClosed() {

                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage(getResources().getString(R.string.plzwait));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                Poster_fragment_backgrounds backgroundFragment = Poster_fragment_backgrounds.this;

                                String imagesDir = Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOCUMENTS).toString() + "/Stickers";

                                File filefir = new File(imagesDir);

                                if (!filefir.isDirectory()) {
                                    filefir.mkdirs();
                                }

                                File file = new File(filefir.getAbsolutePath(), "temp_" + System.currentTimeMillis() + ".png");

                                Poster_Application.getInstance().addToRequestQueue(new ImageRequest("https://postermaker.letsappbuilder.com/uploads/" + BG_Images.get(position).getBGImage_url(), new Response.Listener<Bitmap>() {
                                    public void onResponse(Bitmap bitmap) {
                                        try {
                                            progressDialog.dismiss();
                                            try {
                                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                                fileOutputStream.flush();
                                                fileOutputStream.close();
                                                try {
                                                    Poster_Fragment_BGImg.onGetSnap.onSnapFilter(0, 104, file.getAbsolutePath(), "");
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
                                }, 0, 0, null, new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError volleyError) {
                                        progressDialog.dismiss();
                                    }
                                }));
                            }
                        });
                    } else {
                        Log.e("##", "backgrounds: active");

                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getResources().getString(R.string.plzwait));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        Poster_fragment_backgrounds backgroundFragment = Poster_fragment_backgrounds.this;

                        String imagesDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOCUMENTS).toString() + "/Stickers";

                        File filefir = new File(imagesDir);

                        if (!filefir.isDirectory()) {
                            filefir.mkdirs();
                        }

                        File file = new File(filefir.getAbsolutePath(), "temp_" + System.currentTimeMillis() + ".png");

                        Poster_Application.getInstance().addToRequestQueue(new ImageRequest("https://postermaker.letsappbuilder.com/uploads/" + BG_Images.get(position).getBGImage_url(), new Response.Listener<Bitmap>() {
                            public void onResponse(Bitmap bitmap) {
                                try {
                                    progressDialog.dismiss();
                                    try {
                                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                        try {
                                            Poster_Fragment_BGImg.onGetSnap.onSnapFilter(0, 104, file.getAbsolutePath(), "");
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
                        }, 0, 0, null, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError volleyError) {
                                progressDialog.dismiss();
                            }
                        }));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (BG_Images == null) {
                return 0;
            }
            return BG_Images.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView IvStickerImage;
            ProgressBar progressBar;

            ViewHolder(View itemView) {
                super(itemView);
                IvStickerImage = itemView.findViewById(R.id.IvStickerImage);
                progressBar = itemView.findViewById(R.id.progress);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        }
    }

    private File getCacheFolder(Context context) {
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
}
