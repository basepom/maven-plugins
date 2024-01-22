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
import static java.lang.String.format;
import static org.basepom.mojo.propertyhelper.IgnoreWarnFail.checkIgnoreWarnFailState;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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
        this.model = checkNotNull(model, "model is null");
    }

    public String interpolate(final String name, final String value, final IgnoreWarnFail onMissingProperty, final Map<String, String> properties)
            throws IOException, InterpolationException {
        checkNotNull(name, "name is null");
        checkNotNull(value, "value is null");
        checkNotNull(properties, "properties is null");

        final Interpolator interpolator = new StringSearchInterpolator(PREFIX, POSTFIX);
        interpolator.addValueSource(new EnvarBasedValueSource());
        interpolator.addValueSource(new PropertiesBasedValueSource(System.getProperties()));

        interpolator.addValueSource(new PrefixedValueSourceWrapper(new ObjectBasedValueSource(model),
                SYNONYM_PREFIXES,
                true));

        interpolator.addValueSource(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(model.getProperties()),
                SYNONYM_PREFIXES,
                true));

        interpolator.addValueSource(new MapBasedValueSource(properties));

        final String result = interpolator.interpolate(value, new PrefixAwareRecursionInterceptor(SYNONYM_PREFIXES, true));

        Matcher matcher = Pattern.compile(Pattern.quote(PREFIX) + ".*?" + Pattern.quote(POSTFIX)).matcher(result);

        checkIgnoreWarnFailState(!matcher.find(), onMissingProperty,
            () -> format("template %s evaluated to %s", value, result),
            () -> format("could not evaluate %s! (result is %s)", value, result));

        return matcher.replaceAll("");
    }
}
