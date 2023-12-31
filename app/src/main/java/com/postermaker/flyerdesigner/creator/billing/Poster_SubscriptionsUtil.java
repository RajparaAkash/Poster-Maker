package com.postermaker.flyerdesigner.creator.billing;

import com.android.billingclient.api.Purchase;
import com.postermaker.flyerdesigner.creator.billing.Poster_BillingConstants;
import com.postermaker.flyerdesigner.creator.utils.Poster_LogUtil;

import java.util.List;

public class Poster_SubscriptionsUtil {

    private static final String TAG = "SubscriptionsUtil";

    public static boolean isSubscriptionActive(List<Purchase> purchaseList) {
        boolean isActive = false;
        if (purchaseList != null && purchaseList.size() > 0) {
            for (Purchase purchase : purchaseList) {
                if (purchase.getSkus().contains(Poster_BillingConstants.SKU_ONE_MONTHLY)) {
                    isActive = purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged();
                    Poster_LogUtil.logDebug(TAG, "One_Month_Subscription Active: " + isActive);
                } else if (purchase.getSkus().contains(Poster_BillingConstants.SKU_SIX_MONTHLY)) {
                    Poster_LogUtil.logDebug(TAG, "Six_Months_Subscription Active: " + isActive);
                    isActive = purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged();
                } else if (purchase.getSkus().contains(Poster_BillingConstants.SKU_YEARLY)) {
                    Poster_LogUtil.logDebug(TAG, "One_Year_Subscription Active: " + isActive);
                    isActive = purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged();
                }
            }
        }
        return isActive;
    }
}
