/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowplugin.impl.statistics.services.direct;

import org.opendaylight.openflowplugin.api.openflow.device.DeviceContext;
import org.opendaylight.openflowplugin.api.openflow.device.RequestContextStack;
import org.opendaylight.openflowplugin.impl.datastore.MultipartWriterProvider;
import org.opendaylight.openflowplugin.openflow.md.core.sal.convertor.ConvertorExecutor;
import org.opendaylight.yang.gen.v1.urn.opendaylight.direct.statistics.rev160511.GetMeterStatisticsInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.direct.statistics.rev160511.GetMeterStatisticsOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.MultipartType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;

/**
 * The Meter direct statistics service.
 */
public abstract class AbstractMeterDirectStatisticsService<T extends OfHeader> extends
        AbstractDirectStatisticsService<GetMeterStatisticsInput, GetMeterStatisticsOutput, T> {

    public AbstractMeterDirectStatisticsService(final RequestContextStack requestContextStack,
                                                final DeviceContext deviceContext,
                                                final ConvertorExecutor convertorExecutor,
                                                final MultipartWriterProvider statisticsWriterProvider) {
        super(MultipartType.OFPMPMETER, requestContextStack, deviceContext, convertorExecutor,
                statisticsWriterProvider);
    }

}
