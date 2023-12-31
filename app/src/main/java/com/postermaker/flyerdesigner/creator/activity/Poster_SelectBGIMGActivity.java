package com.postermaker.flyerdesigner.creator.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
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
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCrop.Options;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.postermaker.flyerdesigner.creator.Poster_Application;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppConfigs;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Bg_Vertical_Adapter;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_OnClickSnapListener;
import com.postermaker.flyerdesigner.creator.fragments.Poster_Fragment_BG1;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Load_More_Listener;
import com.postermaker.flyerdesigner.creator.listener.Poster_RV_Load_More_Scroll;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_BG_Image;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Data_Provider;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Main_BG_Image;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Snap_Info;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Thumb_BG;

public class Poster_SelectBGIMGActivity extends AppCompatActivity implements OnClickListener, Poster_OnClickSnapListener {

    private List<WeakReference<Fragment>> mFragments = new ArrayList();
    ArrayList<Object> snapData = new ArrayList();
    ArrayList<Poster_Main_BG_Image> thumbnail_bg = new ArrayList();

    private static final int SELECT_PICTURE_FROM_CAMERA = 9062;
    private static final int SELECT_PICTURE_FROM_GALLERY = 9072;

    private static final String TAG = "BackgrounImageActivity";
    private RelativeLayout btnBack;
    private ImageView btnGalleryPicker, btnTakePicture;
    private int height, cnt = 0;
    private ProgressDialog dialogIs;
    private File f;
    private Poster_AppPreferenceClass appPreferenceClass;
    private String ratio;
    private Uri resultUri;
    private Poster_RV_Load_More_Scroll scrollListener;
    private int widht;
    private TextView txtTitle;
    private Poster_Bg_Vertical_Adapter veticalViewAdapter;
    Poster_Data_Provider dataProvider;

    LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;

