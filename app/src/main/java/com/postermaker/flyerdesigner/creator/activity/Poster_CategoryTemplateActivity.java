package com.postermaker.flyerdesigner.creator.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.common.Scopes;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.postermaker.flyerdesigner.creator.Poster_Application;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.postermaker.flyerdesigner.creator.ads.MyDataSaved;
import com.postermaker.flyerdesigner.creator.ads.NativeAds;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.billing.Poster_BillingUpdatesListener;
import com.postermaker.flyerdesigner.creator.billing.Poster_SubscriptionsUtil;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_IV_Download_Manager;
import com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_templates;
import com.postermaker.flyerdesigner.creator.imageloader.Poster_Custom_Glide_IMG_Loader;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Co;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Datas;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Full_Poster_Thumb;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_StickerInfo;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Text_Info;
import com.postermaker.flyerdesigner.creator.receiver.Poster_NetworkConnectivityReceiver;
import com.postermaker.flyerdesigner.creator.utils.Poster_LogUtil;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Poster_CategoryTemplateActivity extends AppCompatActivity implements Poster_BillingUpdatesListener {

    public RewardedAd ADrewardedad;
    MyDataSaved myDataSaved;

    // The number of native ads to load.
    public int NUMBER_OF_ADS = 5;

    // List of MenuItems and native ads that populate the RecyclerView.
    private List<Object> mRecyclerViewItems = new ArrayList<>();

    // List of native ads that have been successfully loaded.
    private List<String> mNativeAds = new ArrayList<>();

    public ArrayList<Poster_Co> posterCos;
    ArrayList<Poster_Full_Poster_Thumb> arrayList = new ArrayList<>();
    public ArrayList<Poster_StickerInfo> stickerInfoArrayList;
    public ArrayList<String> url;
    public ArrayList<Poster_Text_Info> textInfoArrayList;

    private static final String TAG = "TemplateActivity";
    private Poster_AppPreferenceClass appPreferenceClass;

    public SweetAlertDialog pDialog;

    TextView tvTitle;
    ImageView iVBack, ivPro;

    RecyclerView rvForTemplateList;
    HomeCardAdapter homeCardAdapter;

    String cat_name, ratio = "0";
    int pos, cat_id, newWidth;

    boolean isActive;

    @Override
    public void onBackPressed() {
        new InterstitialAds().Show_Ads(Poster_CategoryTemplateActivity.this, new InterstitialAds.AdCloseListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.poster_new_activity_category_template);

        this.appPreferenceClass = new Poster_AppPreferenceClass(this);

        myDataSaved = new MyDataSaved(this);

        // adView = new AdView(this);
        // adView = findViewById(R.id.adView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        newWidth = size.x;
        newWidth = newWidth / 2 + newWidth / 4;

        iVBack = findViewById(R.id.iVBack);
        iVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivPro = findViewById(R.id.ivPro);
        YoYo.with(Techniques.Shake).duration(1000).repeat(500).playOn(ivPro);

        ivPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Poster_CategoryTemplateActivity.this, Poster_PurcheshActivity.class));
            }
        });

        pos = getIntent().getIntExtra("position", -1);
        cat_id = getIntent().getIntExtra("cat_id", -1);
        cat_name = getIntent().getStringExtra("cateName");

        tvTitle = findViewById(R.id.tvTitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/Raleway Bold.ttf");
        tvTitle.setTypeface(custom_font);
        tvTitle.setText(cat_name);

        try {
            if (Poster_fragment_templates.posterDatas.get(pos).getPoster_list() != null)
                arrayList = Poster_fragment_templates.posterDatas.get(pos).getPoster_list();

            rvForTemplateList = findViewById(R.id.rvForTemplateList);
            // StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            rvForTemplateList.setLayoutManager(layoutManager);

            mRecyclerViewItems.addAll(arrayList);

            homeCardAdapter = new HomeCardAdapter(mRecyclerViewItems);
            rvForTemplateList.setAdapter(homeCardAdapter);

            if (arrayList.size() < 5) {
                NUMBER_OF_ADS = 0;
            } else if (arrayList.size() <= 10) {
                NUMBER_OF_ADS = 2;
            } else if (arrayList.size() <= 20) {
                NUMBER_OF_ADS = 4;
            } else if (arrayList.size() <= 30) {
                NUMBER_OF_ADS = 6;
            } else if (arrayList.size() <= 40) {
                NUMBER_OF_ADS = 8;
            } else {
                NUMBER_OF_ADS = 10;
            }

            loadNativeAds();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT);
        }

