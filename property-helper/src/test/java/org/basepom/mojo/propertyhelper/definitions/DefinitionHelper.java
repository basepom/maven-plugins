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
package org.basepom.mojo.propertyhelper.definitions;

import java.io.File;
import java.util.Arrays;

public final class DefinitionHelper {

    public static UuidDefinition uuidDefinition(String id) {
        return new UuidDefinition(id);
    }

    public static NumberDefinition numberDefinition(String id) {
        return new NumberDefinition(id);
    }

    public static DateDefinition dateDefinition(String id) {
        return new DateDefinition(id);
    }

    public static StringDefinition stringDefinition(String id) {
        return new StringDefinition(id);
    }

    public static PropertyGroupDefinition propertyGroupDefinition(String id) {
        return new PropertyGroupDefinition(id);
    }

    public static PropertyDefinition propertyDefinition(String name, String value) {
        return new PropertyDefinition(name, value);
    }

    public static void setOnMissingProperty(FieldDefinition<?> fieldDefinition, String value) {
        fieldDefinition.setOnMissingProperty(value);
    }

    public static void setOnMissingFileProperty(FieldDefinition<?> fieldDefinition, String value) {
        fieldDefinition.setOnMissingFileProperty(value);
    }

    public static void setOnMissingFile(FieldDefinition<?> fieldDefinition, String value) {
        fieldDefinition.setOnMissingFile(value);
    }

    public static void setPropertyFile(FieldDefinition<?> fieldDefinition, File value) {
        fieldDefinition.propertyFile = value;
    }

    public static void setInitialValue(FieldDefinition<?> fieldDefinition, String initialValue) {
        fieldDefinition.initialValue = initialValue;
    }

    // uuid helpers
    public static void setValue(UuidDefinition uuidDefinition, String value) {
        uuidDefinition.value = value;
    }

    // date helpers
    public static void setValue(DateDefinition dateDefinition, Long value) {
        dateDefinition.value = value;
    }

    public static void setTimezone(DateDefinition dateDefinition, String timezone) {
        dateDefinition.timezone = timezone;
    }

    public static void setFormat(DateDefinition dateDefinition, String format) {
        dateDefinition.format = format;
    }

    // number helpers
    public static void setFieldNumber(NumberDefinition numberDefinition, int fieldNumber) {
        numberDefinition.fieldNumber = fieldNumber;
    }

    // string helpers

    public static void setValues(StringDefinition stringDefinition, String ... values) {
        stringDefinition.setValues(Arrays.asList(values));
    }

    public static void setBlankIsValid(StringDefinition stringDefinition, boolean blankIsValid) {
        stringDefinition.blankIsValid = blankIsValid;
    }

    public static void setOnMissingValue(StringDefinition stringDefinition, String onMissingValue) {
        stringDefinition.setOnMissingValue(onMissingValue);
    }

    // property group helpers
    public static void setProperties(PropertyGroupDefinition propertyGroupDefinition, PropertyDefinition ... propertyDefinitions) {
        propertyGroupDefinition.setProperties(propertyDefinitions);
    }

    public static void setOnMissingField(PropertyGroupDefinition propertyGroupDefinition, String value) {
        propertyGroupDefinition.setOnMissingField(value);
    }
}
