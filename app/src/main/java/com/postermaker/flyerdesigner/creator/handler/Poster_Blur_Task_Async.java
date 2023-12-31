package com.postermaker.flyerdesigner.creator.handler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import cz.msebera.android.httpclient.HttpStatus;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.PosterMakerActivity;
import com.postermaker.flyerdesigner.creator.custom_view.Poster_Adjust_GPU_Filter;

public class Poster_Blur_Task_Async extends AsyncTask<String, Void, String> {

    private ImageView background_blur;
    private Bitmap btmp;
    private Activity context;
    private ProgressDialog pd;

    private Bitmap taskGaussinBlur(Activity activity, Bitmap bitmap) {
        try {
            GPUImage gPUImage = new GPUImage(activity);
            GPUImageGaussianBlurFilter gPUImageGaussianBlurFilter = new GPUImageGaussianBlurFilter();
            gPUImage.setFilter(gPUImageGaussianBlurFilter);
            new Poster_Adjust_GPU_Filter(gPUImageGaussianBlurFilter).adjust(HttpStatus.SC_MULTIPLE_CHOICES);
            gPUImage.requestRender();
            return gPUImage.getBitmapWithFilterApplied(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Poster_Blur_Task_Async(PosterMakerActivity posterMAKERActivity, Bitmap bitmap, ImageView imageView) {
        this.context = posterMAKERActivity;
        this.btmp = bitmap;
        this.background_blur = imageView;
    }


    @Override
    public String doInBackground(String... strArr) {
        this.btmp = taskGaussinBlur(this.context, this.btmp);
        return "yes";
    }

    @Override
    public void onPostExecute(String str) {
        this.pd.dismiss();
        Bitmap bitmap = this.btmp;
        if (bitmap != null) {
            this.background_blur.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPreExecute() {
        this.pd = new ProgressDialog(this.context);
        this.pd.setMessage(this.context.getResources().getString(R.string.plzwait));
        this.pd.setCancelable(false);
        this.pd.show();
    }


}
