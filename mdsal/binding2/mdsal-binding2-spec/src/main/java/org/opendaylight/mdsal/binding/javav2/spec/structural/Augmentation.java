/*
 * Copyright (c) 2017 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.mdsal.binding.javav2.spec.structural;

import com.google.common.annotations.Beta;
import org.opendaylight.yangtools.util.ClassLoaderUtils;

@Beta
public interface Augmentation<T> {

    // REPLACES: BindingReflections#findAugmentationTarget
    default Class<T> augmentTarget() {
        return ClassLoaderUtils.findFirstGenericArgument(getClass(), Augmentation.class);
    }

}
