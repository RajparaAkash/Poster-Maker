package com.postermaker.flyerdesigner.creator.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.Purchase;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;
import com.postermaker.flyerdesigner.creator.ads.NativeAds;
import com.postermaker.flyerdesigner.creator.billing.Poster_BillingUpdatesListener;
import com.postermaker.flyerdesigner.creator.billing.Poster_SubscriptionsUtil;
import com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_templates;
import com.postermaker.flyerdesigner.creator.fragments.Poster_fragment_viewpager_templates;
import com.postermaker.flyerdesigner.creator.poster_builder.Poster_Full_Poster_Thumb;

import java.util.ArrayList;
import java.util.List;

public class Poster_ViewPagerActivity extends AppCompatActivity implements Poster_BillingUpdatesListener {

    public ArrayList<Poster_Full_Poster_Thumb> arrayList = new ArrayList<>();

    private ViewPager pager;
    private TabLayout tabs;
    private SectionsPagerAdapter adapter;

    String cat_name, ratio = "0";
    int pos, cat_id, newWidth;

    ImageView iVBack, ivPro;
    TextView tvTitle;

    boolean isActive;

    SharedPreferences preferences;

    @Override
    public void onBackPressed() {
        new InterstitialAds().Show_Ads(Poster_ViewPagerActivity.this, new InterstitialAds.AdCloseListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.poster_layout_viewpager_templates);

        //  adView = new AdView(this);

        preferences = getSharedPreferences("mysession", MODE_PRIVATE);

        pos = getIntent().getIntExtra("position", -1);
        cat_id = getIntent().getIntExtra("cat_id", -1);
        cat_name = getIntent().getStringExtra("cateName");

        pager = findViewById(R.id.pager);
        tabs = findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(pager);

        tvTitle = findViewById(R.id.tvTitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/Raleway Bold.ttf");
        tvTitle.setTypeface(custom_font);

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        if (Poster_fragment_templates.posterDatas.get(pos).getPoster_list() != null)
            arrayList = Poster_fragment_templates.posterDatas.get(pos).getPoster_list();

        for (int i = 0; i < Poster_fragment_templates.posterDatas.size(); i++) {
            Poster_fragment_viewpager_templates fragment_templates_viewPager = new Poster_fragment_viewpager_templates();
            fragment_templates_viewPager.setCategory_id(Integer.parseInt(Poster_fragment_templates.posterDatas.get(i).getPOSTERCat_id()));
            fragment_templates_viewPager.setPos(i);
            fragment_templates_viewPager.setCategory_Name(Poster_fragment_templates.posterDatas.get(i).getPOSTERCat_name());
            adapter.AddFragment(fragment_templates_viewPager, Poster_fragment_templates.posterDatas.get(i).getPOSTERCat_name());
        }

        pager.setAdapter(adapter);
        pager.setCurrentItem(pos);

        //  changeTabsFont(custom_font);

        iVBack = findViewById(R.id.iVBack);
        iVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InterstitialAds().Show_Ads(Poster_ViewPagerActivity.this, new InterstitialAds.AdCloseListener() {
                    @Override
                    public void onAdClosed() {
                        finish();
                    }
                });
            }
        });

        ivPro = findViewById(R.id.ivPro);
        YoYo.with(Techniques.Shake).duration(1000).repeat(500).playOn(ivPro);

        ivPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Poster_ViewPagerActivity.this, Poster_PurcheshActivity.class));
            }
        });
        //  Glide.with(this).asGif().load(R.drawable.one).into(iVQuiz);

//        initAppBilling();
    }

    @Override
    public void onDestroy() {
//        if (mBillingManager != null) {
//            mBillingManager.destroy();
//        }
        super.onDestroy();
    }

    @Override
    public void onBillingClientSetupFinished() {

    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        isActive = Poster_SubscriptionsUtil.isSubscriptionActive(purchases);

        if (!isActive) {

            // Adaptive_Banner
            new NativeAds(this).Adaptive_Banner(findViewById(R.id.adaptive_banner));

        }
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

}
