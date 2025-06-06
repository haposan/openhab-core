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
package org.openhab.core.events.system;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.events.AbstractEventFactory;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventFactory;
import org.osgi.service.component.annotations.Component;

/**
 * Factory that creates system events.
 *
 * @author Kai Kreuzer - Initial contribution
 */
@Component(immediate = true, service = EventFactory.class)
@NonNullByDefault
public class SystemEventFactory extends AbstractEventFactory {

    static final String SYSTEM_STARTLEVEL_TOPIC = "openhab/system/startlevel";

    public SystemEventFactory() {
        super(Set.of(StartlevelEvent.TYPE));
    }

    /**
     * Creates a trigger event from a {@link org.openhab.core.types.Type}.
     *
     * @param startlevel Startlevel of system
     * @return Created start level event.
     */
    public static StartlevelEvent createStartlevelEvent(Integer startlevel) {
        SystemEventPayloadBean bean = new SystemEventPayloadBean(startlevel);
        String payload = serializePayload(bean);
        return new StartlevelEvent(SYSTEM_STARTLEVEL_TOPIC, payload, null, startlevel);
    }

    @Override
    protected Event createEventByType(String eventType, String topic, String payload, @Nullable String source)
            throws Exception {
        return createStartlevelEvent(topic, payload, source);
    }

    /**
     * Creates a startlevel event from a payload.
     *
     * @param topic Event topic
     * @param source Event source
     * @param payload Payload
     * @return created startlevel event
     */
    public StartlevelEvent createStartlevelEvent(String topic, String payload, @Nullable String source) {
        SystemEventPayloadBean bean = deserializePayload(payload, SystemEventPayloadBean.class);
        return new StartlevelEvent(topic, payload, source, bean.getStartlevel());
    }

    /**
     * This is a java bean that is used to serialize/deserialize system event payload.
     */
    public static class SystemEventPayloadBean {
        private @NonNullByDefault({}) Integer startlevel;

        /**
         * Default constructor for deserialization e.g. by Gson.
         */
        protected SystemEventPayloadBean() {
        }

        public SystemEventPayloadBean(Integer startlevel) {
            this.startlevel = startlevel;
        }

        public Integer getStartlevel() {
            return startlevel;
        }
    }
}
