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
package org.openhab.core.thing.xml.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

/**
 * @author Simon Kaufmann - Initial contribution
 */
@NonNullByDefault
public class XmlHelperTest {

    @Test
    public void whenUIDContainsDotShouldBeconvcertedToColon() {
        assertThat(XmlHelper.getSystemUID("system.test"), is("system:test"));
    }

    @Test
    public void whenNoPrefixIsGivenShouldPrependSystemPrefix() {
        assertThat(XmlHelper.getSystemUID("test"), is("system:test"));
    }
}
