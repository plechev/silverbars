package com.silverbars.shop.service

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import com.silverbars.shop.domain.Order
import com.silverbars.shop.domain.OrderType
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class OrderServiceTest {

    val underTest = OrderService

    @BeforeEach
    fun init() {
        storage.clear()
    }

    @TestFactory
    fun `should register new order`(): Collection<DynamicTest> {
        val validOrders = listOf(
                Order(UUID.randomUUID(), "user1", BigDecimal(2.5), BigDecimal(306.0), OrderType.SELL),
                Order(UUID.randomUUID(), "user2", BigDecimal(5.0), BigDecimal(304.5), OrderType.BUY)
        )

        return validOrders.map {
            DynamicTest.dynamicTest("${it.type} order created for ${it.quantity}kg at £${it.pricePerKg}/kg") {
                underTest.register(it)
                assert.that(storage.containsKey(it.id), equalTo(true))
            }
        }.toList()
    }

    @TestFactory
    fun `should cancel existing order`(): Collection<DynamicTest> {
        val existingOrders = listOf(
                Order(UUID.randomUUID(), "user1", BigDecimal(2.5), BigDecimal(306.0), OrderType.SELL),
                Order(UUID.randomUUID(), "user2", BigDecimal(5.0), BigDecimal(304.5), OrderType.BUY)
        )
        existingOrders.forEach {
            storage.put(it.id, it)
        }
        Assumptions.assumeTrue(storage.size == 2)

        return existingOrders.map {
            DynamicTest.dynamicTest("${it.type} order cancelled for ID=${it.id}") {
                val removed = underTest.cancel(it.id)
                assert.that(removed, present())
                assert.that(storage.containsKey(it.id), equalTo(false))
            }
        }.toList()
    }


    @Test
    fun `should create summary of SELL orders sorted by price ascending`() {

        val existingOrders = listOf(
                Order(UUID.randomUUID(), "user1", BigDecimal(2.5), BigDecimal(306.0), OrderType.SELL),
                Order(UUID.randomUUID(), "user2", BigDecimal(3.0), BigDecimal(304.5), OrderType.SELL),
                Order(UUID.randomUUID(), "user2", BigDecimal(5.0), BigDecimal(307.5), OrderType.SELL),
                Order(UUID.randomUUID(), "user2", BigDecimal(3.0), BigDecimal(307.5), OrderType.SELL),
                Order(UUID.randomUUID(), "user3", BigDecimal(5.0), BigDecimal(307.5), OrderType.BUY)
        )
        existingOrders.forEach {
            storage.put(it.id, it)
        }
        Assumptions.assumeTrue(storage.size == 5)

        val summary = underTest.orderSummary(OrderType.SELL)
        assert.that(summary.toList(), equalTo(listOf(
                Pair<BigDecimal, BigDecimal?>(BigDecimal(304.5), BigDecimal(3)),
                Pair<BigDecimal, BigDecimal?>(BigDecimal(306), BigDecimal(2.5)),
                Pair<BigDecimal, BigDecimal?>(BigDecimal(307.5), BigDecimal(8))
        )))
    }


    @Test
    fun `should create summary of BUY orders sorted by price descending`() {

        val existingOrders = listOf(
                Order(UUID.randomUUID(), "user1", BigDecimal(2.5), BigDecimal(306.0), OrderType.BUY),
                Order(UUID.randomUUID(), "user2", BigDecimal(3.0), BigDecimal(304.5), OrderType.BUY),
                Order(UUID.randomUUID(), "user2", BigDecimal(5.0), BigDecimal(307.5), OrderType.BUY),
                Order(UUID.randomUUID(), "user2", BigDecimal(3.0), BigDecimal(307.5), OrderType.BUY),
                Order(UUID.randomUUID(), "user3", BigDecimal(5.0), BigDecimal(307.5), OrderType.SELL)
        )
        existingOrders.forEach {
            storage.put(it.id, it)
        }
        Assumptions.assumeTrue(storage.size == 5)

        val summary = underTest.orderSummary(OrderType.BUY)
        assert.that(summary.toList(), equalTo(listOf(
                Pair<BigDecimal, BigDecimal?>(BigDecimal(307.5), BigDecimal(8)),
                Pair<BigDecimal, BigDecimal?>(BigDecimal(306), BigDecimal(2.5)),
                Pair<BigDecimal, BigDecimal?>(BigDecimal(304.5), BigDecimal(3))
        )))
    }

}
