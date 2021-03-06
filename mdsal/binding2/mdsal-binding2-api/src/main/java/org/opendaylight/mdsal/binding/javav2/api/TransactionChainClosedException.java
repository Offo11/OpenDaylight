/*
 * Copyright (c) 2018 Pantheon Technologies, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.javav2.api;

/**
 * Exception thrown when an attempt is made to open a new transaction in a closed chain.
 */
public final class TransactionChainClosedException extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public TransactionChainClosedException(final String message) {
        super(message);
    }

    public TransactionChainClosedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
