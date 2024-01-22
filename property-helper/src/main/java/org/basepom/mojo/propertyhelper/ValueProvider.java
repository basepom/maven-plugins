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

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public interface ValueProvider {

    ValueProvider NULL_PROVIDER = Optional::empty;

    Optional<String> getValue();

    default void setValue(String value) {
    }

    class StaticValueProvider implements ValueProvider {

        private String value;

        StaticValueProvider() {
        }

        @Override
        public Optional<String> getValue() {
            return Optional.ofNullable(value);
        }

        @Override
        public void setValue(final String value) {
            this.value = value;
        }
    }

    class MapValueProvider implements ValueProvider {

        private final Map<String, String> values;
        private final String valueName;

        MapValueProvider(final Map<String, String> values, final String valueName) {
            this.valueName = checkNotNull(valueName, "valueName is null");
            this.values = values;
        }

        @Override
        public Optional<String> getValue() {
            return Optional.ofNullable(values.get(valueName));
        }

        @Override
        public void setValue(final String value) {
            checkNotNull(value, "value is null");
            values.put(valueName, value);
        }
    }

    class PropertyProvider implements ValueProvider {

        private final Properties props;
        private final String propertyName;

        PropertyProvider(final Properties props, final String propertyName) {
            this.props = props;
            this.propertyName = checkNotNull(propertyName, "propertyName is null");
        }

        @Override
        public Optional<String> getValue() {
            return Optional.ofNullable(props.getProperty(propertyName));
        }

        @Override
        public void setValue(final String value) {
            checkNotNull(value, "value is null");
            props.setProperty(propertyName, value);
        }
    }
}

