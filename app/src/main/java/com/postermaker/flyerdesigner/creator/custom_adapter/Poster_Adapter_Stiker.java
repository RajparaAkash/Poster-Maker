package com.postermaker.flyerdesigner.creator.custom_adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.Poster_IntegerVersionSignature;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_OnClickCallback;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Item_Click_Listener;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;
import com.postermaker.flyerdesigner.creator.receiver.Poster_NetworkConnectivityReceiver;

public class Poster_Adapter_Stiker extends Adapter<Poster_Adapter_Stiker.ViewHolder> {

    public Poster_OnClickCallback<ArrayList<String>, Integer, String, Activity, String> mSingleCallback;
    public ArrayList<Poster_BG_Image> BG_Images;
    public Poster_AppPreferenceClass appPreference;

    private boolean mHorizontal, mPager, isDownloadProgress = true;

    String color;
    Context context;
    int flagForActivity;
    Poster_On_Item_Click_Listener listener;

    SharedPreferences preferences;

    public class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {
        public ImageView imageView;
        RelativeLayout imgDownload;
        ImageView ivLock;
        ProgressBar mProgressBar;
        TextView nameTextView;
        TextView ratingTextView;
        RelativeLayout rl_see_more;

        public void onClick(View view) {
        }

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.imgDownload = view.findViewById(R.id.imgDownload);
            this.imageView = view.findViewById(R.id.imageView);
            this.ivLock = view.findViewById(R.id.iv_lock);
            this.nameTextView = view.findViewById(R.id.nameTextView);
            this.ratingTextView = view.findViewById(R.id.ratingTextView);
            this.mProgressBar = view.findViewById(R.id.progressBar1);
            this.rl_see_more = view.findViewById(R.id.rl_see_more);
        }
    }

    public Poster_Adapter_Stiker(Context context, boolean z, boolean z2, ArrayList<Poster_BG_Image> arrayList, int i, String str, Poster_On_Item_Click_Listener onItemClickListener) {
        this.mHorizontal = z;
        this.BG_Images = arrayList;
        this.mPager = z2;
        this.context = context;
        this.flagForActivity = i;
        this.appPreference = new Poster_AppPreferenceClass(this.context);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.color = str;
        this.listener = onItemClickListener;
    }



    public void DownoloadSticker(String str, String str2, String str3) {
        AndroidNetworking.download(str, str2, str3).build().startDownload(new DownloadListener() {
            public void onDownloadComplete() {
                Poster_Adapter_Stiker.this.isDownloadProgress = true;
                Poster_Adapter_Stiker.this.notifyDataSetChanged();
            }

            public void onError(ANError aNError) {
                Poster_Adapter_Stiker.this.isDownloadProgress = true;
                Poster_Adapter_Stiker.this.notifyDataSetChanged();
                Toast.makeText(Poster_Adapter_Stiker.this.context, "Network Error", 0).show();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (this.mPager) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_list_item_pager_adapter, viewGroup, false));
        }
        ViewHolder viewHolder;
        if (this.mHorizontal) {
            viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_layout_sticker_adapters, viewGroup, false));
        } else {
            viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_layout_vertical_adapter, viewGroup, false));
        }
        return viewHolder;
    }

    public static String findFileNameFromUrl(String str) {
        return str.substring(str.lastIndexOf(47) + 1).split("\\?")[0].split("#")[0];
    }

    public void setItemClickCallback(Poster_OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }

    @Override
    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final Poster_BG_Image BG_Image = (Poster_BG_Image) this.BG_Images.get(i);
        if (i == this.BG_Images.size() - 1) {
            viewHolder.imgDownload.setVisibility(View.GONE);
            viewHolder.mProgressBar.setVisibility(View.GONE);
            viewHolder.ivLock.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.INVISIBLE);
            viewHolder.rl_see_more.setVisibility(View.VISIBLE);
            viewHolder.rl_see_more.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (Poster_Adapter_Stiker.this.listener != null) {
                        Poster_Adapter_Stiker.this.listener.onItemClick(i);
                    }
                }
            });
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Poster_AppConstants.BASE_URL_BG);
        stringBuilder.append("/Sticker_List/");
        stringBuilder.append(BG_Image.getBGImage_url());
        String stringBuilder2 = stringBuilder.toString();
        String[] split = Uri.parse(stringBuilder2).getPath().split("/");
        final String str = split[split.length - 2];
        viewHolder.rl_see_more.setVisibility(View.GONE);
        viewHolder.imageView.setVisibility(View.VISIBLE);
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(this.appPreference.getString(Poster_AppConstants.sdcardPath));
        stringBuilder3.append("/cat/");
        stringBuilder3.append(str);
        stringBuilder3.append("/");
        stringBuilder3.append(findFileNameFromUrl(stringBuilder2));
        File file = new File(stringBuilder3.toString());
        if (file.exists()) {
            viewHolder.imgDownload.setVisibility(View.GONE);
            viewHolder.mProgressBar.setVisibility(View.GONE);
            Glide.with(this.context).load(file.getPath()).thumbnail(0.1f).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new Poster_IntegerVersionSignature(Poster_AppConstants.getAPPVersionInfo())).dontAnimate().fitCenter().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).into(viewHolder.imageView);
        } else {
            viewHolder.imgDownload.setVisibility(View.VISIBLE);
            viewHolder.mProgressBar.setVisibility(View.GONE);
            Glide.with(this.context).load(stringBuilder2).thumbnail(0.1f).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).signature(new Poster_IntegerVersionSignature(Poster_AppConstants.getAPPVersionInfo())).dontAnimate().fitCenter().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).into(viewHolder.imageView);
        }
        viewHolder.imgDownload.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!Poster_NetworkConnectivityReceiver.isNetConnected()) {
                    Toast.makeText(Poster_Adapter_Stiker.this.context, "No Internet Connection!!!", 0).show();
                } else if (Poster_Adapter_Stiker.this.isDownloadProgress) {
                    Poster_Adapter_Stiker.this.isDownloadProgress = false;
                    viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(Poster_AppConstants.BASE_URL_BG);
                    stringBuilder.append("/Sticker_List/");
                    stringBuilder.append(BG_Image.getBGImage_url());
                    String stringBuilder2 = stringBuilder.toString();
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(Poster_Adapter_Stiker.this.appPreference.getString(Poster_AppConstants.sdcardPath));
                    stringBuilder3.append("/cat/");
                    stringBuilder3.append(str);
                    stringBuilder3.append("/");
                    File file = new File(stringBuilder3.toString());
                    String fileNameFromUrl = Poster_Adapter_Stiker.findFileNameFromUrl(stringBuilder2);
                    viewHolder.imgDownload.setVisibility(View.GONE);
                    Poster_Adapter_Stiker.this.DownoloadSticker(stringBuilder2, file.getPath(), fileNameFromUrl);
                } else {
                    Toast.makeText(Poster_Adapter_Stiker.this.context, "Please wait..", 0).show();
                }
            }
        });
        viewHolder.imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Poster_AppConstants.BASE_URL_BG);
                stringBuilder.append("/Sticker_List/");
                stringBuilder.append(BG_Image.getBGImage_url());
                String stringBuilder2 = stringBuilder.toString();
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(Poster_Adapter_Stiker.this.appPreference.getString(Poster_AppConstants.sdcardPath));
                stringBuilder3.append("/cat/");
                stringBuilder3.append(str);
                stringBuilder3.append("/");
                stringBuilder3.append(Poster_Adapter_Stiker.findFileNameFromUrl(stringBuilder2));
                File file = new File(stringBuilder3.toString());
                if (file.exists()) {
                    Poster_Adapter_Stiker.this.mSingleCallback.onClickCallBack(null, Poster_Adapter_Stiker.this.BG_Images, file.getPath(), (FragmentActivity) Poster_Adapter_Stiker.this.context, Poster_Adapter_Stiker.this.color);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.BG_Images.size();
    }
}
