package com.cp.brittany.dixon.activities.home.models

import com.werb.eventbus.IEvent

data class MessageEvent(val type: Int, val id: Int): IEvent