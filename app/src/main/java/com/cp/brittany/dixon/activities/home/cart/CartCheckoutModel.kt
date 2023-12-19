package com.cp.brittany.dixon.activities.home.cart

import com.cp.brittany.dixon.activities.home.models.CardData
import com.cp.brittany.dixon.activities.home.models.CartItem
import com.cp.brittany.dixon.activities.home.models.ShippingAddresse

data class CartCheckoutModel(
    var items: MutableList<CartItem> = ArrayList(),
    var shippingAddresses: ShippingAddresse = ShippingAddresse(),
    var defaultCard: CardData = CardData(),
    var discountedPrice: Int = 0,
    var total_price: Int = 0,
)