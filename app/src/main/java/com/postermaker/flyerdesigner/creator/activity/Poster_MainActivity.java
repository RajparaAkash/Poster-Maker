package com.postermaker.flyerdesigner.creator.activity;

import static com.postermaker.flyerdesigner.creator.ratinghelper.Poster_RatingDialog.btnSubmit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.billingclient.api.Purchase;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.kongzue.tabbar.Tab;
import com.kongzue.tabbar.TabBarView;
import com.kongzue.tabbar.interfaces.OnTabChangeListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.postermaker.flyerdesigner.creator.ads.NativeAds;
import com.postermaker.flyerdesigner.creator.billing.Poster_BillingUpdatesListener;
import com.postermaker.flyerdesigner.creator.billing.Poster_SubscriptionsUtil;
import com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_create;
import com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_home;
import com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_my_design;
import com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_templates;
import com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_work_poster;
import com.postermaker.flyerdesigner.creator.ratinghelper.Poster_RatingDialog;

import java.util.ArrayList;
import java.util.List;

public class Poster_MainActivity extends AppCompatActivity implements Poster_BillingUpdatesListener {

    LinearLayout contentView;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private ImageView iv_proads, iv_howtostart;

    public static MaterialSearchBar materialSearchBar;
    public static TextView tvNavTitle;

    TabBarView tabbar;
    List<Tab> tabs;

    SharedPreferences preferences;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.color.white);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            //   window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private void animateNavigationDrawer() {

        drawerLayout.setScrimColor(getResources().getColor(R.color.transparent));

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - 1.0f);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.poster_new_activity_main);

        // setStatusBarGradiant(MainActivity.this);

        preferences = getSharedPreferences("mysession", MODE_PRIVATE);

//        adView = new AdView(this);
        //  adView = findViewById(R.id.adView);

//        initAppBilling();

        contentView = findViewById(R.id.contentView);

        drawerLayout = findViewById(R.id.drawer_layout_home);

        navigationView = findViewById(R.id.nav_view);

        animateNavigationDrawer();

        iv_howtostart = findViewById(R.id.iv_howtostart);
        iv_proads = findViewById(R.id.iv_proads);
        YoYo.with(Techniques.Shake).duration(1000).repeat(500).playOn(iv_proads);

        materialSearchBar = findViewById(R.id.searchBar);
        tvNavTitle = findViewById(R.id.tvTitle);

        tabbar = findViewById(R.id.tabbar);

        tabs = new ArrayList<>();
        tabs.add(new Tab(this, "Home", R.drawable.poster_ic_bottom_home));
        tabs.add(new Tab(this, "Category", R.drawable.poster_ic_template_list));
        tabs.add(new Tab(this, "Scratch", R.drawable.poster_ic_create_scratch));
        tabs.add(new Tab(this, "My Edit", R.drawable.poster_ic_my_work));
        tabs.add(new Tab(this, "My Work", R.drawable.poster_ic_bottom_my_design));

        tabbar.setTabClickBackground(TabBarView.TabClickBackgroundValue.RIPPLE);

        replaceFragment(new Poster_fragment_templates());

        tabbar.setTab(tabs).setOnTabChangeListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(View v, int index) {
                switch (index) {
                    case 0:
                        replaceFragment(new Poster_fragment_templates());
                        break;

                    case 1:
                        replaceFragment(new Poster_fragment_home());
                        break;

                    case 2:
                        replaceFragment(new Poster_fragment_create());
                        break;

                    case 3:
                        replaceFragment(new Poster_fragment_my_design());
                        break;

                    case 4:
                        replaceFragment(new Poster_fragment_work_poster());
                        break;
                }
            }
        });

        iv_proads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Poster_MainActivity.this, Poster_PurcheshActivity.class));
            }
        });

        iv_howtostart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=khuedRb9vLA")));
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    public void handleClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_nav_home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            replaceFragment(new Poster_fragment_templates());
        } else if (id == R.id.ll_nav_categories) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            replaceFragment(new Poster_fragment_home());
        } else if (id == R.id.ll_nav_scratch) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            replaceFragment(new Poster_fragment_create());
        } else if (id == R.id.ll_nav_myedit) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            replaceFragment(new Poster_fragment_my_design());
        } else if (id == R.id.ll_nav_mywork) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            replaceFragment(new Poster_fragment_work_poster());
        } else if (id == R.id.ll_share_app_nav) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", "Video Status");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("\nLet me recommend you this application\n\n");
                stringBuilder.append("https://play.google.com/store/apps/details?id=");
                stringBuilder.append(getApplicationContext().getPackageName());
                intent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                startActivity(Intent.createChooser(intent, "choose one"));
            } catch (Exception unused) {
                unused.printStackTrace();
            }
        } else if (id == R.id.ll_rate_app_nav) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            launchMarket();
        }
    }

    public void launchMarket() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("market://details?id=");
        stringBuilder.append(getPackageName());
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(this, " Sorry, Not able to open!", Toast.LENGTH_SHORT).show();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
//        if (mBillingManager != null) {
//            mBillingManager.destroy();
//        }
        super.onDestroy();
    }

    public void ratingdialog() {
        final Poster_RatingDialog ratingDialog = new Poster_RatingDialog(this);
        ratingDialog.setRatingDialogListener(new Poster_RatingDialog.RatingDialogInterFace() {
            @Override
            public void onDismiss() {

            }

            @Override
            public void onSubmit(float rating) {
                if (rating > 3) {
                    // rateAppOnPlay();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }

            @Override
            public void onRatingChanged(float rating) {
                YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnSubmit);
            }

            @Override
            public void onExitClicked() {
                new InterstitialAds().Show_Ads(Poster_MainActivity.this, new InterstitialAds.AdCloseListener() {
                    @Override
                    public void onAdClosed() {
                        finishAffinity();
                    }
                });
            }
        });

        ratingDialog.showDialog();
    }

    public void showRateUsDialog() {
        ratingdialog();
    }

    @Override
    public void onBackPressed() {
        showRateUsDialog();
    }

    @Override
    public void onBillingClientSetupFinished() {

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchaseList) {
        boolean isActive = Poster_SubscriptionsUtil.isSubscriptionActive(purchaseList);

        if (!isActive) {

            // Adaptive_Banner
            new NativeAds(this).Adaptive_Banner(findViewById(R.id.adaptive_banner));

        }
    }

    @Override
    public void onPurchaseVerified() {

    }

/*
    public  class MyPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 5;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new fragment_home();
                case 2:
                    return new fragment_create();
                case 3:
                    return new fragment_my_design();
                case 4:
                    return new fragment_work_poster();
                case 1:
                    return new fragment_templates();
                default:
                    return new fragment_home();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
*/
}
