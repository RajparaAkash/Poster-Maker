package com.postermaker.flyerdesigner.creator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.Scopes;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.ads.InterstitialAds;

public class Poster_ChooseSizeActivity extends AppCompatActivity {

    ImageView ivBack;

    @Override
    public void onBackPressed() {

        new InterstitialAds().Show_Ads(Poster_ChooseSizeActivity.this, new InterstitialAds.AdCloseListener() {
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
        setContentView(R.layout.poster_activity_choose_size);

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void goEditorScreen(View view) {

        new InterstitialAds().Show_Ads(Poster_ChooseSizeActivity.this, new InterstitialAds.AdCloseListener() {
            @Override
            public void onAdClosed() {
                if (view.getTag() != null) {
                    String[] split = view.getTag().toString().split("#");

                    Intent intent = new Intent(getApplicationContext(), PosterMakerActivity.class);
                    intent.putExtra("ratio", split[1]);
                    intent.putExtra("loadUserFrame", true);
                    intent.putExtra(Scopes.PROFILE, "https://postermaker.letsappbuilder.com/sizebg/" + split[0]);
                    intent.putExtra("hex", "");
                    startActivity(intent);
                }
            }
        });
    }
}
