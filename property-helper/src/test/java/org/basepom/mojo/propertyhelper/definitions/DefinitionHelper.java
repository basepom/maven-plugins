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

import com.google.common.collect.ImmutableList;

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

    public static void setOnMissingProperty(AbstractDefinition baseDefinition, String value) {
        baseDefinition.setOnMissingProperty(value);
    }

    public static void setOnMissingFile(AbstractDefinition baseDefinition, String value) {
        baseDefinition.setOnMissingFile(value);
    }

    public static void setPropertyFile(AbstractDefinition baseDefinition, File value) {
        baseDefinition.propertyFile = value;
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
}
