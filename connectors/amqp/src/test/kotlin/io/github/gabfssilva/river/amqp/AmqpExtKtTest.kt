package io.github.gabfssilva.river.amqp

import com.rabbitmq.client.BuiltinExchangeType
import io.github.gabfssilva.river.core.toList
import io.github.gabfssilva.river.core.via
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class AmqpExtKtTest : FeatureSpec({
    feature("AMQP flow") {
        val connection =
            nonBlockingConnectionFactory { setUri("amqp://admin:admin@localhost:5672") }
                .connection()

        beforeTest {
            connection.channel {
                queueDelete("hello.world")
                queueDeclare("hello.world", false, false, false, emptyMap())
                exchangeDeclare("hello", BuiltinExchangeType.DIRECT, true)
                queueBind("hello.world", "hello", "world")
            }
        }

        scenario("Basic publish flow") {
            (1..1000)
                .asFlow()
                .map { Message.Simple(it.toString().toByteArray()) }
                .via { connection.publishFlow("hello", "world") }
                .collect()

            val count =
                connection
                    .channel { queueDeclarePassive("hello.world").messageCount }

            count shouldBe 1000
        }

        scenario("Basic message consuming") {
            (1..1000)
                .asFlow()
                .map { Message.Simple(it.toString().toByteArray()) }
                .via { connection.publishFlow("hello", "world") }
                .collect()

            val numbers = connection
                .consume("hello.world")
                .map {
                    it.ack()
                    it.bodyAsString().toInt()
                }
                .toList(1000)

            numbers shouldContainAll (1..1000).toList()

            val count =
                connection.channel { queueDeclarePassive("hello.world").messageCount }

            count shouldBe 0
        }
    }
})