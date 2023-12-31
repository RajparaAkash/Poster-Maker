package com.postermaker.flyerdesigner.creator.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppConfigs;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Design_Adapter;
import com.postermaker.flyerdesigner.creator.handler.Poster_DB_Handler;
import com.postermaker.flyerdesigner.creator.handler.Poster_Template_InfoData;
import com.postermaker.flyerdesigner.creator.imageloader.Poster_Glide_Image_Utils;

public class Poster_MyDesignActivity extends Poster_ShapeActivity {

    public ArrayList<Poster_Template_InfoData> templateList = new ArrayList();

    public Poster_Design_Adapter posterDesignAdapter;
    public GridView gridView;
    private RelativeLayout imagBack;
    LordDataAsync loadDataAsync = null;
    ProgressBar progress_bar;
    public int spoisiton, heightItemGrid = 50, widthItemGrid = 50;
    private TextView txtTitle, txt_dialog;

    String catName = "MY_TEMP";

    @Override
    public void onBackPressed() {
        new InterstitialAds().Show_Ads(Poster_MyDesignActivity.this, new InterstitialAds.AdCloseListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        requestWindowFeature(1);
        setContentView((int) R.layout.poster_new_activity_my_design);

        Poster_AppConfigs.storeInt("flow", 1, this);
        this.appPreferenceClass = new Poster_AppPreferenceClass(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.widthItemGrid = ((int) ((float) (displayMetrics.widthPixels - Poster_Glide_Image_Utils.convertDpToPx(this, 10.0f)))) / 2;
        this.heightItemGrid = ((int) ((float) (displayMetrics.heightPixels - Poster_Glide_Image_Utils.convertDpToPx(this, 10.0f)))) / 2;

        this.txtTitle = findViewById(R.id.txtTitle);
        this.txtTitle.setTypeface(adjustFontBold());
        this.imagBack = findViewById(R.id.btn_back);
        this.imagBack.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        this.gridView = findViewById(R.id.gridview);
        this.progress_bar = findViewById(R.id.progress_bar);
        this.progress_bar.setVisibility(View.GONE);
        this.txt_dialog = findViewById(R.id.txt_dialog);
        requestIOStoragePermission();

        this.gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Poster_MyDesignActivity.this.spoisiton = i;

                new InterstitialAds().Show_Ads(Poster_MyDesignActivity.this, new InterstitialAds.AdCloseListener() {
                    @Override
                    public void onAdClosed() {
                        Intent intent = new Intent(Poster_MyDesignActivity.this, PosterMakerActivity.class);
                        intent.putExtra("position", Poster_MyDesignActivity.this.spoisiton);
                        intent.putExtra("loadUserFrame", false);
                        intent.putExtra("Temp_Type", Poster_MyDesignActivity.this.catName);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    public class LordDataAsync extends AsyncTask<String, Void, String> {

        @Override
        public void onPreExecute() {
            Poster_MyDesignActivity.this.progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        public String doInBackground(String... strArr) {
            try {
                Poster_MyDesignActivity.this.templateList.clear();
                Poster_DB_Handler dbHandler = Poster_DB_Handler.getDatabaseHandler(Poster_MyDesignActivity.this);
                if (Poster_MyDesignActivity.this.catName.equals("MY_TEMP")) {
                    Poster_MyDesignActivity.this.templateList = dbHandler.find_Template_List_Des("USER");
                }
                dbHandler.close();
            } catch (NullPointerException unused) {
            }
            return "yes";
        }

        @Override
        public void onPostExecute(String str) {
            try {
                Poster_MyDesignActivity.this.progress_bar.setVisibility(View.GONE);
                if (Poster_MyDesignActivity.this.templateList.size() != 0) {
                    Poster_MyDesignActivity.this.posterDesignAdapter = new Poster_Design_Adapter(Poster_MyDesignActivity.this, Poster_MyDesignActivity.this.templateList, Poster_MyDesignActivity.this.catName, Poster_MyDesignActivity.this.widthItemGrid);
                    Poster_MyDesignActivity.this.gridView.setAdapter(Poster_MyDesignActivity.this.posterDesignAdapter);
                }
                if (Poster_MyDesignActivity.this.catName.equals("MY_TEMP")) {
                    if (Poster_MyDesignActivity.this.templateList.size() == 0) {
                        Poster_MyDesignActivity.this.txt_dialog.setText(Poster_MyDesignActivity.this.getResources().getString(R.string.NoDesigns));
                    } else if (Poster_MyDesignActivity.this.templateList.size() <= 4) {
                        Poster_MyDesignActivity.this.txt_dialog.setText(Poster_MyDesignActivity.this.getResources().getString(R.string.DesignOptionsInstruction));
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
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

    private void requestIOStoragePermission() {
        /* Changed 22/05/2023 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_MyDesignActivity.this.makeStickerDir();
                        Poster_MyDesignActivity myDesignActivity = Poster_MyDesignActivity.this;
                        myDesignActivity.loadDataAsync = new LordDataAsync();
                        Poster_MyDesignActivity.this.loadDataAsync.execute(new String[]{""});
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        Poster_MyDesignActivity.this.displaySettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(Poster_MyDesignActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        } else {
            Dexter.withActivity(this).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Poster_MyDesignActivity.this.makeStickerDir();
                        Poster_MyDesignActivity myDesignActivity = Poster_MyDesignActivity.this;
                        myDesignActivity.loadDataAsync = new LordDataAsync();
                        Poster_MyDesignActivity.this.loadDataAsync.execute(new String[]{""});
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        Poster_MyDesignActivity.this.displaySettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(Poster_MyDesignActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        }
    }

    public void displaySettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Poster_MyDesignActivity.this.viewSettings();
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
}