    private void fetchAllBgImages() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Poster_AppConstants.BASE_URL_POSTER);
        stringBuilder.append("backgroundlatest");
        Poster_Application.getInstance().addToRequestQueue(new StringRequest(1, stringBuilder.toString(), new Listener<String>() {
            public void onResponse(String str) {
                RuntimeException e;
                try {
                    Poster_Thumb_BG thumbBG = new Gson().fromJson(str, Poster_Thumb_BG.class);
                    thumbnail_bg = thumbBG.getThumbnail_bg();
                    setBGImageAdapter();
                    return;
                } catch (NullPointerException e2) {
                    e = e2;
                } catch (JsonSyntaxException e3) {
                    e = e3;
                }
                e.printStackTrace();
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                String str = Poster_SelectBGIMGActivity.TAG;
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
                Poster_Application.getInstance().cancelPendingRequests(Poster_SelectBGIMGActivity.TAG);
                Poster_SelectBGIMGActivity.this.fetchAllBgImages();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> hashMap = new HashMap();
                hashMap.put("device", "1");
                return hashMap;
            }
        }, TAG);
    }

    public void createStickerDir() {
        appPreferenceClass = new Poster_AppPreferenceClass(this);
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
        Log.e(str, stringBuilder.toString());
    }

    private void SeeMoreData() {
        veticalViewAdapter.insertLoadingView();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                veticalViewAdapter.removeLoadingView();
                veticalViewAdapter.InsertData(Poster_SelectBGIMGActivity.this.dataProvider.getLoad_More_PosterItemsS());
                veticalViewAdapter.notifyDataSetChanged();
                scrollListener.set_Data_Loaded();
            }
        }, 3000);
    }

    @Override
    public void onCreate(Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        setContentView((int) R.layout.poster_new_activity_bg_images);

        Poster_AppConfigs.storeInt("flow", 1, this);
        findViewsWithID();

        mRecyclerView = findViewById(R.id.background_recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(this.mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        appPreferenceClass = new Poster_AppPreferenceClass(this);

        fetchAllBgImages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMemory();
    }

    public void releaseMemory() {
        try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Glide.get(Poster_SelectBGIMGActivity.this).clearDiskCache();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Glide.get(this).clearMemory();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private void setBGImageAdapter() {

        for (int i = 0; i < thumbnail_bg.size(); i++) {
            snapData.add(new Poster_Snap_Info(1, (thumbnail_bg.get(i)).getCategory_name(), (thumbnail_bg.get(i)).getCategory_list()));
        }

        dataProvider = new Poster_Data_Provider();
        dataProvider.apply_Poster_List(this.snapData);
        if (snapData != null) {
            veticalViewAdapter = new Poster_Bg_Vertical_Adapter(this, this.dataProvider.get_Load_Poster_Items(), this.mRecyclerView, 1);
            mRecyclerView.setAdapter(this.veticalViewAdapter);
            scrollListener = new Poster_RV_Load_More_Scroll(this.mLinearLayoutManager);
            scrollListener.set_Data_LoadMore_Listener(new Poster_On_Load_More_Listener() {
                public void onLoadMore() {
                    Poster_SelectBGIMGActivity.this.SeeMoreData();
                }
            });
            mRecyclerView.addOnScrollListener(this.scrollListener);
        }
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        StringBuilder stringBuilder;
        Uri fromFile;
        int i3;
        int i4 = i;
        int i5 = i2;
        super.onActivityResult(i, i2, intent);

        if (i == 2000) {
/*
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(getApplicationContext(), "Permission allowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow permission", Toast.LENGTH_SHORT).show();
                }
            }
*/
        }

        if (i5 == -1 && i4 == SELECT_PICTURE_FROM_GALLERY) {
            try {
                stringBuilder = new StringBuilder();
                stringBuilder.append("SampleCropImage");
                stringBuilder.append(System.currentTimeMillis());
                stringBuilder.append(".png");
                fromFile = Uri.fromFile(new File(getCacheDir(), stringBuilder.toString()));
                Options options = new Options();
                options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                options.setAspectRatioOptions(1, new AspectRatio("1:1", 1.0f, 1.0f), new AspectRatio("3:2", 3.0f, 2.0f), new AspectRatio("2:3", 2.0f, 3.0f), new AspectRatio("4:3", 4.0f, 3.0f), new AspectRatio("3:4", 3.0f, 4.0f), new AspectRatio("16:9", 16.0f, 9.0f), new AspectRatio("5:4", 5.0f, 4.0f), new AspectRatio("4:5", 4.0f, 5.0f));
                UCrop.of(intent.getData(), fromFile).withOptions(options).start(this);
                i3 = -1;
            } catch (Exception e) {
                e.printStackTrace();
                i3 = -1;
            }
        } else {
            i3 = -1;
        }
        if (i5 == i3) {
            if (i4 == SELECT_PICTURE_FROM_CAMERA) {
                try {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("SampleCropImage");
                    stringBuilder.append(System.currentTimeMillis());
                    stringBuilder.append(".png");
                    fromFile = Uri.fromFile(new File(getCacheDir(), stringBuilder.toString()));
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("====");
                    stringBuilder2.append(fromFile.getPath());
                    Log.e("downaload", stringBuilder2.toString());
                    Options options2 = new Options();
                    options2.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                    options2.setAspectRatioOptions(1, new AspectRatio("1:1", 1.0f, 1.0f), new AspectRatio("3:2", 3.0f, 2.0f), new AspectRatio("2:3", 2.0f, 3.0f), new AspectRatio("4:3", 4.0f, 3.0f), new AspectRatio("3:4", 3.0f, 4.0f), new AspectRatio("16:9", 16.0f, 9.0f), new AspectRatio("5:4", 5.0f, 4.0f), new AspectRatio("4:5", 4.0f, 5.0f));
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(getApplicationContext().getPackageName());
                    stringBuilder3.append(".provider");
                    UCrop.of(FileProvider.getUriForFile(this, stringBuilder3.toString(), this.f), fromFile).withOptions(options2).start(this);
                    i3 = -1;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    i3 = -1;
                }
            } else {
                i3 = -1;
            }
        }
        if (i5 == i3 && i4 == 69) {
            adjustCropResult(intent);
            return;
        }
        Intent intent2 = intent;
        if (i5 == 96) {
            UCrop.getError(intent);
        }
    }


    private void findViewsWithID() {
        btnBack = findViewById(R.id.btn_back);
        txtTitle = findViewById(R.id.txtTitle);
        btnGalleryPicker = findViewById(R.id.btnGalleryPicker);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        txtTitle.setText("Background");
        btnBack.setOnClickListener(this);
        btnGalleryPicker.setOnClickListener(this);
        btnTakePicture.setOnClickListener(this);
    }

    private void adjustCropResult(@NonNull Intent intent) {
        this.resultUri = UCrop.getOutput(intent);
        if (this.resultUri != null) {
            this.widht = UCrop.getOutputImageWidth(intent);
            this.height = UCrop.getOutputImageHeight(intent);
            int i = this.widht;
            int i2 = this.height;
            int gcd = gcd(i, i2);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i / gcd);
            stringBuilder.append(":");
            stringBuilder.append(i2 / gcd);
            stringBuilder.append(":");
            stringBuilder.append(i);
            stringBuilder.append(":");
            stringBuilder.append(i2);
            this.ratio = stringBuilder.toString();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("===");
            stringBuilder2.append(this.ratio);
            Log.e("Ratio", stringBuilder2.toString());
            try {
                showInterstialAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnGalleryPicker) {
            requestGalleryPermission();
        } else if (id == R.id.btnTakePicture) {
            requestInternalStoragePermission();
        } else if (id == R.id.btn_back) {
            onBackPressed();
        }
    }

    public boolean permission() {
/*
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
*/

        return false;
    }


    private void requestGalleryPermission() {
        /* Changed 22/05/2023 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_SelectBGIMGActivity.this.createStickerDir();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction("android.intent.action.GET_CONTENT");
                        Poster_SelectBGIMGActivity bGImageActivity = Poster_SelectBGIMGActivity.this;
                        bGImageActivity.startActivityForResult(Intent.createChooser(intent, bGImageActivity.getString(R.string.select_picture)), Poster_SelectBGIMGActivity.SELECT_PICTURE_FROM_GALLERY);
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        Poster_SelectBGIMGActivity.this.displaySettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(Poster_SelectBGIMGActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        } else {
            Dexter.withActivity(this).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_SelectBGIMGActivity.this.createStickerDir();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction("android.intent.action.GET_CONTENT");
                        Poster_SelectBGIMGActivity bGImageActivity = Poster_SelectBGIMGActivity.this;
                        bGImageActivity.startActivityForResult(Intent.createChooser(intent, bGImageActivity.getString(R.string.select_picture)), Poster_SelectBGIMGActivity.SELECT_PICTURE_FROM_GALLERY);
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        Poster_SelectBGIMGActivity.this.displaySettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(Poster_SelectBGIMGActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        }
    }


    private void requestInternalStoragePermission() {
        /* Changed 22/05/2023 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_SelectBGIMGActivity.this.createStickerDir();
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        Poster_SelectBGIMGActivity.this.f = new File(Environment.getExternalStorageDirectory(), ".temp.jpg");
                        Context context = Poster_SelectBGIMGActivity.this;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(Poster_SelectBGIMGActivity.this.getApplicationContext().getPackageName());
                        stringBuilder.append(".provider");
                        intent.putExtra("output", FileProvider.getUriForFile(context, stringBuilder.toString(), Poster_SelectBGIMGActivity.this.f));
                        startActivityForResult(intent, Poster_SelectBGIMGActivity.SELECT_PICTURE_FROM_CAMERA);
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        displaySettingsDialog();
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
            Dexter.withActivity(this).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_SelectBGIMGActivity.this.createStickerDir();
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        Poster_SelectBGIMGActivity.this.f = new File(Environment.getExternalStorageDirectory(), ".temp.jpg");
                        Context context = Poster_SelectBGIMGActivity.this;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(Poster_SelectBGIMGActivity.this.getApplicationContext().getPackageName());
                        stringBuilder.append(".provider");
                        intent.putExtra("output", FileProvider.getUriForFile(context, stringBuilder.toString(), Poster_SelectBGIMGActivity.this.f));
                        startActivityForResult(intent, Poster_SelectBGIMGActivity.SELECT_PICTURE_FROM_CAMERA);
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        displaySettingsDialog();
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

    private int gcd(int i, int i2) {
        return i2 == 0 ? i : gcd(i2, i % i2);
    }

    public void showInterstialAd() {

        if (this.resultUri != null) {
            new InterstitialAds().Show_Ads(Poster_SelectBGIMGActivity.this, new InterstitialAds.AdCloseListener() {
                @Override
                public void onAdClosed() {
                    Intent intent = new Intent(Poster_SelectBGIMGActivity.this, PosterMakerActivity.class);
                    intent.putExtra("ratio", ratio);
                    intent.putExtra("loadUserFrame", true);
                    intent.putExtra(Scopes.PROFILE, resultUri.toString());
                    intent.putExtra("hex", "");
                    startActivity(intent);
                }
            });
        } else {
            Toast.makeText(this, "Image Not Retrived", Toast.LENGTH_SHORT).show();
        }
    }

    private void displaySettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Poster_SelectBGIMGActivity.this.trySettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void trySettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 101);
    }


    public void onClickSnapFilter(int i, int i2, String str, String str2) {
        requestpmmakerStoragePermission(i, i2, str, str2);
    }


    private void requestpmmakerStoragePermission(final int i, final int i2, final String str, String str2) {
        /* Changed 22/05/2023 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {

                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    IOException e;
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_SelectBGIMGActivity.this.createStickerDir();
                        String string = Poster_SelectBGIMGActivity.this.appPreferenceClass.getString(Poster_AppConstants.jsonData);
                        if (string != null) {
                            string.equals("");
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("===");
                        stringBuilder.append(i);
                        Log.e("position", stringBuilder.toString());
                        try {
                            Uri fromFile;
                            Uri uri = null;
                            if (str.equals("null")) {
                                Bitmap decodeResource = BitmapFactory.decodeResource(Poster_SelectBGIMGActivity.this.getResources(), 1);
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(Poster_SelectBGIMGActivity.this.appPreferenceClass.getString(Poster_AppConstants.sdcardPath));
                                stringBuilder2.append("/bg");
                                stringBuilder2.append(i2);
                                stringBuilder2.append("/");
                                File file = new File(stringBuilder2.toString());
                                File file2 = new File(file, String.valueOf(i));
                                if (file2.exists()) {
                                    fromFile = Uri.fromFile(file2);
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("===");
                                    stringBuilder3.append(fromFile);
                                    Log.e("url", stringBuilder3.toString());
                                    uri = fromFile;
                                } else {
                                    FileOutputStream fileOutputStream;
                                    try {
                                        if (!file.exists()) {
                                            file.mkdirs();
                                        }
                                        fileOutputStream = new FileOutputStream(file2);
                                        try {
                                            decodeResource.compress(CompressFormat.JPEG, 100, fileOutputStream);
                                            fileOutputStream.close();
                                            uri = Uri.fromFile(file2);
                                        } catch (IOException e2) {
                                            e = e2;
                                            if (fileOutputStream != null) {
                                            }
                                            if (uri != null) {
                                            }
                                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                            }
                                        }
                                    } catch (IOException e3) {
                                        e = e3;
                                        fileOutputStream = null;
                                        if (fileOutputStream != null) {
                                            try {
                                                fileOutputStream.close();
                                            } catch (IOException e4) {
                                                e4.printStackTrace();
                                            }
                                        }
                                        if (uri != null) {
                                        }
                                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        }
                                    }
                                }
                            } else {
                                performNextTask(str);
                            }
                            if (uri != null) {
                                StringBuilder stringBuilder4 = new StringBuilder();
                                stringBuilder4.append("SampleCropImage");
                                stringBuilder4.append(System.currentTimeMillis());
                                stringBuilder4.append(".png");
                                fromFile = Uri.fromFile(new File(Poster_SelectBGIMGActivity.this.getCacheDir(), stringBuilder4.toString()));
                                Options options = new Options();
                                options.setToolbarColor(Poster_SelectBGIMGActivity.this.getResources().getColor(R.color.colorPrimary));
                                options.setAspectRatioOptions(2, new AspectRatio("1:1", 1.0f, 1.0f), new AspectRatio("3:2", 3.0f, 2.0f), new AspectRatio("2:3", 2.0f, 3.0f), new AspectRatio("4:3", 4.0f, 3.0f), new AspectRatio("3:4", 3.0f, 4.0f), new AspectRatio("16:9", 16.0f, 9.0f), new AspectRatio("5:4", 5.0f, 4.0f), new AspectRatio("4:5", 4.0f, 5.0f));
                                UCrop.of(uri, fromFile).withOptions(options).start(Poster_SelectBGIMGActivity.this);
                            }
                        } catch (Exception e5) {
                            e5.printStackTrace();
                        }
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        Poster_SelectBGIMGActivity.this.displaySettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(Poster_SelectBGIMGActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        } else {
            Dexter.withActivity(this).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO").withListener(new MultiplePermissionsListener() {

                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    IOException e;
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_SelectBGIMGActivity.this.createStickerDir();
                        String string = Poster_SelectBGIMGActivity.this.appPreferenceClass.getString(Poster_AppConstants.jsonData);
                        if (string != null) {
                            string.equals("");
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("===");
                        stringBuilder.append(i);
                        Log.e("position", stringBuilder.toString());
                        try {
                            Uri fromFile;
                            Uri uri = null;
                            if (str.equals("null")) {
                                Bitmap decodeResource = BitmapFactory.decodeResource(Poster_SelectBGIMGActivity.this.getResources(), 1);
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(Poster_SelectBGIMGActivity.this.appPreferenceClass.getString(Poster_AppConstants.sdcardPath));
                                stringBuilder2.append("/bg");
                                stringBuilder2.append(i2);
                                stringBuilder2.append("/");
                                File file = new File(stringBuilder2.toString());
                                File file2 = new File(file, String.valueOf(i));
                                if (file2.exists()) {
                                    fromFile = Uri.fromFile(file2);
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("===");
                                    stringBuilder3.append(fromFile);
                                    Log.e("url", stringBuilder3.toString());
                                    uri = fromFile;
                                } else {
                                    FileOutputStream fileOutputStream;
                                    try {
                                        if (!file.exists()) {
                                            file.mkdirs();
                                        }
                                        fileOutputStream = new FileOutputStream(file2);
                                        try {
                                            decodeResource.compress(CompressFormat.JPEG, 100, fileOutputStream);
                                            fileOutputStream.close();
                                            uri = Uri.fromFile(file2);
                                        } catch (IOException e2) {
                                            e = e2;
                                            if (fileOutputStream != null) {
                                            }
                                            if (uri != null) {
                                            }
                                            if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                            }
                                        }
                                    } catch (IOException e3) {
                                        e = e3;
                                        fileOutputStream = null;
                                        if (fileOutputStream != null) {
                                            try {
                                                fileOutputStream.close();
                                            } catch (IOException e4) {
                                                e4.printStackTrace();
                                            }
                                        }
                                        if (uri != null) {
                                        }
                                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        }
                                    }
                                }
                            } else {
                                performNextTask(str);
                            }
                            if (uri != null) {
                                StringBuilder stringBuilder4 = new StringBuilder();
                                stringBuilder4.append("SampleCropImage");
                                stringBuilder4.append(System.currentTimeMillis());
                                stringBuilder4.append(".png");
                                fromFile = Uri.fromFile(new File(Poster_SelectBGIMGActivity.this.getCacheDir(), stringBuilder4.toString()));
                                Options options = new Options();
                                options.setToolbarColor(Poster_SelectBGIMGActivity.this.getResources().getColor(R.color.colorPrimary));
                                options.setAspectRatioOptions(2, new AspectRatio("1:1", 1.0f, 1.0f), new AspectRatio("3:2", 3.0f, 2.0f), new AspectRatio("2:3", 2.0f, 3.0f), new AspectRatio("4:3", 4.0f, 3.0f), new AspectRatio("3:4", 3.0f, 4.0f), new AspectRatio("16:9", 16.0f, 9.0f), new AspectRatio("5:4", 5.0f, 4.0f), new AspectRatio("4:5", 4.0f, 5.0f));
                                UCrop.of(uri, fromFile).withOptions(options).start(Poster_SelectBGIMGActivity.this);
                            }
                        } catch (Exception e5) {
                            e5.printStackTrace();
                        }
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        Poster_SelectBGIMGActivity.this.displaySettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(Poster_SelectBGIMGActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        }
    }

    public File collectCacheFolder(Context context) {
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

    public void itemClickSeeMoreAdapter(ArrayList<Poster_BG_Image> arrayList, String str) {
        seeMoreCollection(arrayList);
    }

    private void seeMoreCollection(ArrayList<Poster_BG_Image> arrayList) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        Poster_Fragment_BG1 fragmentBG1 = (Poster_Fragment_BG1) supportFragmentManager.findFragmentByTag("back_category_frgm");
        if (fragmentBG1 != null) {
            beginTransaction.remove(fragmentBG1);
        }
        Fragment newInstance = Poster_Fragment_BG1.newInstance(arrayList);
        this.mFragments.add(new WeakReference(newInstance));
        beginTransaction.add(R.id.frameContainerBackground, newInstance, "back_category_frgm");
        beginTransaction.addToBackStack("back_category_frgm");
        try {
            beginTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performNextTask(String str) {
        dialogIs = new ProgressDialog(this);
        dialogIs.setMessage(getResources().getString(R.string.plzwait));
        dialogIs.setCancelable(false);
        dialogIs.show();
        final File cacheFolder = collectCacheFolder(this);
        Poster_Application.getInstance().addToRequestQueue(new ImageRequest(str, new Listener<Bitmap>() {
            public void onResponse(Bitmap bitmap) {
                try {
                    dialogIs.dismiss();
                } catch (Throwable e) {
                    try {
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return;
                    }
                }
                try {
                    File file = new File(cacheFolder, "localFileName.png");
                    OutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    try {
                        Uri fromFile = Uri.fromFile(file);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("SampleCropImage");
                        stringBuilder.append(System.currentTimeMillis());
                        stringBuilder.append(".png");
                        Uri fromFile2 = Uri.fromFile(new File(getCacheDir(), stringBuilder.toString()));
                        Options options = new Options();
                        options.setToolbarColor(Poster_SelectBGIMGActivity.this.getResources().getColor(R.color.colorPrimary));
                        options.setAspectRatioOptions(2, new AspectRatio("1:1", 1.0f, 1.0f), new AspectRatio("3:2", 3.0f, 2.0f), new AspectRatio("2:3", 2.0f, 3.0f), new AspectRatio("4:3", 4.0f, 3.0f), new AspectRatio("3:4", 3.0f, 4.0f), new AspectRatio("16:9", 16.0f, 9.0f), new AspectRatio("5:4", 5.0f, 4.0f), new AspectRatio("4:5", 4.0f, 5.0f));
                        UCrop.of(fromFile, fromFile2).withOptions(options).start(Poster_SelectBGIMGActivity.this);
                    } catch (NullPointerException e3) {
                        e3.printStackTrace();
                    }
                } catch (FileNotFoundException e4) {
                    e4.printStackTrace();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
        }, 0, 0, null, new ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    dialogIs.dismiss();
                } catch (Throwable e) {
                }
            }
        }));
    }
}


