/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.md.sal.dom.broker.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.controller.md.sal.dom.api.DOMDataWriteTransaction;
import org.opendaylight.controller.sal.core.spi.data.DOMStore;
import org.opendaylight.controller.sal.core.spi.data.DOMStoreThreePhaseCommitCohort;
import org.opendaylight.yangtools.util.DurationStatisticsTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of blocking three phase commit coordinator, which which
 * supports coordination on multiple {@link DOMStoreThreePhaseCommitCohort}.
 *
 * <p>
 * This implementation does not support cancellation of commit,
 *
 * <p>
 * In order to advance to next phase of three phase commit all subtasks of
 * previous step must be finish.
 *
 * <p>
 * This executor does not have an upper bound on subtask timeout.
 */
public class SerializedDOMDataBroker extends AbstractDOMDataBroker {
    private static final Logger LOG = LoggerFactory.getLogger(SerializedDOMDataBroker.class);
    private final DurationStatisticsTracker commitStatsTracker = DurationStatisticsTracker.createConcurrent();
    private final ListeningExecutorService executor;

    /**
     * Construct DOMDataCommitCoordinator which uses supplied executor to
     * process commit coordinations.
     *
     * @param datastores data stores
     * @param executor executor service
     */
    public SerializedDOMDataBroker(final Map<LogicalDatastoreType, DOMStore> datastores,
                                   final ListeningExecutorService executor) {
        super(datastores);
        this.executor = Preconditions.checkNotNull(executor, "executor must not be null.");
    }

    public DurationStatisticsTracker getCommitStatsTracker() {
        return commitStatsTracker;
    }

    @Override
    protected <T> ListenableFuture<T> commit(final DOMDataWriteTransaction transaction,
            final Collection<DOMStoreThreePhaseCommitCohort> cohorts, final Supplier<T> futureValueSupplier) {
        Preconditions.checkArgument(transaction != null, "Transaction must not be null.");
        Preconditions.checkArgument(cohorts != null, "Cohorts must not be null.");
        LOG.debug("Tx: {} is submitted for execution.", transaction.getIdentifier());

        ListenableFuture<T> commitFuture;
        try {
            commitFuture = executor.submit(new CommitCoordinationTask<>(transaction, cohorts, commitStatsTracker,
                    futureValueSupplier));
        } catch (RejectedExecutionException e) {
            LOG.error("The commit executor {} queue is full - submit task was rejected. \n", executor, e);
            commitFuture = Futures.immediateFailedFuture(new TransactionCommitFailedException(
                    "Could not submit the commit task - the commit queue capacity has been exceeded.", e));
        }

        return commitFuture;
    }
}