package com.cp.brittany.dixon.activities.home.premium

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

@SuppressLint("StaticFieldLeak")
object BillingClientSetup {

    lateinit var billingClient: BillingClient
    private var billingPurchaseInterface: BillingPurchaseInterface? = null
    private lateinit var mContext: Context

    interface BillingPurchaseInterface {
        fun onPurchaseUpdated(billingResult: BillingResult, purchases: Purchase)
    }

    fun setPurchaseUpdatedListener(billingPurchaseInterface: BillingPurchaseInterface?) {
        this.billingPurchaseInterface = billingPurchaseInterface
    }

    fun getInstance(context: Context) {
        mContext = context
        if (!::billingClient.isInitialized)
            billingClient = setUpBillingClient(mContext)
        startConnection()
    }

    private fun setUpBillingClient(context: Context): BillingClient {
        return BillingClient.newBuilder(mContext)
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
            .build()
    }

    private fun startConnection() {
        if (!billingClient.isReady)
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        queryPurchases(BillingClient.SkuType.SUBS) {}
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    startConnection()
                }
            })
        else
            queryPurchases(BillingClient.SkuType.SUBS) {}
    }

    /*
    * typeOfPurchase ->  In App or Sub
    * purchaseListener -> Unit method to get the list of pucrhases
    * */

    fun queryPurchases(
        typeOfPurchase: String = BillingClient.SkuType.SUBS,
        function: (MutableList<Purchase>) -> Unit
    ) {
        if (::billingClient.isInitialized && billingClient.isReady) {
            billingClient.queryPurchasesAsync(typeOfPurchase) { billingResult, purchaseList ->
                Log.d("Purchase", purchaseList.toString())

                function(purchaseList)
                //Possible values are 0 (purchased), 1 (canceled), 2 (refunded), or 3 (expired, for subscription purchases only).
            }
        }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.v("Init Purchase Listener", "billingResult responseCode : ${billingResult.responseCode}")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                Log.d("PurchaseUpdatedListener", purchases.size.toString())
                handleNonConsumablePurchase(purchases[0])
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.d("PurchaseUpdatedListener", "billingResult responseCode : ${billingResult.responseCode}")
            } else {
                Log.d("PurchaseUpdatedListener", "billingResult responseCode : ${billingResult.responseCode}")
            }
        }

    private fun handleConsumedPurchases(purchase: Purchase) {
        Log.d("TAG_INAPP", "handleConsumablePurchasesAsync foreach it is $purchase")
        val params =
            ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.consumeAsync(params) { billingResult, purchaseToken ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    // Update the appropriate tables/databases to grant user the items
                    Log.d("TAG_INAPP", " Update the appropriate tables/databases to grant user the items")
                }
                else -> {
                    Log.w("TAG_INAPP", billingResult.debugMessage)
                }
            }
        }
    }

    private fun handleNonConsumablePurchase(purchase: Purchase) {
        Log.v("TAG_INAPP", "handlePurchase : $purchase")
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    val billingResponseCode = billingResult.responseCode
                    val billingDebugMessage = billingResult.debugMessage

                    if (this@BillingClientSetup.billingPurchaseInterface != null)
                        this@BillingClientSetup.billingPurchaseInterface?.onPurchaseUpdated(billingResult, purchase)

                    Log.v("TAG_INAPP", "response code: $billingResponseCode")
                    Log.v("TAG_INAPP", "debugMessage : $billingDebugMessage")

                }
            }
        }
    }

    fun queryAvailableProducts(context: Context, function: (MutableList<SkuDetails>?) -> Unit) {
        if (::billingClient.isInitialized && billingClient.isReady) {
            val skuList = ArrayList<String>()
//            skuList.add("test_1")
//            skuList.add("test_2")
            skuList.add("bd_one_month_sub")
            skuList.add("bd_quater_sub")
            skuList.add("bd_yearly_sub")
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                // Process the result.
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                    function(skuDetailsList)
                } else function(null)
            }
        } else {
            getInstance(context)
        }
    }

    fun destroyConnection() {
        billingClient.endConnection()
    }


}