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
def file = new File(basedir, "target/classes/numbers.properties")
assert file.exists()
def stream = new FileInputStream(file)
properties.load(stream)

assert properties.size() == 4

def version = properties.getProperty("my-version", "")
def maj = properties.getProperty("my-version", "")
def min = properties.getProperty("my-version", "")
def rev = properties.getProperty("my-version", "")

assert version.equals("1.2.3")
assert maj.equals("1.2.3")
assert min.equals("1.2.3")
assert rev.equals("1.2.3")
