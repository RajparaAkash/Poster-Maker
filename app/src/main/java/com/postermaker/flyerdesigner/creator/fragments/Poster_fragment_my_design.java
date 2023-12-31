package com.postermaker.flyerdesigner.creator.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.postermaker.flyerdesigner.creator.R;
import com.postermaker.flyerdesigner.creator.activity.PosterMakerActivity;
import com.postermaker.flyerdesigner.creator.app_utils.Poster_AppPreferenceClass;
import com.postermaker.flyerdesigner.creator.custom_adapter.Poster_Design_Adapter;
import com.postermaker.flyerdesigner.creator.editor_intelligence.Poster_AppConstants;
import com.postermaker.flyerdesigner.creator.handler.Poster_DB_Handler;
import com.postermaker.flyerdesigner.creator.handler.Poster_Handle_Bitmap_Object;
import com.postermaker.flyerdesigner.creator.handler.Poster_Template_InfoData;
import com.postermaker.flyerdesigner.creator.imageloader.Poster_Glide_Image_Utils;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.os.Build.VERSION.SDK_INT;
import static com.postermaker.flyerdesigner.creator.activity.Poster_MainActivity.materialSearchBar;
import static com.postermaker.flyerdesigner.creator.activity.Poster_MainActivity.tvNavTitle;

public class Poster_fragment_my_design extends Fragment {

    public Typeface typefaceTextBold;

    String catName = "MY_TEMP";

    public Poster_Design_Adapter posterDesignAdapter;

    //  public GridView gridView;
    int heightItemGrid = 50;
    private RelativeLayout imagBack, lay_dialog;
    LordDataOperationAsync loadDataAsync = null;

    private Poster_AppPreferenceClass appPreferenceClass;
    // ProgressBar progress_bar;

    public int spoisiton;

    public ArrayList<Poster_Template_InfoData> templateList = new ArrayList();
    Typeface ttf;

    private TextView tvTitle;
    private TextView no_image;

    int widthItemGrid = 50, newWidth;

    public Typeface setBoldFont() {
        return this.typefaceTextBold;
    }

    public SweetAlertDialog pDialog;

