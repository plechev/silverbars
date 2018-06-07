package com.silverbars.shop.domain

import java.math.BigDecimal
import java.util.*

data class Order(val id: UUID, val userId: String, val quantity: BigDecimal, val pricePerKg: BigDecimal, val type: OrderType)

enum class OrderType(val comparator: Comparator<in BigDecimal>) {
    BUY(compareByDescending<BigDecimal> { it }),
    SELL(compareBy<BigDecimal> { it })
}
