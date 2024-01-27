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
/**
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


import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

def properties = new Properties()
def file = new File(basedir, "target/classes/date.properties")
assert file.exists()
def stream = new FileInputStream(file)
properties.load(stream)

assert properties.size() == 5

def regular = properties.getProperty("regular", "")
def regularUtc = properties.getProperty("regular-utc", "")
def epoch = properties.getProperty("epoch", "")
def epochUtc = properties.getProperty("epoch-utc", "")

// pattern must match the pattern in the pom
def format = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneId.systemDefault())
def formatUtc = format.withZone(ZoneOffset.UTC)

def regularDate = ZonedDateTime.parse(regular, format)
assert regularDate != null

def regularUtcDate = ZonedDateTime.parse(regularUtc, formatUtc)
assert regularUtcDate != null

def epochDate = ZonedDateTime.parse(epoch, format)
assert epochDate != null

def epochUtcDate = ZonedDateTime.parse(epochUtc, formatUtc)
assert epochUtcDate != null

assert epochUtcDate == ZonedDateTime.parse("1970-01-01T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
assert epochDate == ZonedDateTime.parse("1970-01-01T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault()))

// equal in different time zones
assert regularDate != regularUtcDate
assert regularDate.toString() != regularUtcDate.toString()

assert epochDate != regularDate
assert epochUtcDate != regularUtcDate
