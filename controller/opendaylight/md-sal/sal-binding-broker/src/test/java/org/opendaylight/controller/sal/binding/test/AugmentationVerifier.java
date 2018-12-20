/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.sal.binding.test;

import static org.junit.Assert.assertNotNull;

import org.opendaylight.yangtools.yang.binding.Augmentable;
import org.opendaylight.yangtools.yang.binding.Augmentation;

public class AugmentationVerifier<T extends Augmentable<T>> {

    private final T object;

    public AugmentationVerifier(final T objectToVerify) {
        this.object = objectToVerify;
    }

    public AugmentationVerifier<T> assertHasAugmentation(final Class<? extends Augmentation<T>> augmentation) {
        assertHasAugmentation(object, augmentation);
        return this;
    }

    public static <T extends Augmentable<T>> void assertHasAugmentation(final T object,
            final Class<? extends Augmentation<T>> augmentation) {
        assertNotNull(object);
        assertNotNull("Augmentation " + augmentation.getSimpleName() + " is not present.",
                object.augmentation(augmentation));
    }

    public static <T extends Augmentable<T>> AugmentationVerifier<T> from(final T obj) {
        return new AugmentationVerifier<>(obj);
    }
}