    RecyclerView rvForTemplateList;
    HomeCardAdapter homeCardAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.poster_layout_fragment_my_design, container, false);

        if (tvNavTitle.getVisibility() == View.GONE) {
            tvNavTitle.setVisibility(View.VISIBLE);
            tvNavTitle.setText("My Edit");
            materialSearchBar.setVisibility(View.GONE);
        } else {
            materialSearchBar.setVisibility(View.GONE);
            tvNavTitle.setText("My Edit");
        }

        this.typefaceTextBold = Typeface.createFromAsset(getActivity().getAssets(), "font/Montserrat-SemiBold.ttf");

        lay_dialog = view.findViewById(R.id.rel_text);
        this.no_image = view.findViewById(R.id.no_image);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        newWidth = size.x;
        newWidth = newWidth / 2;

        appPreferenceClass = new Poster_AppPreferenceClass(getActivity());

        //  AdHelper.showInterstitial(getActivity());

        tvTitle = view.findViewById(R.id.tvTitle);
        Typeface custom_title = Typeface.createFromAsset(getActivity().getAssets(), "font/cabin.ttf");
        tvTitle.setTypeface(custom_title);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthItemGrid = ((int) ((float) (displayMetrics.widthPixels - Poster_Glide_Image_Utils.convertDpToPx(getActivity(), 10.0f)))) / 2;
        heightItemGrid = ((int) ((float) (displayMetrics.heightPixels - Poster_Glide_Image_Utils.convertDpToPx(getActivity(), 10.0f)))) / 2;

        rvForTemplateList = view.findViewById(R.id.rvForTemplateList);
        //  StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvForTemplateList.setLayoutManager(layoutManager);

        homeCardAdapter = new HomeCardAdapter();

        this.no_image.setTypeface(setBoldFont());

        return view;
    }

    class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.ViewHolder> {

        HomeCardAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.poster_new_card_edit_design_templates, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

            final Poster_Template_InfoData templateInfoData = templateList.get(i);

            if (catName.equals("MY_TEMP")) {

                viewHolder.imgDeletePoster.setVisibility(View.VISIBLE);
                try {
                    if (templateInfoData.getTHUMB_INFO_URI().toString().contains("thumb")) {
                        Glide.with(getActivity()).load(new File(templateInfoData.getTHUMB_INFO_URI()).getAbsoluteFile()).thumbnail(0.1f).apply(new RequestOptions().dontAnimate().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(viewHolder.mThumbnail);
                    } else if (templateInfoData.getTHUMB_INFO_URI().toString().contains("raw")) {
                        Glide.with(getActivity()).load(findBitmapDataObject(Uri.parse(templateInfoData.getTHUMB_INFO_URI()).getPath()).imageByteArray).thumbnail(0.1f).apply(new RequestOptions().dontAnimate().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(viewHolder.mThumbnail);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    viewHolder.mThumbnail.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.poster_no_image));
                }
            } else {
                Glide.with(getActivity()).load(getActivity().getResources().getIdentifier(templateInfoData.getTHUMB_INFO_URI(), "drawable", getActivity().getPackageName())).thumbnail(0.1f).apply(new RequestOptions().dontAnimate().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                }).into(viewHolder.mThumbnail);
            }

            viewHolder.mThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu popup = new PopupMenu(getActivity(), viewHolder.iv_edit);
                    popup.inflate(R.menu.poster_menu_edit_design);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.EditTemplate:

                                    Intent intent = new Intent(getActivity(), PosterMakerActivity.class);
                                    intent.putExtra("position", i);
                                    intent.putExtra("loadUserFrame", false);
                                    intent.putExtra("Temp_Type", "Temp_Type");
                                    startActivity(intent);

                                    return true;

                                case R.id.ShareTemplate:

                                    try {
                                        if (templateInfoData.getTHUMB_INFO_URI().toString().contains("thumb")) {
                                            Glide.with(getActivity()).load(new File(templateInfoData.getTHUMB_INFO_URI()).getAbsoluteFile()).thumbnail(0.1f).apply(new RequestOptions().dontAnimate().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                                    Uri uri = getImageUri(getActivity(), bitmap);
                                                    shareImageALL(uri);
                                                    Log.e("##", "called 1");
                                                    return true;
                                                }
                                            }).into(viewHolder.mThumbnail);
                                        } else if (templateInfoData.getTHUMB_INFO_URI().toString().contains("raw")) {
                                            Glide.with(getActivity()).load(findBitmapDataObject(Uri.parse(templateInfoData.getTHUMB_INFO_URI()).getPath()).imageByteArray).thumbnail(0.1f).apply(new RequestOptions().dontAnimate().placeholder((int) R.drawable.poster_no_image).error((int) R.drawable.poster_no_image)).listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                                    Uri uri = getImageUri(getActivity(), bitmap);
                                                    shareImageALL(uri);

                                                    Log.e("##", "called 2");

                                                    return false;
                                                }
                                            }).into(viewHolder.mThumbnail);
                                        }
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        viewHolder.mThumbnail.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.poster_no_image));
                                    }

                                    return true;
                                case R.id.DeleteTemplate:

                                    showDesignOptionsDialog(i);

                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    popup.show();
                }
            });

            viewHolder.imgDeletePoster.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                }
            });

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  popup.show();
                }
            });

        }

        @Override
        public int getItemCount() {
            if (templateList == null)
                return 0;
            return templateList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            ImageView mThumbnail;
            ProgressBar progressBar;
            ImageView imgDeletePoster, iv_edit;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardViewHome);
                mThumbnail = itemView.findViewById(R.id.iv_image);
                imgDeletePoster = itemView.findViewById(R.id.iVDelete);
                iv_edit = itemView.findViewById(R.id.iv_edit);
                progressBar = itemView.findViewById(R.id.progressBar1);
            }
        }
    }

    public void shareImageALL(Uri str) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.STREAM", str);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(getString(R.string.share_text));
            stringBuilder2.append("\nhttps://play.google.com/store/apps/details?id=");
            stringBuilder2.append(getActivity().getPackageName());
            intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void showDesignOptionsDialog(final int i) {
        final Dialog dialog = new Dialog(getActivity(), R.style.ThemeWithCorners);
        dialog.setContentView(R.layout.poster_layout_delete_dialog);
        dialog.setCancelable(false);
        Button button = dialog.findViewById(R.id.btnCancel);
        (dialog.findViewById(R.id.btnDelete)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Poster_Template_InfoData templateInfoData = templateList.get(i);
                Poster_DB_Handler dbHandler = Poster_DB_Handler.getDatabaseHandler(getActivity());
                boolean deleteTemplateInfo = dbHandler.remove_Template_Info(templateInfoData.getTEMPLATE_INFO_ID());
                dbHandler.close();
                if (deleteTemplateInfo) {
                    rmeoveFile(Uri.parse(templateInfoData.getTHUMB_INFO_URI()));
                    templateList.remove(templateInfoData);
                    homeCardAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    return;
                }
                Toast.makeText(getActivity(), getResources().getString(R.string.del_error_toast), Toast.LENGTH_SHORT).show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public boolean rmeoveFile(Uri uri) {
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
                    z = getActivity().deleteFile(file.getName());
                }
            }
            //  Context context = this.context;
            //  Context context2 = getContext();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getContext().getApplicationContext().getPackageName());
            stringBuilder.append(".provider");
            getActivity().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", FileProvider.getUriForFile(getActivity(), stringBuilder.toString(), file)));
        } catch (Exception e2) {
            String str = "DELETED FILE";
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("deleteTempFile: ");
            stringBuilder2.append(e2);
            Log.e(str, stringBuilder2.toString());
        }
        return z;
    }

    private Poster_Handle_Bitmap_Object findBitmapDataObject(String str) {
        try {
            return (Poster_Handle_Bitmap_Object) new ObjectInputStream(new FileInputStream(new File(str))).readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        } catch (ClassNotFoundException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestStoragePermission();
    }

    public void makeStickerDir() {
        appPreferenceClass = new Poster_AppPreferenceClass(getActivity());
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
        String str = "dfcsdv";
        stringBuilder = new StringBuilder();
        stringBuilder.append("onCreate: ");
        stringBuilder.append(Poster_AppConstants.sdcardPath);
        Log.e(str, stringBuilder.toString());
    }

    public void setupProgress() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#D81B60"));
        pDialog.setTitleText("Fetching...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public class LordDataOperationAsync extends AsyncTask<String, Void, String> {
        @Override
        public void onPreExecute() {

        }

        @Override
        public String doInBackground(String... strArr) {

            //  setupProgress();

            try {
                templateList.clear();
                Poster_DB_Handler dbHandler = Poster_DB_Handler.getDatabaseHandler(getActivity());
                if (catName.equals("MY_TEMP")) {
                    templateList = dbHandler.find_Template_List_Des("USER");
                }
                dbHandler.close();
            } catch (NullPointerException unused) {
            }
            return "yes";
        }

        @Override
        public void onPostExecute(String str) {

/*
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
*/

            try {
                if (templateList.size() != 0) {
                    rvForTemplateList.setAdapter(homeCardAdapter);
                    //  gridView.setAdapter(posterDesignAdapter);
                    lay_dialog.setVisibility(View.GONE);
                }
                if (catName.equals("MY_TEMP")) {
                    if (templateList.size() == 0) {
                        lay_dialog.setVisibility(View.VISIBLE);
                        //  txt_dialog.setText(getResources().getString(R.string.NoDesigns));
                    } else if (templateList.size() <= 4) {
                        //  txt_dialog.setText(getResources().getString(R.string.DesignOptionsInstruction));
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
/*
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(getActivity(),"Permission allowed",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),"Please allow permission",Toast.LENGTH_SHORT).show();
                }
            }
*/
        }
    }

    private void requestStoragePermission() {
        /* Changed 22/05/2023 */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Dexter.withActivity(getActivity()).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        makeStickerDir();
                        loadDataAsync = new LordDataOperationAsync();
                        loadDataAsync.execute(new String[]{""});
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        } else {
            Dexter.withActivity(getActivity()).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO").withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        makeStickerDir();
                        loadDataAsync = new LordDataOperationAsync();
                        loadDataAsync.execute(new String[]{""});
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        }
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

    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        startActivityForResult(intent, 101);
    }

}
