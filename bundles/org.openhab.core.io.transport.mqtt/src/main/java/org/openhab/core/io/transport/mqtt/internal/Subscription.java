/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.core.io.transport.mqtt.internal;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.io.transport.mqtt.MqttMessageSubscriber;
import org.openhab.core.util.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

/**
 * This class keeps track of all the subscribers to a specific topic.
 * <p>
 * <b>Retained</b> messages for the topic are stored so they can be replayed to new subscribers.
 *
 * @author Jochen Klein - Initial contribution
 */
@NonNullByDefault
public class Subscription {
    private final Logger logger = LoggerFactory.getLogger(Subscription.class);
    private final Map<String, byte[]> retainedMessages = new ConcurrentHashMap<>();
    private final Collection<MqttMessageSubscriber> subscribers = ConcurrentHashMap.newKeySet();

    /**
     * Add a new subscriber.
     * <p>
     * If there are any retained messages, they will be delivered to the subscriber.
     *
     * @param subscriber
     */
    public void add(MqttMessageSubscriber subscriber) {
        if (subscribers.add(subscriber)) {
            // new subscriber. deliver all known retained messages
            retainedMessages.entrySet().stream().forEach(entry -> {
                if (entry.getValue().length > 0) {
                    processMessage(subscriber, entry.getKey(), entry.getValue());
                }
            });
        }
    }

    /**
     * Remove a subscriber from the list.
     *
     * @param subscriber
     */
    public void remove(MqttMessageSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public boolean isEmpty() {
        return subscribers.isEmpty();
    }

    public void messageArrived(Mqtt3Publish message) {
        messageArrived(message.getTopic().toString(), message.getPayloadAsBytes(), message.isRetain());
    }

    public void messageArrived(Mqtt5Publish message) {
        messageArrived(message.getTopic().toString(), message.getPayloadAsBytes(), message.isRetain());
    }

    public void messageArrived(String topic, byte[] payload, boolean retain) {
        // http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc385349265
        // Only the first message delivered will have the retain flag; subsequent messages
        // will not have the flag set. So see if we retained it in the past, and continue
        // to retain it (even if it's now empty - we need to know to continue to retain it)
        if (retain || retainedMessages.containsKey(topic)) {
            retainedMessages.put(topic, payload);
        }
        subscribers.forEach(subscriber -> processMessage(subscriber, topic, payload));
    }

    private void processMessage(MqttMessageSubscriber subscriber, String topic, byte[] payload) {
        try {
            subscriber.processMessage(topic, payload);
        } catch (RuntimeException e) {
            logger.warn("A subscriber of type '{}' failed to process message '{}' to topic '{}'.",
                    subscriber.getClass(), HexUtils.bytesToHex(payload), topic);
        }
    }
}
