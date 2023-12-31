package com.postermaker.flyerdesigner.creator.billing;

import com.android.billingclient.api.Purchase;

import java.util.List;

public interface Poster_BillingUpdatesListener {

    void onBillingClientSetupFinished();

    void onPurchasesUpdated(List<Purchase> purchases);

    void onPurchaseVerified();
}
