package com.postermaker.flyerdesigner.creator.fragments;

import static com.postermaker.flyerdesigner.creator.activity.Poster_MainActivity.materialSearchBar;
import static com.postermaker.flyerdesigner.creator.activity.Poster_MainActivity.tvNavTitle;
import static com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_templates.posterDatas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.postermaker.flyerdesigner.creator.Poster_Application;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.Poster_ViewPagerActivity;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.billing.Poster_BillingUpdatesListener;
import com.postermaker.flyerdesigner.creator.billing.Poster_SubscriptionsUtil;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.model.Poster_HomeModel;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Data_List;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_With_List;
import com.postermaker.flyerdesigner.creator.receiver.Poster_NetworkConnectivityReceiver;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Poster_fragment_home extends Fragment implements MaterialSearchBar.OnSearchActionListener, Poster_BillingUpdatesListener {

    private static final String TAG = "PosterCatActivity";

    //   public static ArrayList<Poster_Data_List> posterDatas = new ArrayList();

    //  public static String appkey = "";

    private String ratio = "0";
    private int catID = 0;

    private ArrayList<Poster_HomeModel> categorieList, tempList;
    private TextView tvTitle;

    public boolean isActive;

    private Activity activity;

    private RecyclerView recycleViewForHomeActivity;
    private HomeCardAdapter homeCardAdapter;

    private MaterialSearchBar searchBar;

    private static final int RC_SIGN_IN = 9001;

    public SweetAlertDialog pDialog;
    Poster_AppPreferenceClass appPreferenceClass;

//    private Poster_BillingManager mBillingManager;

//    private void initAppBilling() {
//        mBillingManager = new Poster_BillingManager(activity, this);
//    }

    public void setupProgress() {
        pDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#D81B60"));
        pDialog.setTitleText("Initializing...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.poster_new_fragment_home, container, false);

        activity = getActivity();

//        initAppBilling();

        if (materialSearchBar.getVisibility() == View.GONE) {
            materialSearchBar.setVisibility(View.VISIBLE);
            tvNavTitle.setVisibility(View.GONE);
        } else {
            tvNavTitle.setVisibility(View.GONE);
        }

        tvTitle = view.findViewById(R.id.tvTitle);
        Typeface custom_title = Typeface.createFromAsset(activity.getAssets(), "font/cabin.ttf");
        tvTitle.setTypeface(custom_title);

        appPreferenceClass = new Poster_AppPreferenceClass(getActivity());

        initRecyclerView(view);


        if (posterDatas.size() == 0) {
/*            if (NetworkConnectivityReceiver.isConnected()) {
                setupProgress();
                getPosKeyAndCall1();
            } else {
                networkError();
            }*/
        } else {
            setupAdapter();
        }

        searchBar = view.findViewById(R.id.searchBar);

        materialSearchBar.setOnSearchActionListener(this);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (homeCardAdapter != null)
                    homeCardAdapter.updateData(tempList, materialSearchBar.getText().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }


/*
    public void getPosKeyAndCall1() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(AppConstants.BASE_URL_POSTER);
        stringBuilder.append("apps_key");
        Application.getInstance().addToRequestQueue(new StringRequest(1, stringBuilder.toString(), new Response.Listener<String>() {
            public void onResponse(String str) {
                try {
                    str = (new Gson().fromJson(str, Key_Poster.class)).getKey();
                    appkey = str;
                    getPosterList(str);
                } catch (Exception e) {
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
                AppConstants.BASE_URL_POSTER = AppConstants.BASE_URL_POSTER_SECOND;
                AppConstants.BASE_URL_STICKER = AppConstants.BASE_URL_STICKER_SECOND;
                AppConstants.BASE_URL_BG = AppConstants.BASE_URL_BG_SECOND;
                AppConstants.BASE_URL = AppConstants.BASE_URL_SECOND;
                AppConstants.stickerURL = AppConstants.stickerURL_SECOND;
                AppConstants.fURL = AppConstants.fURL_SECOND;
                AppConstants.bgURL = AppConstants.bgURL_SECOND;
                Application.getInstance().cancelPendingRequests(TAG);

                getPosKeyAndCall1();
            }
        }) {
            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("device", "1");
                return hashMap;
            }
        }, TAG);
    }
*/

    @Override
    public void onDestroy() {
//        if (mBillingManager != null) {
//            mBillingManager.destroy();
//        }
        super.onDestroy();
    }

    public void getPosterList(String str) {
        final String str2 = str;
        Poster_Application.getInstance().addToRequestQueue(new StringRequest(1, "https://postermaker.letsappbuilder.com/apipro/swiperCat2", new Response.Listener<String>() {
            public void onResponse(String str) {

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                posterDatas.clear();
                try {
                    try {
                        Iterator it = (new Gson().fromJson(str, Poster_With_List.class)).getData().iterator();
                        while (it.hasNext()) {
                            posterDatas.add((Poster_Data_List) it.next());
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("==");
                        stringBuilder.append(posterDatas.size());
                        Log.e("pos cat ArrayList", stringBuilder.toString());
                        setupAdapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error: ");
                stringBuilder.append(volleyError.getMessage());
                Log.e(str, stringBuilder.toString());
                Poster_AppConstants.BASE_URL_POSTER = Poster_AppConstants.BASE_URL_POSTER_SECOND;
                Poster_AppConstants.BASE_URL_STICKER = Poster_AppConstants.BASE_URL_STICKER_SECOND;
                Poster_AppConstants.BASE_URL_BG = Poster_AppConstants.BASE_URL_BG_SECOND;
                Poster_AppConstants.BASE_URL = Poster_AppConstants.BASE_URL_SECOND;
                Poster_AppConstants.stickerURL = Poster_AppConstants.stickerURL_SECOND;
                Poster_AppConstants.fURL = Poster_AppConstants.fURL_SECOND;
                Poster_AppConstants.bgURL = Poster_AppConstants.bgURL_SECOND;
                Poster_Application.getInstance().cancelPendingRequests(TAG);

                // getPosKeyAndCall1();
            }
        }) {
            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("key", str2);
                hashMap.put("device", "1");
                hashMap.put("cat_id", String.valueOf(catID));
                hashMap.put("package", "com.postermakerflyerdesigner");
                hashMap.put("ratio", ratio);
                return hashMap;
            }
        }, TAG);
    }

    public void initRecyclerView(View view) {
        categorieList = new ArrayList<Poster_HomeModel>();

        recycleViewForHomeActivity = view.findViewById(R.id.recycleViewForHomeActivity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycleViewForHomeActivity.setLayoutManager(layoutManager);
    }

    public void setupAdapter() {

        categorieList = new ArrayList<>();
        tempList = new ArrayList<>();
        for (int i = 0; i < posterDatas.size(); i++) {

            Poster_HomeModel homeModel = new Poster_HomeModel();
            homeModel.setIcon(posterDatas.get(i).getPOSTERThumb_img());
            homeModel.setTitle(posterDatas.get(i).getPOSTERCat_name());

            categorieList.add(homeModel);
        }

        tempList = categorieList;

        homeCardAdapter = new HomeCardAdapter(categorieList);
        recycleViewForHomeActivity.setAdapter(homeCardAdapter);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    public void networkError() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE).setTitleText("No Internet connected?").setContentText("Make sure your internet connection is working.").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                getActivity().finishAffinity();
            }
        }).show();
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

    public boolean permission() {
/*
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
*/

        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
/*
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(getActivity(), "Permission allowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Please allow permission", Toast.LENGTH_SHORT).show();
                }
            }
*/
        }
    }

    class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.ViewHolder> {

        ArrayList<Poster_HomeModel> itemList;

        HomeCardAdapter(ArrayList<Poster_HomeModel> items) {
            itemList = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_new_card_item_home, viewGroup, false);
            return new HomeCardAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeCardAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
            Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "font/cabin.ttf");
            viewHolder.txtMainHeading.setTypeface(custom_font);
            viewHolder.txtMainHeading.setText("#" + itemList.get(i).getTitle().trim());
            viewHolder.txtMainHeading.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.poster_anim_slide_up));

            PushDownAnim.setPushDownAnimTo(viewHolder.cardView);
            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Changed 22/05/2023 */
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        Dexter.withActivity(getActivity()).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    if (Poster_NetworkConnectivityReceiver.isNetConnected()) {
                                        String title = itemList.get(i).getTitle();

                                        int finalPOS = 0;

                                        for (int i = 0; i < posterDatas.size(); i++) {
                                            if (title.equals(posterDatas.get(i).getPOSTERCat_name())) {
                                                finalPOS = i;
                                            }
                                        }

                                        if (!isActive) {
                                            int finalPOS1 = finalPOS;
                                            new InterstitialAds().Show_Ads(getActivity(), new InterstitialAds.AdCloseListener() {
                                                @Override
                                                public void onAdClosed() {
                                                    Log.e("@@@", "1A");
                                                    Intent intent = new Intent(new Intent(getActivity(), Poster_ViewPagerActivity.class));
                                                    intent.putExtra("position", finalPOS1);
                                                    intent.putExtra("cat_id", Integer.parseInt(posterDatas.get(finalPOS1).getPOSTERCat_id()));
                                                    intent.putExtra("cateName", posterDatas.get(finalPOS1).getPOSTERCat_name());
                                                    intent.putExtra("ratio", 0);
                                                    startActivity(intent);
                                                }
                                            });
                                        } else {
                                            Intent intent = new Intent(new Intent(getActivity(), Poster_ViewPagerActivity.class));
                                            intent.putExtra("position", finalPOS);
                                            intent.putExtra("cat_id", Integer.parseInt(posterDatas.get(finalPOS).getPOSTERCat_id()));
                                            intent.putExtra("cateName", posterDatas.get(finalPOS).getPOSTERCat_name());
                                            intent.putExtra("ratio", 0);
                                            startActivity(intent);
                                        }

                                    } else {
                                        networkError();
                                    }

                                } else {
                                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(new PermissionRequestErrorListener() {
                            public void onError(DexterError dexterError) {
                                //  Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        }).onSameThread().check();
                    } else {
                        Dexter.withActivity(getActivity()).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    if (Poster_NetworkConnectivityReceiver.isNetConnected()) {
                                        String title = itemList.get(i).getTitle();

                                        int finalPOS = 0;

                                        for (int i = 0; i < posterDatas.size(); i++) {
                                            if (title.equals(posterDatas.get(i).getPOSTERCat_name())) {
                                                finalPOS = i;
                                            }
                                        }

                                        if (!isActive) {
                                            int finalPOS1 = finalPOS;
                                            new InterstitialAds().Show_Ads(getActivity(), new InterstitialAds.AdCloseListener() {
                                                @Override
                                                public void onAdClosed() {
                                                    Log.e("@@@", "1A");
                                                    Intent intent = new Intent(new Intent(getActivity(), Poster_ViewPagerActivity.class));
                                                    intent.putExtra("position", finalPOS1);
                                                    intent.putExtra("cat_id", Integer.parseInt(posterDatas.get(finalPOS1).getPOSTERCat_id()));
                                                    intent.putExtra("cateName", posterDatas.get(finalPOS1).getPOSTERCat_name());
                                                    intent.putExtra("ratio", 0);
                                                    startActivity(intent);
                                                }
                                            });
                                        } else {
                                            Intent intent = new Intent(new Intent(getActivity(), Poster_ViewPagerActivity.class));
                                            intent.putExtra("position", finalPOS);
                                            intent.putExtra("cat_id", Integer.parseInt(posterDatas.get(finalPOS).getPOSTERCat_id()));
                                            intent.putExtra("cateName", posterDatas.get(finalPOS).getPOSTERCat_name());
                                            intent.putExtra("ratio", 0);
                                            startActivity(intent);
                                        }

                                    } else {
                                        networkError();
                                    }

                                } else {
                                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(new PermissionRequestErrorListener() {
                            public void onError(DexterError dexterError) {
                                //  Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        }).onSameThread().check();
                    }
                }
            });

            Glide.with(activity).load(itemList.get(i).getIcon()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(viewHolder.iv_icon);

            switch (i % 14) {
                case 0:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_7_gradient);
                    break;
                case 1:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_10_gradient);
                    break;
                case 2:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_9_gradient);
                    break;
                case 3:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_5_gradient);
                    break;
                case 4:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_6_gradient);
                    break;
                case 5:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_14_gradient);
                    break;
                case 6:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_11_gradient);
                    break;
                case 7:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_12_gradient);
                    break;
                case 8:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_13_gradient);
                    break;
                case 9:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_14_gradient);
                    break;
                case 10:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_8_gradient);
                    break;
                case 11:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_green_gradient);
                    break;
                case 12:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_purple_gradient);
                    break;
                case 13:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_red_gradient);
                    break;
                default:
                    viewHolder.LLOut.setBackgroundResource(R.drawable.poster_drawable_red_gradient);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            if (itemList == null) {
                Toast.makeText(getActivity(), "Please re-open this app", Toast.LENGTH_SHORT).show();
                return 0;
            }
            return itemList.size();
        }


        private void updateData(ArrayList<Poster_HomeModel> datas, String text) {
            ArrayList<Poster_HomeModel> newNames = new ArrayList<>();

            if (text.length() == 0) {
                newNames.addAll(datas);
            } else {
                for (Poster_HomeModel name : datas) {
                    if (name.getTitle().toLowerCase().contains(text))
                        newNames.add(name);
                }
            }

            itemList = newNames;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            ImageView iv_icon;
            LinearLayout LLOut;
            TextView txtMainHeading;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtMainHeading = itemView.findViewById(R.id.txtMainHeading);
                cardView = itemView.findViewById(R.id.cardViewHome);
                iv_icon = itemView.findViewById(R.id.iv_icon);
                LLOut = itemView.findViewById(R.id.LLOut);
            }
        }
    }

    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        startActivityForResult(intent, 101);
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

}
