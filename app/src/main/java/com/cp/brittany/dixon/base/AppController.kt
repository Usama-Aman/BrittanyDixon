package com.cp.brittany.dixon.base

import android.app.Application
import com.cp.brittany.dixon.activities.home.cart.CartCheckoutModel
import com.cp.brittany.dixon.activities.home.premium.BillingClientSetup
import com.cp.brittany.dixon.network.SocketIO
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache

class AppController : Application() {

    companion object {
        const val PAGINATION_COUNT = 10
        var resetOTP = 0
        var isGuest = false
        var deviceAppUID = ""
        var cartCount: Int = 0

        var cartCheckoutData: CartCheckoutModel = CartCheckoutModel()
        var gotoEditAddress: Boolean = false
        var gotoEditCard: Boolean = false

        var socket: SocketIO = SocketIO()

        lateinit var simpleCache: SimpleCache
        const val exoPlayerCacheSize: Long = 500 * 1024 * 1024 // 500mb
        lateinit var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor
        lateinit var standaloneDatabaseProvider: StandaloneDatabaseProvider
    }

    override fun onCreate() {
        super.onCreate()

        //Billing Client Setup
        BillingClientSetup.getInstance(this)
        BillingClientSetup.billingClient

        //Cache Video Exo Player
        leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize)
        standaloneDatabaseProvider = StandaloneDatabaseProvider(this)
        simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, standaloneDatabaseProvider)
    }

}