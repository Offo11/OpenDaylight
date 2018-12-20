/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.sal.binding.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class AbstractBrokerAwareActivator implements BundleActivator {

    private static final ExecutorService MD_ACTIVATION_POOL = Executors.newCachedThreadPool();
    private BundleContext context;
    private ServiceTracker<BindingAwareBroker, BindingAwareBroker> tracker;
    private BindingAwareBroker broker;
    private final ServiceTrackerCustomizer<BindingAwareBroker, BindingAwareBroker> customizer =
            new ServiceTrackerCustomizer<BindingAwareBroker, BindingAwareBroker>() {

        @Override
        public BindingAwareBroker addingService(ServiceReference<BindingAwareBroker> reference) {
            broker = context.getService(reference);
            MD_ACTIVATION_POOL.execute(() -> onBrokerAvailable(broker, context));
            return broker;
        }

        @Override
        public void modifiedService(ServiceReference<BindingAwareBroker> reference, BindingAwareBroker service) {
            removedService(reference, service);
            addingService(reference);
        }

        @Override
        public void removedService(ServiceReference<BindingAwareBroker> reference, BindingAwareBroker service) {
            broker = context.getService(reference);
            MD_ACTIVATION_POOL.execute(() -> onBrokerRemoved(broker, context));
        }

    };


    @Override
    public final void start(BundleContext bundleContext) {
        this.context = bundleContext;
        startImpl(bundleContext);
        tracker = new ServiceTracker<>(bundleContext, BindingAwareBroker.class, customizer);
        tracker.open();

    }



    @Override
    public final  void stop(BundleContext bundleContext) {
        if (tracker != null) {
            tracker.close();
        }
        stopImpl(bundleContext);
    }

    /**
     * Called when this bundle is started (before
     * {@link BindingAwareProvider#onSessionInitiated(ProviderContext)} so the Framework can perform
     * the bundle-specific activities necessary to start this bundle. This
     * method can be used to register services or to allocate any resources that
     * this bundle needs.
     *
     * <p>
     * This method must complete and return to its caller in a timely manner.
     *
     * @param bundleContext
     *            The execution context of the bundle being started.
     * @throws RuntimeException
     *             If this method throws an exception, this bundle is marked as
     *             stopped and the Framework will remove this bundle's
     *             listeners, unregister all services registered by this bundle,
     *             and release all services used by this bundle.
     */
    protected void startImpl(BundleContext bundleContext) {
        // NOOP
    }

    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle. In general, this
     * method should undo the work that the {@code BundleActivator.start} method
     * started. There should be no active threads that were started by this
     * bundle when this bundle returns. A stopped bundle must not call any
     * Framework objects.
     *
     * <p>
     * This method must complete and return to its caller in a timely manner.
     *
     * @param bundleContext The execution context of the bundle being stopped.
     * @throws RuntimeException If this method throws an exception, the bundle is still
     *         marked as stopped, and the Framework will remove the bundle's
     *         listeners, unregister all services registered by the bundle, and
     *         release all services used by the bundle.
     */
    protected void stopImpl(BundleContext bundleContext) {
        // NOOP
    }

    protected abstract void onBrokerAvailable(BindingAwareBroker bindingBroker, BundleContext bundleContext);

    protected void onBrokerRemoved(BindingAwareBroker bindingBroker, BundleContext bundleContext) {
        stopImpl(bundleContext);
    }
}