//        initAppBilling();
    }

    private void loadNativeAds() {

        UnifiedNativeAd unifiedNativeAd = null;

        for (int i = 0; i < NUMBER_OF_ADS; i++) {
            mNativeAds.add("NATIVE");
        }

        insertAdsInMenuItems();
    }

    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            homeCardAdapter = new HomeCardAdapter(mRecyclerViewItems);
            rvForTemplateList.setAdapter(homeCardAdapter);
            return;
        }

        int offset = (mRecyclerViewItems.size() / mNativeAds.size()) + 1;
        int index = 1;
        for (String ad : mNativeAds) {
            mRecyclerViewItems.add(index, ad);
            index = index + offset;
        }

        homeCardAdapter = new HomeCardAdapter(mRecyclerViewItems);
        rvForTemplateList.setAdapter(homeCardAdapter);
    }

    @Override
    public void onBillingClientSetupFinished() {
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchaseList) {
        isActive = Poster_SubscriptionsUtil.isSubscriptionActive(purchaseList);

        if (!isActive) {

            // Adaptive_Banner
            new NativeAds(this).Adaptive_Banner(findViewById(R.id.adaptive_banner));

        }
        Poster_LogUtil.logDebug(TAG, "onPurchasesUpdated: " + isActive);
    }

    @Override
    public void onPurchaseVerified() {

    }

    public void deleteRecursive(File file) {
        try {
            if (file.isDirectory()) {
                for (File deleteRecursive : file.listFiles()) {
                    deleteRecursive(deleteRecursive);
                }
            }
            file.delete();
        } catch (NullPointerException e) {
            //  e.printStackTrace();
        }
    }

    public void loadPoster(String str, int i, int i2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Poster_AppConstants.BASE_URL_POSTER);
        stringBuilder.append("poster");
        final String str2 = str;
        final int i3 = i;
        final int i4 = i2;

        Poster_Application.getInstance().addToRequestQueue(new StringRequest(Request.Method.POST, stringBuilder.toString(), new Response.Listener<String>() {
            public void onResponse(String str) {
                try {
                    Poster_Datas posterDatas = new Gson().fromJson(str, Poster_Datas.class);
                    posterCos = new ArrayList();
                    posterCos = posterDatas.getData();
                    textInfoArrayList = (posterCos.get(0)).getPOSTERText_info();
                    stickerInfoArrayList = (posterCos.get(0)).getPOSTERSticker_info();
                    Poster_Co posterCo = posterCos.get(0);
                    ratio = posterCo.getPOSTERRatio();
                    url = new ArrayList();
                    url.add(posterCo.getPOSTERBack_image());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("==");
                    stringBuilder.append(stickerInfoArrayList.size());

                    //  Log.e("sticker", stringBuilder.toString());

                    for (int i = 0; i < stickerInfoArrayList.size(); i++) {
                        if (!((Poster_StickerInfo) stickerInfoArrayList.get(0)).getSTICKER_image().equals("")) {
                            ArrayList arrayList = url;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(Poster_AppConstants.BASE_URL_STICKER);
                            stringBuilder2.append(((Poster_StickerInfo) stickerInfoArrayList.get(i)).getSTICKER_image());
                            arrayList.add(stringBuilder2.toString());
                        }
                    }

                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
                    stringBuilder3.append("/.PosterResorces/");
                    String stringBuilder4 = stringBuilder3.toString();
                    File file = new File(stringBuilder4);
                    if (file.exists()) {
                        deleteRecursive(file);
                        file.mkdir();
                    } else {
                        file.mkdir();
                    }

                    Poster_IV_Download_Manager.getInstance(getApplicationContext()).addDownloadTask(new Poster_IV_Download_Manager.IV_DownloadTask(this, Poster_IV_Download_Manager.IVTask.DOWNLOAD, url, stringBuilder4, new Poster_IV_Download_Manager.Callback() {
                        public void onSuccess(Poster_IV_Download_Manager.IV_DownloadTask imageDownloadTask, ArrayList<String> arrayList) {
                            try {
                                if (pDialog != null && pDialog.isShowing()) {
                                    pDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d(Poster_IV_Download_Manager.class.getSimpleName(), "Image save success news ");
                            int i = 0;
                            while (i < arrayList.size()) {
                                try {
                                    if (i == 0) {
                                        posterCos.get(i).setPOSTERBack_image(arrayList.get(i));
                                    } else {
                                        (stickerInfoArrayList.get(i - 1)).setSTICKER_image(arrayList.get(i));
                                    }
                                    i++;
                                } catch (IndexOutOfBoundsException e2) {
                                    e2.printStackTrace();
                                    return;
                                }
                            }

                            File file = new File((posterCos.get(0)).getPOSTERBack_image());
                            if (!file.exists()) {
                                Log.d("not exist", "not exist");
                            } else if (file.length() == 0) {
                                Log.d("File Empty", "File does not have any content");
                            } else {

                                Intent intent = new Intent(Poster_CategoryTemplateActivity.this, PosterMakerActivity.class);
                                intent.putParcelableArrayListExtra("template", posterCos);
                                intent.putParcelableArrayListExtra("text", textInfoArrayList);
                                intent.putParcelableArrayListExtra("sticker", stickerInfoArrayList);
                                intent.putExtra(Scopes.PROFILE, "Background");
                                intent.putExtra("catId", 1);
                                intent.putExtra("loadUserFrame", false);
                                intent.putExtra("sizeposition", 0/* size postion */);
                                intent.putExtra("ratio", ratio);
                                intent.putExtra("title", cat_name);
                                intent.putExtra("Temp_Type", "MY_TEMP");
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Poster_IV_Download_Manager.ImageSaveFailureReason imageSaveFailureReason) {
                            String simpleName = Poster_IV_Download_Manager.class.getSimpleName();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Image save fail news ");
                            stringBuilder.append(imageSaveFailureReason);
                            Log.d(simpleName, stringBuilder.toString());
                            try {
                                if (pDialog != null && pDialog.isShowing()) {
                                    pDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }));
                } catch (JsonSyntaxException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error: ");
                stringBuilder.append(volleyError.getMessage());
                Log.e(str, stringBuilder.toString());
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("key", str2);
                hashMap.put("device", "1");
                hashMap.put("cat_id", String.valueOf(i3));
                hashMap.put("post_id", String.valueOf(i4));

                return hashMap;
            }
        });
    }

    public void makeStickerDir() {
        this.appPreferenceClass = new Poster_AppPreferenceClass(this);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        stringBuilder.append("/.Poster Design Stickers/sticker");
        File file = new File(stringBuilder.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        stringBuilder2.append("/.Poster Design Stickers/sticker/bg");
        File file2 = new File(stringBuilder2.toString());
        if (!file2.exists()) {
            file2.mkdirs();
        }
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        stringBuilder2.append("/.Poster Design Stickers/sticker/font");
        file2 = new File(stringBuilder2.toString());
        if (!file2.exists()) {
            file2.mkdirs();
        }
        for (int i = 0; i < 29; i++) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
            stringBuilder3.append("/.Poster Design Stickers/sticker/cat");
            stringBuilder3.append(i);
            File file3 = new File(stringBuilder3.toString());
            if (!file3.exists()) {
                file3.mkdirs();
            }
        }
        for (int i2 = 0; i2 < 11; i2++) {
            StringBuilder stringBuilder4 = new StringBuilder();
            stringBuilder4.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
            stringBuilder4.append("/.Poster Design Stickers/sticker/art");
            stringBuilder4.append(i2);
            File file4 = new File(stringBuilder4.toString());
            if (!file4.exists()) {
                file4.mkdirs();
            }
        }
        this.appPreferenceClass.putString(Poster_AppConstants.sdcardPath, file.getPath());
        String str = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("onCreate: ");
        stringBuilder.append(Poster_AppConstants.sdcardPath);
        //  Log.e(str, stringBuilder.toString());
    }

    public void setupProgress() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#D81B60"));
        pDialog.setTitleText("Downloading Templates");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void getPosKeyAndCall(int i, int i2) {
        loadPoster(Poster_fragment_templates.appkey, i, i2);
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }


    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 101);
    }

    public void networkError() {
        new SweetAlertDialog(Poster_CategoryTemplateActivity.this, SweetAlertDialog.WARNING_TYPE).setTitleText("No Internet connected?").setContentText("make sure your internet connection is working.").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        }).show();
    }

    public boolean permission() {
/*
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
*/

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
/*
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(getApplicationContext(),"Permission allowed",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Please allow permission",Toast.LENGTH_SHORT).show();
                }
            }
*/
        }
    }

    private void requestStoragePermission(final int i, final int i2) {
        /* Changed 22/05/2023 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        makeStickerDir();
                        setupProgress();
                        getPosKeyAndCall(i2, i);
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        } else {
            Dexter.withActivity(this).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        makeStickerDir();
                        setupProgress();
                        getPosKeyAndCall(i2, i);
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        }
    }


    class HomeCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int MENU_ITEM_VIEW_TYPE = 0;

        private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

        private List<Object> mRecyclerViewItems;

        //  ArrayList<Logo_Maker_FullThumbInfo> itemList;

        HomeCardAdapter(List<Object> items) {
            mRecyclerViewItems = items;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            switch (i) {
                case UNIFIED_NATIVE_AD_VIEW_TYPE:
                    View unifiedNativeLayoutView = LayoutInflater.from(
                            viewGroup.getContext()).inflate(R.layout.poster_new_custom_native_ads,
                            viewGroup, false);
                    return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
                case MENU_ITEM_VIEW_TYPE:
                    // Fall through.
                default:
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_new_card_list_templates, viewGroup, false);
                    return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

            int viewType = getItemViewType(i);
            switch (viewType) {
                case UNIFIED_NATIVE_AD_VIEW_TYPE:

                    UnifiedNativeAdViewHolder unifiedNativeAdViewHolder =
                            (HomeCardAdapter.UnifiedNativeAdViewHolder) holder;

                    // Native_Ad
                    new NativeAds(Poster_CategoryTemplateActivity.this).Native_Ad(unifiedNativeAdViewHolder.native_big);

                    break;
                case MENU_ITEM_VIEW_TYPE:
                    // fall through
                default:

                    final Poster_Full_Poster_Thumb fullPosterThumb = (Poster_Full_Poster_Thumb) mRecyclerViewItems.get(i);

                    ViewHolder viewHolder = (ViewHolder) holder;

                    String ratio = fullPosterThumb.get_POST_Ratio();
                    int pro = fullPosterThumb.getPRO();

                    if (pro == 1) {
                        viewHolder.iv_pro.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.iv_pro.setVisibility(View.GONE);
                    }

                    float y = 1;
                    if (ratio != null) {
                        String[] widthheight = ratio.split(":");
                        y = Float.parseFloat(widthheight[1]) / Float.parseFloat(widthheight[0]);
                    }

                    //  int scale = (int) getApplicationContext().getResources().getDisplayMetrics().density;
                    int newHeight = (int) (newWidth * y);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newWidth, newHeight);
                    viewHolder.image.requestLayout();
                    viewHolder.image.setLayoutParams(params);

                    new Poster_Custom_Glide_IMG_Loader(viewHolder.image, viewHolder.progressBar).loadImgFromUrl(fullPosterThumb.getPost_thumb(), new RequestOptions().priority(Priority.HIGH));
                    // Glide.with(CategoryTemplateActivity.this).load(fullPosterThumb.getPost_thumb()).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.image);

                    PushDownAnim.setPushDownAnimTo(viewHolder.cardView);
                    viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isActive) {
                                if (Poster_NetworkConnectivityReceiver.isNetConnected()) {
                                    requestStoragePermission(fullPosterThumb.getPost_id(), cat_id);
                                } else {
                                    networkError();
                                }
                            } else {
                                if (pro == 1) {

                                    AlertDialog dialogBuilder = new AlertDialog.Builder(Poster_CategoryTemplateActivity.this).create();
                                    LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                                    View dialogView = mInflater.inflate(R.layout.before_save_popup, null);

                                    ImageView ivClose = dialogView.findViewById(R.id.actionCancel);
                                    ImageView iv_premium = dialogView.findViewById(R.id.iv_premium);
                                    ImageView iv_watch_ads = dialogView.findViewById(R.id.iv_watch_ads);

                                    Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                                    int width = display.getWidth();
                                    int height = display.getHeight();

                                    dialogBuilder.getWindow().setLayout((2 * width) / 7, (4 * height) / 5);

                                    ivClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogBuilder.dismiss();
                                        }
                                    });

                                    iv_premium.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogBuilder.dismiss();
                                            startActivity(new Intent(Poster_CategoryTemplateActivity.this, Poster_PurcheshActivity.class));
                                        }
                                    });

                                    iv_watch_ads.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            dialogBuilder.dismiss();

                                            Log.e("Akash", "onClick: 2");

                                            ProgressDialog progressDialog = new ProgressDialog(Poster_CategoryTemplateActivity.this);
                                            progressDialog.setCancelable(false);
                                            progressDialog.setMessage("Loading Video Ads..");
                                            progressDialog.show();

                                            AdRequest adRequest = new AdRequest.Builder().build();
                                            RewardedAd.load(Poster_CategoryTemplateActivity.this, myDataSaved.get_google_reward(), adRequest, new RewardedAdLoadCallback() {
                                                @Override
                                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                                    ADrewardedad = null;
                                                    progressDialog.cancel();

                                                    new InterstitialAds().Show_Ads(Poster_CategoryTemplateActivity.this, new InterstitialAds.AdCloseListener() {
                                                        @Override
                                                        public void onAdClosed() {
                                                            if (Poster_NetworkConnectivityReceiver.isNetConnected()) {
                                                                requestStoragePermission(fullPosterThumb.getPost_id(), cat_id);
                                                            } else {
                                                                networkError();
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                                    ADrewardedad = rewardedAd;
                                                    ADrewardedad.show(Poster_CategoryTemplateActivity.this, new OnUserEarnedRewardListener() {
                                                        @Override
                                                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                            //Your Code Goes Here
                                                        }
                                                    });
                                                    ADrewardedad.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdShowedFullScreenContent() {
                                                            progressDialog.cancel();

                                                        }

                                                        @Override
                                                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                                            Log.d("TAG", "Ad failed to show.");
                                                            ADrewardedad = null;
                                                            progressDialog.cancel();


                                                        }

                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            Log.d("TAG", "Ad was dismissed.");
                                                            ADrewardedad = null;

                                                            if (Poster_NetworkConnectivityReceiver.isNetConnected()) {
                                                                requestStoragePermission(fullPosterThumb.getPost_id(), cat_id);
                                                            } else {
                                                                networkError();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });

                                    dialogBuilder.setCanceledOnTouchOutside(false);
                                    dialogBuilder.setCancelable(false);
                                    dialogBuilder.setView(dialogView);
                                    dialogBuilder.show();

                                    //
                                } else {
                                    new InterstitialAds().Show_Ads(Poster_CategoryTemplateActivity.this, new InterstitialAds.AdCloseListener() {
                                        @Override
                                        public void onAdClosed() {
                                            if (Poster_NetworkConnectivityReceiver.isNetConnected()) {
                                                requestStoragePermission(fullPosterThumb.getPost_id(), cat_id);
                                            } else {
                                                networkError();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
            }
        }

        @Override
        public int getItemCount() {
            if (mRecyclerViewItems == null)
                return 0;
            return mRecyclerViewItems.size();
        }

        @Override
        public int getItemViewType(int position) {

            Object recyclerViewItem = mRecyclerViewItems.get(position);
            if (recyclerViewItem instanceof String) {
                return UNIFIED_NATIVE_AD_VIEW_TYPE;
            }
            return MENU_ITEM_VIEW_TYPE;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            ImageView image, iv_pro;
            ProgressBar progressBar;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardViewHome);
                image = itemView.findViewById(R.id.iv_image);
                iv_pro = itemView.findViewById(R.id.iv_pro);
                progressBar = itemView.findViewById(R.id.progressBar1);
            }
        }

        class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {

            FrameLayout native_big;

            UnifiedNativeAdViewHolder(View view) {
                super(view);

                native_big = view.findViewById(R.id.native_big);

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
