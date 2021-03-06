/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.bgp.rib.spi;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;

public interface BGPSessionNegotiatorFactory<S extends BGPSession> {
    SessionNegotiator getSessionNegotiator(Channel channel, Promise<S> promise);
}