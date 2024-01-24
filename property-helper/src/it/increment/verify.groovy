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
def file = new File(basedir, "build.properties")
assert file.exists()
def stream = new FileInputStream(file)
properties.load(stream)

assert properties.size() == 1

def buildNumber = properties.getProperty("build.number", "")
assert buildNumber == "1.2.4"

// test pre-increment values
properties = new Properties()
file = new File(basedir, "target/classes/main.properties")
assert file.exists()
stream = new FileInputStream(file)
properties.load(stream)

assert properties.size() == 4

buildNumber = properties.getProperty("build.number", "")
def buildNumberMajor = properties.getProperty("build.number.major", "")
def buildNumberMinor = properties.getProperty("build.number.minor", "")
def buildNumberPatch = properties.getProperty("build.number.patch", "")

assert buildNumber == "1.2.3"
assert buildNumberMajor == "1"
assert buildNumberMinor == "2"
assert buildNumberPatch == "3"

// test post-increment values
properties = new Properties()
file = new File(basedir, "target/test-classes/test.properties")
assert file.exists()
stream = new FileInputStream(file)
properties.load(stream)

assert properties.size() == 4

buildNumber = properties.getProperty("build.number", "")
buildNumberMajor = properties.getProperty("build.number.major", "")
buildNumberMinor = properties.getProperty("build.number.minor", "")
buildNumberPatch = properties.getProperty("build.number.patch", "")

assert buildNumber == "1.2.4"
assert buildNumberMajor == "1"
assert buildNumberMinor == "2"
assert buildNumberPatch == "4"
