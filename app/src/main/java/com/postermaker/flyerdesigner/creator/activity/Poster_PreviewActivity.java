package com.postermaker.flyerdesigner.creator.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat.IntentBuilder;
import androidx.core.content.FileProvider;
import androidx.print.PrintHelper;

import com.android.billingclient.api.Purchase;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.postermaker.flyerdesigner.creator.ads.NativeAds;
import com.postermaker.flyerdesigner.creator.billing.Poster_BillingUpdatesListener;
import com.postermaker.flyerdesigner.creator.billing.Poster_SubscriptionsUtil;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class Poster_PreviewActivity extends AppCompatActivity implements OnClickListener, RatingDialogListener, Poster_BillingUpdatesListener {

    private static final String TAG = "PreviewActivity";
    private static final String PLAY_CONSTANT = "http://play.google.com/store/apps/details?id=";
    private static final String MARKET_CONSTANT = "market://details?id=";

    private int REQUEST_FOR_GOOGLE_PLUS = 0;

    public Poster_AppPreferenceClass appPreference;
    private RelativeLayout btnBack, removeWaterMark;
    private ImageView iVPrint, iVHome, imageView, btnSharewMessanger, btnShareMoreImage, btnShareTwitter, btnShareWhatsapp, btnShareIntagram, btnShareHike, btnShareGooglePlus, btnMoreApp, btnShareFacebook;
    private TextView txtToolbar;
    public Typeface typefaceTextNormal, typefaceTextBold;

    private String oldpath;
    public Uri phototUri = null;
    public File pictureFile;
    ViewGroup root;

    MediaPlayer mediaPlayer;

    SharedPreferences preferences;

    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

/*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
*/

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        setContentView(R.layout.poster_new_activity_share_image);

        this.root = findViewById(R.id.main);
        this.appPreference = new Poster_AppPreferenceClass(this);

        preferences = getSharedPreferences("mysession", MODE_PRIVATE);

        this.typefaceTextBold = Typeface.createFromAsset(getAssets(), "font/Montserrat-SemiBold.ttf");
        this.typefaceTextNormal = Typeface.createFromAsset(getAssets(), "font/Montserrat-Medium.ttf");

        findViewByID();

        if (getIntent().getExtras().getString("way").equals("Gallery")) {
            this.removeWaterMark.setVisibility(View.GONE);
        }

        // adView = new AdView(this);

        //  decorView = getWindow().getDecorView();

//        initAppBilling();

        initialization();

        exportBitmap();

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.poster_rating);

            if (this.appPreference.getInt(Poster_AppConstants.isRated, 0) == 0) {
                mediaPlayer.start();
                customRateDialog();
                //  ratingdialog();
            } else {
                //  AdHelper.showInterstitial(getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

        if (mediaPlayer != null)
            mediaPlayer.pause();

//        if (mBillingManager != null) {
//            mBillingManager.destroy();
//        }
        super.onDestroy();
    }

    public void onNegativeButtonClicked() {
    }


    public void onNeutralButtonClicked() {
    }

    public Typeface applyBoldFont() {
        return this.typefaceTextBold;
    }

    private void findViewByID() {
        btnBack = findViewById(R.id.btn_back);
        txtToolbar = findViewById(R.id.txt_toolbar);

        Typeface custom_title = Typeface.createFromAsset(getAssets(), "font/cabin.ttf");
        txtToolbar.setTypeface(custom_title);

        iVHome = findViewById(R.id.iVHome);
        iVPrint = findViewById(R.id.iVPrint);

        btnShareFacebook = findViewById(R.id.btnShareFacebook);
        btnShareIntagram = findViewById(R.id.btnShareIntagram);
        btnShareWhatsapp = findViewById(R.id.btnShareWhatsapp);
        btnShareGooglePlus = findViewById(R.id.btnShareGooglePlus);
        btnSharewMessanger = findViewById(R.id.btnSharewMessanger);
        btnShareTwitter = findViewById(R.id.btnShareTwitter);
        btnShareHike = findViewById(R.id.btnShareHike);
        btnShareMoreImage = findViewById(R.id.btnShareMoreImage);

        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnShareFacebook);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnShareIntagram);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnShareWhatsapp);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnShareGooglePlus);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnSharewMessanger);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnShareTwitter);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnShareHike);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnShareMoreImage);

        btnBack.setOnClickListener(this);
        iVPrint.setOnClickListener(this);
        iVHome.setOnClickListener(this);
        btnShareFacebook.setOnClickListener(this);
        btnShareIntagram.setOnClickListener(this);
        btnShareWhatsapp.setOnClickListener(this);
        btnShareGooglePlus.setOnClickListener(this);
        btnSharewMessanger.setOnClickListener(this);
        btnShareTwitter.setOnClickListener(this);
        btnShareHike.setOnClickListener(this);
        btnShareMoreImage.setOnClickListener(this);
        txtToolbar.setTypeface(applyBoldFont());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iVPrint) {
           /* StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://play.google.com/store/apps/details?id=");
            stringBuilder.append(getPackageName());
            moreAppClick(stringBuilder.toString());
     */
            new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                @Override
                public void onAdClosed() {
                    doPhotoPrint();
                }
            });
        } else if (id == R.id.btn_back) {

            new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                @Override
                public void onAdClosed() {
                    finish();
                }
            });
        } else {
            switch (id) {
                case R.id.btnShareFacebook:

                    new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                        @Override
                        public void onAdClosed() {
                            shareWithFacebook(pictureFile.getPath());
                        }
                    });
                    return;
                case R.id.btnShareGooglePlus:

                    new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                        @Override
                        public void onAdClosed() {
                            shareWithGooglePlus(pictureFile.getPath());
                        }
                    });
                    return;
                case R.id.btnShareHike:

                    new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                        @Override
                        public void onAdClosed() {
                            shareWithHike(pictureFile.getPath());
                        }
                    });
                    return;
                case R.id.btnShareIntagram:

                    new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                        @Override
                        public void onAdClosed() {
                            sendOnInstagram(pictureFile.getPath());
                        }
                    });
                    return;
                case R.id.iVHome:

                    new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                        @Override
                        public void onAdClosed() {
                            Intent frameCategory = new Intent(Poster_PreviewActivity.this, Poster_MainActivity.class);
                            frameCategory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            frameCategory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(frameCategory);
                            finish();
                        }
                    });
                    return;

                case R.id.btnShareMoreImage:

                    new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                        @Override
                        public void onAdClosed() {
                            shareImageALL(pictureFile.getPath());
                        }
                    });
                    return;
                case R.id.btnShareTwitter:

                    new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                        @Override
                        public void onAdClosed() {
                            shareWithTwitter(pictureFile.getPath());
                        }
                    });
                    return;
                case R.id.btnShareWhatsapp:

                    new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
                        @Override
                        public void onAdClosed() {
                            shareToWhatsapp(pictureFile.getPath());
                        }
                    });
                    return;
                case R.id.btnSharewMessanger:
                    shareOnMessanger(this.pictureFile.getPath());
                    return;
                default:
                    toSocialMediashare();
            }
        }
    }

    public void initialization() {
        this.imageView = findViewById(R.id.image);
        PushDownAnim.setPushDownAnimTo(imageView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.oldpath = extras.getString("uri");
            if (this.oldpath.equals("")) {
                Toast.makeText(this, getResources().getString(R.string.picUpImg), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                this.phototUri = Uri.parse(this.oldpath);
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.picUpImg), Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            this.pictureFile = new File(this.phototUri.getPath());
            this.imageView.setImageBitmap(BitmapFactory.decodeFile(this.pictureFile.getAbsolutePath(), new Options()));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.imageView.setImageURI(this.phototUri);
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (this.appPreference.getBoolean("removeWatermark", false)) {
            this.removeWaterMark.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }


    public void moreAppClick(String str) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        } catch (ActivityNotFoundException unused) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://play.google.com/store/search?q=pub:");
            stringBuilder.append(getResources().getString(R.string.app_name));
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
        }
    }

    public void toSocialMediashare() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getCacheDir());
            stringBuilder.append("/share_icon.png");

            Uri parse = Uri.parse(stringBuilder.toString());
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.STREAM", parse);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getString(R.string.share_text));
            stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
            stringBuilder2.append(getPackageName());
            intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doPhotoPrint() {
        PrintHelper printHelper = new PrintHelper(Poster_PreviewActivity.this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        if (pictureFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
            printHelper.printBitmap("Print Document", myBitmap);
        }
    }

    public void customRateDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Rate ME")
                .setNegativeButtonText("LATER")
                //   .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList(new String[]{"Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"}))
                .setDefaultRating(5)
                .setTitle("Do You Love App?")
                .setDescription("Please rate us with stars and must give feedback")
                .setCommentInputEnabled(false).setStarColor(R.color.yellow)
                .setNoteDescriptionTextColor(R.color.text_color)
                .setTitleTextColor(R.color.text_color)
                .setDescriptionTextColor(R.color.text_color)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.hintTextColor)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(this)
                .show();
    }

    private void rateAppOnPlay() {
        StringBuilder stringBuilder;
        try {
            stringBuilder = new StringBuilder();
            stringBuilder.append(MARKET_CONSTANT);
            stringBuilder.append(getApplicationContext().getPackageName());
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
        } catch (ActivityNotFoundException unused) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(PLAY_CONSTANT);
            stringBuilder.append(getApplicationContext().getPackageName());
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
        }
        appPreference.putInt(Poster_AppConstants.isRated, 1);
    }

    @Override
    public void onBackPressed() {
        new InterstitialAds().Show_Ads(Poster_PreviewActivity.this, new InterstitialAds.AdCloseListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void shareToWhatsapp(String str) {
        try {
            Uri parse = Uri.parse(str);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.setPackage("com.whatsapp");
            intent.putExtra("android.intent.extra.STREAM", parse);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getString(R.string.share_text));
            stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
            stringBuilder2.append(getPackageName());
            intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void shareWithGooglePlus(String str) {
        try {
            IntentBuilder type = IntentBuilder.from(this).setType("image/jpeg");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getApplicationContext().getPackageName());
            stringBuilder.append(".provider");
            startActivityForResult(type.setStream(FileProvider.getUriForFile(this, stringBuilder.toString(), new File(str))).getIntent().setPackage("com.google.android.apps.plus"), this.REQUEST_FOR_GOOGLE_PLUS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareWithHike(String str) {
        try {
            Uri parse = Uri.parse(str);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.setPackage("com.bsb.hike");
            intent.putExtra("android.intent.extra.STREAM", parse);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getString(R.string.share_text));
            stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
            stringBuilder2.append(getPackageName());
            intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void shareWithTwitter(String str) {
        try {
            Uri parse = Uri.parse(str);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.setPackage("com.twitter.android");
            intent.putExtra("android.intent.extra.STREAM", parse);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getString(R.string.share_text));
            stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
            stringBuilder2.append(getPackageName());
            intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void shareWithFacebook(String str) {
        try {
            Uri parse = Uri.parse(str);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.setPackage("com.facebook.katana");
            intent.putExtra("android.intent.extra.STREAM", parse);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getString(R.string.share_text));
            stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
            stringBuilder2.append(getPackageName());
            intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void shareOnMessanger(String str) {
        try {
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{str}, null, new OnScanCompletedListener() {
                public void onScanCompleted(String str, Uri uri) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/gif");
                    intent.setPackage("com.facebook.orca");
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(getResources().getString(R.string.share_text));
                    stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
                    stringBuilder2.append(getPackageName());

                    intent.putExtra(Intent.EXTRA_TEXT, stringBuilder2.toString());
                    intent.putExtra("android.intent.extra.STREAM", uri);
                    intent.addFlags(524288);
                    Poster_PreviewActivity.this.startActivity(Intent.createChooser(intent, "Test"));
                }
            });
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(getApplicationContext(), "You don't seem to have twitter installed on this device", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendOnInstagram(String str) {
        try {
            Uri parse = Uri.parse(str);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.setPackage("com.instagram.android");
            intent.putExtra("android.intent.extra.STREAM", parse);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getString(R.string.share_text));
            stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
            stringBuilder2.append(getPackageName());
            intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void shareImageALL(String str) {
        try {
            Uri parse = Uri.parse(str);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.STREAM", parse);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getString(R.string.share_text));
            stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
            stringBuilder2.append(getPackageName());
            intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void exportBitmap() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.plzwait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Poster_PreviewActivity.this.pictureFile = new File(Poster_PreviewActivity.this.phototUri.getPath());
                    try {
                        if (!Poster_PreviewActivity.this.pictureFile.exists()) {
                            Poster_PreviewActivity.this.pictureFile.createNewFile();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                        PosterMakerActivity.withoutWatermark.compress(CompressFormat.PNG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        MediaScannerConnection.scanFile(Poster_PreviewActivity.this, new String[]{Poster_PreviewActivity.this.pictureFile.getAbsolutePath()}, null, new OnScanCompletedListener() {
                            public void onScanCompleted(String str, Uri uri) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Scanned ");
                                stringBuilder.append(str);
                                stringBuilder.append(":");
                                Log.i("ExternalStorage", stringBuilder.toString());
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("-> uri=");
                                stringBuilder2.append(uri);
                                Log.i("ExternalStorage", stringBuilder2.toString());
                            }
                        });
                        Poster_PreviewActivity previewActivity = Poster_PreviewActivity.this;
                        Poster_PreviewActivity previewActivity2 = Poster_PreviewActivity.this;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(Poster_PreviewActivity.this.getApplicationContext().getPackageName());
                        stringBuilder.append(".provider");
                        previewActivity.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", FileProvider.getUriForFile(previewActivity2, stringBuilder.toString(), Poster_PreviewActivity.this.pictureFile)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(1000);
                } catch (Exception unused) {
                }
                progressDialog.dismiss();
            }
        }).start();
        progressDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                Options options = new Options();
                options.inPreferredConfig = Config.ARGB_8888;
                options.inSampleSize = 2;
                Poster_PreviewActivity.this.imageView.setImageBitmap(BitmapFactory.decodeFile(Poster_PreviewActivity.this.pictureFile.getAbsolutePath(), options));
            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int i, String str) {
        if (i > 3) {
            rateAppOnPlay();
        }
        this.appPreference.putInt(Poster_AppConstants.isRated, 1);
    }

    @Override
    public void onBillingClientSetupFinished() {

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        boolean isActive = Poster_SubscriptionsUtil.isSubscriptionActive(purchases);

        if (!isActive) {

            // Adaptive_Banner
            new NativeAds(this).Adaptive_Banner(findViewById(R.id.adaptive_banner));

        }
    }

    @Override
    public void onPurchaseVerified() {

    }
}
