/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.basepom.mojo.propertyhelper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import org.apache.maven.model.Model;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import org.codehaus.plexus.interpolation.PrefixedValueSourceWrapper;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;

public final class InterpolatorFactory {

    private static final List<String> SYNONYM_PREFIXES = ImmutableList.of("project", "pom");
    private static final String PREFIX = "#{";
    private static final String POSTFIX = "}";

    private final Model model;

    public InterpolatorFactory(final Model model) {
        this.model = model;
    }

    public String interpolate(final String value, final IgnoreWarnFail onMissingProperty, final Map<String, String> properties)
            throws IOException, InterpolationException {
        checkNotNull(value, "value is null");
        checkNotNull(properties, "properties is null");

        final Interpolator interpolator = new StringSearchInterpolator(PREFIX, POSTFIX);
        interpolator.addValueSource(new EnvarBasedValueSource());
        interpolator.addValueSource(new PropertiesBasedValueSource(System.getProperties()));

        if (model != null) {
            final Model pomModel = model;
            interpolator.addValueSource(new PrefixedValueSourceWrapper(new ObjectBasedValueSource(pomModel),
                    SYNONYM_PREFIXES,
                    true));

            interpolator.addValueSource(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(pomModel.getProperties()),
                    SYNONYM_PREFIXES,
                    true));
        }

        interpolator.addValueSource(new MapBasedValueSource(properties));

        final String result = interpolator.interpolate(value, new PrefixAwareRecursionInterceptor(SYNONYM_PREFIXES, true));
        final String stripped = result.replaceAll(Pattern.quote(PREFIX) + ".*?" + Pattern.quote(POSTFIX), "");
        IgnoreWarnFail.checkState(onMissingProperty, stripped.equals(result), "property");
        return stripped;
    }
}
