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

import org.joda.time.format.*

def properties = new Properties()
def file = new File(basedir, "target/classes/date.properties")
assert file.exists()
def stream = new FileInputStream(file)
properties.load(stream)

assert properties.size() == 4

def regular = properties.getProperty("regular", "")
def regularUtc = properties.getProperty("regular-utc", "")
def epoch = properties.getProperty("epoch", "")
def epochUtc = properties.getProperty("epoch-utc", "")

def format = DateTimeFormat.forPattern("yyyyMMdd_HHmmss")

def regularDate = format.parseDateTime(regular);
assert regularDate != null

def epochDate = format.parseDateTime(epoch);
assert epochDate != null

def epochUtcDate = format.parseDateTime(epochUtc);
assert epochUtcDate != null

