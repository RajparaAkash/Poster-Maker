package com.postermaker.flyerdesigner.creator.ratinghelper;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.postermaker.flyerdesigner.creator.R;

import static android.content.Context.MODE_PRIVATE;

public class Poster_RatingDialog {
    private Context context;
    private Dialog dialog;
    private RelativeLayout main;
    private ImageView btnCacncel, ratingFace;
    private Poster_RotationRatingBar rotationratingbar_main;
    public static TextView btnSubmit;
    SharedPreferences pre;
    SharedPreferences.Editor edit;
    private boolean isEnable = true;
    private int defRating = 5;
    RatingDialogInterFace mRatingDialogListener;

    public Poster_RatingDialog(Context ctx) {
        this.context = ctx;
        pre = ctx.getSharedPreferences("rateData", MODE_PRIVATE);
        edit = pre.edit();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.poster_rate_dialog_main);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnCacncel =  dialog.findViewById(R.id.btnCacncel);
        ratingFace =  dialog.findViewById(R.id.ratingFace);
        main =  dialog.findViewById(R.id.main);
        rotationratingbar_main =  dialog.findViewById(R.id.rotationratingbar_main);
       // rotationratingbar_main.fillRatingBar(5);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(rotationratingbar_main);
        btnSubmit =  dialog.findViewById(R.id.btnSubmit);
        YoYo.with(Techniques.Shake).duration(1200).repeat(2).playOn(btnSubmit);

        Typeface custom_title = Typeface.createFromAsset(context.getAssets(), "font/cabin.ttf");
        btnSubmit.setTypeface(custom_title);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                main.setRotation(0);
                main.setAlpha(0);
                main.setScaleY(0);
                main.setScaleX(0);
                main.clearAnimation();
                rotationratingbar_main.setVisibility(View.INVISIBLE);
                if (mRatingDialogListener != null) {
                    mRatingDialogListener.onDismiss();
                }
            }
        });
        btnCacncel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // closeDialog();
                if (mRatingDialogListener != null) {
                    mRatingDialogListener.onExitClicked();
                }
            }
        });

        rotationratingbar_main.setOnRatingChangeListener(new Poster_BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(Poster_BaseRatingBar ratingBar, float rating) {
                if (ratingBar.getRating() < 4.0f) {
                    setRatingFace(false);
                } else {
                    setRatingFace(true);
                }
                if (mRatingDialogListener != null) {
                    mRatingDialogListener.onRatingChanged(rotationratingbar_main.getRating());
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.animate().scaleY(0).scaleX(0).alpha(0).rotation(-1800).setDuration(600).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        dialog.dismiss();
                        main.clearAnimation();
                        rotationratingbar_main.setVisibility(View.INVISIBLE);
                        if (mRatingDialogListener != null) {
                            mRatingDialogListener.onSubmit(rotationratingbar_main.getRating());
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).start();
            }
        });

    }

    public void showDialog() {
        isEnable = pre.getBoolean("enb", true);
        if (isEnable) {
            dialog.show();
            rotationratingbar_main.clearAnimation();
            rotationratingbar_main.setRating(defRating);
            setRatingFace(true);
            main.animate().scaleY(1).scaleX(1).rotation(1800).alpha(1).setDuration(600).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    main.clearAnimation();
                    rotationratingbar_main.setVisibility(View.VISIBLE);
                    rotationratingbar_main.startAnimation(AnimationUtils.loadAnimation(context, R.anim.poster_bounce_amn));
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();
        }
    }


    public void setEnable(boolean isEnable) {

        edit.putBoolean("enb", isEnable);
        edit.commit();
    }

    public boolean getEnable() {
        return pre.getBoolean("enb", true);
    }

    private void setRatingFace(boolean isTrue) {
        if (isTrue) {
            ratingFace.setImageResource(R.drawable.poster_favorite);
        } else {
            ratingFace.setImageResource(R.drawable.poster_favorite2);
        }

    }

    public void closeDialog() {
        main.animate().scaleY(0).scaleX(0).alpha(0).rotation(-1800).setDuration(600).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dialog.dismiss();
                main.clearAnimation();
                rotationratingbar_main.setVisibility(View.INVISIBLE);
                if (mRatingDialogListener != null) {
                    mRatingDialogListener.onDismiss();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }

    public void setDefaultRating(int defaultRating) {
        this.defRating = defaultRating;
    }

    public void setRatingDialogListener(RatingDialogInterFace mRatingDialogListener) {
        this.mRatingDialogListener = mRatingDialogListener;
    }

    public interface RatingDialogInterFace {
        void onDismiss();

        void onSubmit(float rating);

        void onRatingChanged(float rating);

        void onExitClicked();
    }

}
