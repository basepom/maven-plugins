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
package org.basepom.mojo.propertyhelper.fields;

import static org.assertj.core.api.Assertions.assertThat;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.dateDefinition;
import static org.basepom.mojo.propertyhelper.definitions.DefinitionHelper.setInitialValue;

import org.basepom.mojo.propertyhelper.FieldContext;
import org.basepom.mojo.propertyhelper.ValueCache;
import org.basepom.mojo.propertyhelper.ValueProvider.SingleValueProvider;
import org.basepom.mojo.propertyhelper.definitions.DateDefinition;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

class TestDateFieldOverrides {

    @Test
    void testZeroValue() throws Exception {
        DateDefinition dateDefinition = dateDefinition("hello");
        setInitialValue(dateDefinition, "0");

        ZonedDateTime localEpochTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());

        var context = FieldContext.forTesting();
        ValueCache valueCache = new ValueCache();
        var dateField = dateDefinition.createField(context, valueCache);
        var value = dateField.getValue();

        assertThat(value).isEqualTo(DateTimeFormatter.ISO_DATE_TIME.format(localEpochTime));
    }

    @Test
    void testLoadedValueOverridesInitialValue() throws Exception {
        DateDefinition dateDefinition = dateDefinition("hello");
        setInitialValue(dateDefinition, "0");

        ZonedDateTime localEpochPlusOneTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1), ZoneId.systemDefault());

        var valueProvider = new SingleValueProvider();
        valueProvider.setValue("1");
        var dateField = DateField.forTesting(dateDefinition, valueProvider);

        var value = dateField.getValue();

        assertThat(value).isEqualTo(DateTimeFormatter.ISO_DATE_TIME.format(localEpochPlusOneTime));
    }
}
