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
def file = new File(basedir, "target/classes/strings.properties")
assert file.exists()
def stream = new FileInputStream(file)
properties.load(stream)

assert properties.size() == 6

def regular = properties.getProperty("regular", "xxxx")
def skipBlank = properties.getProperty("skip-blank", "xxxx")
def acceptBlank = properties.getProperty("accept-blank", "xxxx")
def nullValue = properties.getProperty("null-value", "xxxx")
def propValue = properties.getProperty("prop-value", "xxxx")
def propDefaultValue = properties.getProperty("prop-default", "xxxx")

assert regular.equals("regular-value")
assert skipBlank.equals("skip-blank-value")
assert acceptBlank.equals("")
assert nullValue.equals("")
assert propValue.equals("from a properties file")
assert propDefaultValue.equals("prop-default-value")
