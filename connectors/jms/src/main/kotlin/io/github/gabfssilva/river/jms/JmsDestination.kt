package io.github.gabfssilva.river.jms

import javax.jms.Destination
import javax.jms.JMSContext

sealed class JmsDestination(
    val destination: JMSContext.() -> Destination
) {
    class Queue(name: String) : JmsDestination({ createQueue(name) })
    class Topic(name: String) : JmsDestination({ createTopic(name) })
}
