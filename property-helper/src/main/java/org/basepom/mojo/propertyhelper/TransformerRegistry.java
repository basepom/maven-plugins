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
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class TransformerRegistry {

    private static final Map<String, Function<String, String>> REGISTRY;

    static {
        final ImmutableMap.Builder<String, Function<String, String>> registry = ImmutableMap.builder();
        registry.put("lowercase", new LowercaseTransformer());
        registry.put("uppercase", new UppercaseTransformer());
        registry.put("remove_whitespace", new RemoveWhitespaceTransformer());
        registry.put("underscore_for_whitespace", new UnderscoreForWhitespaceTransformer());
        registry.put("dash_for_whitespace", new DashForWhitespaceTransformer());
        registry.put("use_underscore", new UseUnderscoreTransformer());
        registry.put("use_dash", new UseDashTransformer());
        registry.put("trim", new TrimTransformer());

        REGISTRY = registry.build();
    }

    private TransformerRegistry() {
        throw new AssertionError("Do not instantiate");
    }

    public static List<Function<String, String>> getTransformers(final String transformerNames) {
        if (transformerNames == null) {
            return ImmutableList.of();
        }

        ImmutableList.Builder<Function<String, String>> transformers = ImmutableList.builder();
        for (String transformerName : Splitter.on(',').omitEmptyStrings().split(transformerNames)) {
            transformers.add(forName(transformerName));
        }

        return transformers.build();
    }

    public static String applyTransformers(final String transformers, final String value) {
        String res = value;
        for (Function<String, String> transformer : getTransformers(transformers)) {
            if (res != null) {
                res = transformer.apply(res);
            }
        }
        return res;
    }

    public static Function<String, String> forName(final String transformerName) {
        checkNotNull(transformerName, "transformerName is null");

        final var transformer = REGISTRY.get(transformerName.toLowerCase(Locale.ENGLISH));

        checkState(transformer != null, "Transformer '%s' is unknown.", transformerName);

        return transformer;
    }

    public static class LowercaseTransformer
            implements Function<String, String> {

        @Override
        public String apply(@Nonnull String value) {
            return value.toLowerCase(Locale.ENGLISH);
        }
    }

    public static class UppercaseTransformer
            implements Function<String, String> {

        @Override
        public String apply(@Nonnull String value) {
            return value.toUpperCase(Locale.ENGLISH);
        }
    }

    public static class RemoveWhitespaceTransformer
            implements Function<String, String> {

        @Override
        public String apply(@Nonnull String value) {
            return CharMatcher.whitespace().removeFrom(value);
        }
    }

    public static class UnderscoreForWhitespaceTransformer
            implements Function<String, String> {

        @Override
        public String apply(@Nonnull String value) {
            return CharMatcher.whitespace().collapseFrom(value, '_');
        }
    }

    public static class DashForWhitespaceTransformer
            implements Function<String, String> {

        @Override
        public String apply(@Nonnull String value) {
            return CharMatcher.whitespace().collapseFrom(value, '-');
        }
    }

    public static class UseUnderscoreTransformer
            implements Function<String, String> {

        @Override
        public String apply(@Nonnull String value) {
            return CharMatcher.whitespace().or(CharMatcher.anyOf("-_")).collapseFrom(value, '_');
        }
    }

    public static class UseDashTransformer
            implements Function<String, String> {

        @Override
        public String apply(@Nonnull String value) {
            return CharMatcher.whitespace().or(CharMatcher.anyOf("-_")).collapseFrom(value, '-');
        }
    }

    public static class TrimTransformer
            implements Function<String, String> {

        @Override
        public String apply(@Nonnull String value) {
            return CharMatcher.whitespace().trimFrom(value);
        }
    }
}
