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
def properties = new Properties()
def file = new File(basedir, "target/classes/uuids.properties")
assert file.exists()
def stream = new FileInputStream(file)
properties.load(stream)

assert properties.size() == 6

def random = properties.getProperty("random", "xxxx")
def fromDefault = properties.getProperty("from-default", "xxxx")
def propValue = properties.getProperty("prop-value", "xxxx")
def propDefaultValue = properties.getProperty("prop-default", "xxxx")
def format1Value = properties.getProperty("format1", "xxxx")
def format2Value = properties.getProperty("format2", "xxxx")

assert !random.equals("xxxx")
assert fromDefault.equals("059cf1d0-435a-49ca-b813-9aad6b56ab39")
assert propValue.equals("bb9ff2e2-fce2-4d46-a857-6f45f4afac95")
assert propDefaultValue.equals("03f87dd5-4bca-4d3c-af54-d02e1acd5563")
assert format1Value.equals("Formatted: 52a78430-144c-4706-b8e8-92c189342f38")
assert format2Value.equals("e613882d")
