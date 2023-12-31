//package com.postermaker.flyerdesigner.creator.adapters;
//
//import android.graphics.drawable.Drawable;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.text.Spanned;
//import android.text.style.TextAppearanceSpan;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.billingclient.api.SkuDetails;
//import com.postermaker.flyerdesigner.creator.Poster_Application;
//import com.postermaker.flyerdesigner.creator.R;
//import com.postermaker.flyerdesigner.creator.billing.Poster_BillingConstants;
//import com.postermaker.flyerdesigner.creator.utils.Poster_ScreenUtils;
//
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Poster_SkuAdapter extends RecyclerView.Adapter<Poster_SkuAdapter.SkuViewHolder> {
//
//    private static final String TAG = "SkuAdapter";
//    private List<SkuDetails> detailsList = new ArrayList<>();
//    private int selectedItem = 1;
//
//    public Poster_SkuAdapter() {
//    }
//
//    @NonNull
//    @Override
//    public SkuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(R.layout.poster_subscription_list_view, parent, false);
//        int width = Poster_ScreenUtils.getScreenWidth();
//        width = Math.abs((width / 3));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
//        view.setLayoutParams(params);
//        return new SkuViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull SkuViewHolder holder, int position) {
//        holder.setData(getItemAtPosition(position));
//
//        if( selectedItem == 1 && isSIXOffer(getItemAtPosition(position)))
//        {
//            holder.offView.setVisibility(View.VISIBLE);
//            holder.offView.setText("35% OFF");
//        }
//
//        if( selectedItem == 2 && isYearOffer(getItemAtPosition(position)))
//        {
//            holder.offView.setVisibility(View.VISIBLE);
//            holder.offView.setText("50% OFF");
//        }
//
//        holder.itemView.setOnClickListener(v -> setActive(holder.getAdapterPosition())
//        );
//
//        Drawable drawable = ContextCompat.getDrawable(Poster_Application.getContext(), (selectedItem == position) ?
//                R.drawable.poster_package_border_active_bg : R.drawable.poster_package_border_inactive_bg);
//        holder.mainContainer.setBackground(drawable);
//    }
//
//    private SkuDetails getItemAtPosition(int position) {
//        return detailsList.get(position);
//    }
//
//    public void setDetailsList(List<SkuDetails> skuDetails) {
//        detailsList = skuDetails;
//        notifyDataSetChanged();
//    }
//
//    private void setActive(int index) {
//        selectedItem = index;
//        notifyDataSetChanged();
//    }
//
//    private boolean isSIXOffer(SkuDetails details){
//        return details != null && details.getSku().equals(Poster_BillingConstants.SKU_SIX_MONTHLY);
//    }
//
//    private boolean isYearOffer(SkuDetails details){
//        return details != null && details.getSku().equals(Poster_BillingConstants.SKU_YEARLY);
//    }
//
//    public SkuDetails getSelectedItem() {
//        SkuDetails skuDetail = null;
//        if (detailsList.size() > 0) {
//            skuDetail = detailsList.get(selectedItem);
//        }
//        return skuDetail;
//    }
//
//    @Override
//    public int getItemCount() {
//        return detailsList.size();
//    }
//
//
//    static class SkuViewHolder extends RecyclerView.ViewHolder {
//
//        private LinearLayout mainContainer;
//        private TextView monthsView, priceView, subtitleView, offView;
//
//        SkuViewHolder(@NonNull View itemView) {
//            super(itemView);
//            mainContainer = itemView.findViewById(R.id.main_container);
//            monthsView = itemView.findViewById(R.id.months);
//            priceView = itemView.findViewById(R.id.price);
//            subtitleView = itemView.findViewById(R.id.subtitle);
//            offView = itemView.findViewById(R.id.off_view);
//        }
//
//        public void setData(SkuDetails details) {
//            if (details != null) {
//                String sku = details.getSku();
//                long price = details.getOriginalPriceAmountMicros();
//                String code = details.getPriceCurrencyCode();
//
//                if (sku.equalsIgnoreCase(Poster_BillingConstants.SKU_ONE_MONTHLY)) {
//                    monthsView.setText(R.string.one_month);
//                    priceView.setText(priceConversion(price, 1, code));
//                } else if (sku.equalsIgnoreCase(Poster_BillingConstants.SKU_SIX_MONTHLY)) {
//                    monthsView.setText(R.string.six_months);
//                    priceView.setText(priceConversion(price, 6, code));
//                    subtitleView.setText(buildSubtitleString(details.getPrice(), 6));
//                } else if (sku.equalsIgnoreCase(Poster_BillingConstants.SKU_YEARLY)) {
//                    monthsView.setText(R.string.twelve_months);
//                    priceView.setText(priceConversion(price, 12, code));
//                    subtitleView.setText(buildSubtitleString(details.getPrice(), 12));
//                }
//            }
//        }
//
//        private Spannable priceConversion(float price, int month, String code) {
//            float amount = (price / 1000000) / month;
//            return buildPriceString(code, currencyFormat(amount));
//        }
//
//        private Spannable buildPriceString(String code, String price) {
//            SpannableStringBuilder builder = new SpannableStringBuilder();
//            builder.append(code).append(" ");
//            builder.setSpan(new TextAppearanceSpan(Poster_Application.getContext(), R.style.TextAppearance_Price), 0, builder.length(),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.append(price);
//            return builder;
//        }
//
//        private String buildSubtitleString(String price, int month) {
//            return Poster_Application.getContext().getString(R.string.price_string, price, month);
//        }
//
//        String currencyFormat(float amount) {
//            return new DecimalFormat("##.##").format(amount);
//        }
//
//    }
//}
