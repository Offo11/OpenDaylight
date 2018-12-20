/*
 * Copyright (c) 2017 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.mdsal.binding.javav2.model.api;

import com.google.common.annotations.Beta;
import org.opendaylight.yangtools.util.AbstractStringIdentifier;

@Beta
public class UnitName extends AbstractStringIdentifier<UnitName> {

    public UnitName(final String string) {
        super(string);
    }

    String getString() {
        return getValue();
    }
}
