/*
 * Copyright (c) 2017 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.javav2.java.api.generator.range_generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import org.opendaylight.yangtools.yang.model.api.type.RangeConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractPrimitiveRangeGenerator<T extends Number & Comparable<T>> extends AbstractRangeGenerator<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPrimitiveRangeGenerator.class);
    private final String primitiveName;
    private final T minValue;
    private final T maxValue;

    protected AbstractPrimitiveRangeGenerator(final Class<T> typeClass, final String primitiveName, final T minValue,
            final T maxValue) {
        super(typeClass);
        this.primitiveName = Preconditions.checkNotNull(primitiveName);
        this.minValue = Preconditions.checkNotNull(minValue);
        this.maxValue = Preconditions.checkNotNull(maxValue);
    }

    /**
     * Return the name of the primitive type, as known by the Java language.
     *
     * @return Primitive type name
     */
    @Nonnull protected final String getPrimitiveName() {
        return primitiveName;
    }

    private boolean needsMaximumEnforcement(final T maxToEnforce) {
        return maxValue.compareTo(maxToEnforce) > 0;
    }

    private boolean needsMinimumEnforcement(final T minToEnforce) {
        return minValue.compareTo(minToEnforce) < 0;
    }

    /**
     * Format a value into a Java-compilable 'greater equal' compare expression.
     * The method would be overrided by {@link AbstractUnsignedIntegerRangeGenerator}.
     *
     * @param value Number value
     * @return Java language compare string representation
     */
    @Nonnull protected String gtExpression(T value) {
        return "value >= " + format(value);
    }

    /**
     * Format a value into a Java-compilable 'less equal' compare expression.
     * The method would be overrided by {@link AbstractUnsignedIntegerRangeGenerator}.
     *
     * @param value Number value
     * @return Java language compare string representation
     */
    @Nonnull protected String ltExpression(T value) {
        return "value <= " + format(value);
    }

    private Collection<String> createExpressions(final RangeConstraint<?> constraint) {
        final Set<? extends Range<? extends Number>> constraints = constraint.getAllowedRanges().asRanges();
        final Collection<String> ret = new ArrayList<>(constraints.size());

        for (Range<? extends Number> r : constraints) {
            final T min = getValue(r.lowerEndpoint());
            final boolean needMin = needsMinimumEnforcement(min);

            final T max = getValue(r.upperEndpoint());
            final boolean needMax = needsMaximumEnforcement(max);

            if (!needMin && !needMax) {
                LOG.debug("Type {} indicates [{}, {}] does not require enforcement", getTypeName(), min, max);
                continue;
            }

            final StringBuilder sb = new StringBuilder();
            if (needMin) {
                sb.append(gtExpression(min));
            }
            if (needMax) {
                if (needMin) {
                    sb.append(" && ");
                }
                sb.append(ltExpression(max));
            }

            ret.add(sb.toString());
        }

        return ret;
    }

    private static String createRangeString(final RangeConstraint<?> constraints) {
        return constraints.getAllowedRanges().toString();
    }

    @Override
    protected final String generateRangeCheckerImplementation(final String checkerName,
            final RangeConstraint<?> constraint) {
        final StringBuilder sb = new StringBuilder();
        final Collection<String> expressions = createExpressions(constraint);

        sb.append("private static void ").append(checkerName).append("(final ").append(primitiveName)
            .append(" value) {\n");

        if (!expressions.isEmpty()) {
            for (String exp : expressions) {
                sb.append("    if (").append(exp).append(") {\n");
                sb.append("        return;\n");
                sb.append("    }\n");
            }

            sb.append("    throw new IllegalArgumentException(String.format(\"Invalid range: %s, expected: ")
              .append(createRangeString(constraint)).append(".\", value));\n");
        }

        sb.append("}\n");

        return sb.toString();
    }
}