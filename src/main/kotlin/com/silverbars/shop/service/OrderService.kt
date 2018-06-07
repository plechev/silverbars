package com.silverbars.shop.service

import com.silverbars.shop.domain.Order
import com.silverbars.shop.domain.OrderType
import java.math.BigDecimal
import java.util.*

object OrderService {

    fun register(order: Order) {
        storage.put(order.id, order)
    }

    fun cancel(id: UUID): Order? {
        return storage.remove(id)
    }

    fun orderSummary(orderType: OrderType): SortedMap<BigDecimal, BigDecimal?> {
        return storage.values
                .filter { it.type == orderType }
                .groupingBy {
                    it.pricePerKg
                }.aggregate({ _, accumulator: BigDecimal?, order, isFirst ->
                    if (isFirst) {
                        order.quantity
                    } else {
                        accumulator?.plus(order.quantity)
                    }
                }).toSortedMap(orderType.comparator)
    }

    @JvmStatic
    fun main(args: Array<String>) {

        register(Order(UUID.randomUUID(), "user1", BigDecimal(2.5), BigDecimal(306), OrderType.SELL))
        register(Order(UUID.randomUUID(), "user2", BigDecimal(3.5), BigDecimal(306), OrderType.SELL))
        register(Order(UUID.randomUUID(), "user2", BigDecimal(0.5), BigDecimal(306), OrderType.SELL))
        register(Order(UUID.randomUUID(), "user3", BigDecimal(1.5), BigDecimal(307), OrderType.SELL))
        register(Order(UUID.randomUUID(), "user3", BigDecimal(9.5), BigDecimal(304.5), OrderType.SELL))

        register(Order(UUID.randomUUID(), "user2", BigDecimal(1.5), BigDecimal(306), OrderType.BUY))
        register(Order(UUID.randomUUID(), "user2", BigDecimal(3.5), BigDecimal(307), OrderType.BUY))
        register(Order(UUID.randomUUID(), "user5", BigDecimal(1.5), BigDecimal(309), OrderType.BUY))
        register(Order(UUID.randomUUID(), "user5", BigDecimal(3.5), BigDecimal(309), OrderType.BUY))

        println("SELL >> ")
        orderSummary(OrderType.SELL).forEach { price, qtty -> println("$qtty kg for £$price") }
        println("BUY >> ")
        orderSummary(OrderType.BUY).forEach { price, qtty -> println("$qtty kg for £$price") }
    }


}



