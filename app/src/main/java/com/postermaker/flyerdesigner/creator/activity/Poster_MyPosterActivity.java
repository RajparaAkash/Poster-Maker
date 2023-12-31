package com.postermaker.flyerdesigner.creator.activity;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Work_Adapter;
import com.postermaker.flyerdesigner.creator.imageloader.Poster_Glide_Image_Utils;
import com.postermaker.flyerdesigner.creator.listener.Poster_On_Click_Callback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Poster_MyPosterActivity extends Poster_ShapeActivity {

    private static final String TAG = "MyPosterActivity";

    public static File[] listFile;
    private RelativeLayout btn_back, rel_text;
    public Context context;
    public GridView imagegrid;
    private TextView txtTitle, no_image;
    public int spostion, screenWidth, count = 0;

    public Poster_Work_Adapter imageAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView((int) R.layout.poster_new_activity_work_poster);

        AdjustFontBold((ViewGroup) findViewById(16908290));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        this.screenWidth = displayMetrics.widthPixels - Poster_Glide_Image_Utils.convertDpToPx(this, 10.0f);
        this.no_image = findViewById(R.id.no_image);
        this.rel_text = findViewById(R.id.rel_text);
        this.txtTitle = findViewById(R.id.txtTitle);
        this.btn_back = findViewById(R.id.btn_back);
        this.txtTitle.setTypeface(adjustFontBold());
        this.no_image.setTypeface(adjustFontBold());
        this.btn_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                new InterstitialAds().Show_Ads(Poster_MyPosterActivity.this, new InterstitialAds.AdCloseListener() {
                    @Override
                    public void onAdClosed() {
                        finish();
                    }
                });
            }
        });

        imagegrid = findViewById(R.id.gridView);
        requestIOStoragePermission();
        this.imagegrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {

                new InterstitialAds().Show_Ads(Poster_MyPosterActivity.this, new InterstitialAds.AdCloseListener() {
                    @Override
                    public void onAdClosed() {
                        Poster_MyPosterActivity.this.spostion = i;
                        Intent intent = new Intent(Poster_MyPosterActivity.this, Poster_PreviewActivity.class);
                        intent.putExtra("uri", Poster_MyPosterActivity.listFile[i].getAbsolutePath());
                        intent.putExtra("way", "Gallery");
                        Poster_MyPosterActivity.this.startActivity(intent);
                    }
                });

            }
        });
    }


    public boolean permission() {
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestIOStoragePermission() {
        /* Changed 22/05/2023 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_MyPosterActivity.this.extractImageAndView();
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        Poster_MyPosterActivity.this.displaySettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(Poster_MyPosterActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        } else {
            Dexter.withActivity(this).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_MyPosterActivity.this.extractImageAndView();
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        Poster_MyPosterActivity.this.displaySettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(Poster_MyPosterActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        }
    }

    public void extractImageAndView() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.plzwait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Poster_MyPosterActivity.this.collectDataFromSdcard();
                    Poster_MyPosterActivity.this.context = Poster_MyPosterActivity.this;
                    if (Poster_MyPosterActivity.listFile != null) {
                        imageAdapter = new Poster_Work_Adapter(getApplicationContext(), Poster_MyPosterActivity.listFile, Poster_MyPosterActivity.this.screenWidth);
                        imageAdapter.setItemClickCallback(new Poster_On_Click_Callback<ArrayList<String>, Integer, String, Context>() {
                            public void onClickCallBack(ArrayList<String> arrayList, Integer num, String str, Context context) {
                                displayOptionsDialog(num);
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    String str = Poster_MyPosterActivity.TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("run: ");
                    stringBuilder.append(e);
                    Log.e(str, stringBuilder.toString());
                }
                progressDialog.dismiss();
            }
        }).start();
        progressDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                Poster_MyPosterActivity.this.imagegrid.setAdapter(Poster_MyPosterActivity.this.imageAdapter);
                if (Poster_MyPosterActivity.this.count == 0) {
                    Poster_MyPosterActivity.this.rel_text.setVisibility(View.VISIBLE);
                } else {
                    Poster_MyPosterActivity.this.rel_text.setVisibility(View.GONE);
                }
            }
        });
    }

    public void displaySettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Poster_MyPosterActivity.this.viewSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public void viewSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 101);
    }

    public void displayOptionsDialog(final int i) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.setContentView(R.layout.poster_layout_delete_dialog);
        dialog.setCancelable(false);
        TextView textView = dialog.findViewById(R.id.txtDescription);
        Button button = dialog.findViewById(R.id.btnDelete);
        Button button2 = dialog.findViewById(R.id.btnCancel);
        ((TextView) dialog.findViewById(R.id.txtTitle)).setTypeface(adjustFontBold());
        textView.setTypeface(applyFontNormal());
        button.setTypeface(adjustFontBold());
        button2.setTypeface(adjustFontBold());
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (deleteTempFile(Uri.parse(listFile[i].getAbsolutePath()))) {
                    extractImageAndView();
                    dialog.dismiss();
                }
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public boolean deleteTempFile(Uri uri) {
        boolean z = false;
        try {
            File file = new File(uri.getPath());
            z = file.delete();
            if (file.exists()) {
                try {
                    z = file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    z = getApplicationContext().deleteFile(file.getName());
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getApplicationContext().getPackageName());
            stringBuilder.append(".provider");
            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", FileProvider.getUriForFile(this, stringBuilder.toString(), file)));
        } catch (Exception unused) {
            Log.e(TAG, "deleteTempFile: ");
        }
        return z;
    }

    public void collectDataFromSdcard() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
        stringBuilder.append("/Poster Design");
        File file = new File(stringBuilder.toString());
        if (file.isDirectory()) {
            listFile = file.listFiles();
            File[] fileArr = listFile;
            this.count = fileArr.length;
            Arrays.sort(fileArr, new Comparator<File>() {
                public int compare(File file, File file2) {
                    if (SDK_INT >= 19) {
                        return (file2.lastModified() > file.lastModified() ? 1 : (file2.lastModified() == file.lastModified() ? 0 : -1));
                    }
                    return (file2.lastModified() > file.lastModified() ? 1 : (file2.lastModified() == file.lastModified() ? 0 : -1));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        new InterstitialAds().Show_Ads(Poster_MyPosterActivity.this, new InterstitialAds.AdCloseListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }
}
