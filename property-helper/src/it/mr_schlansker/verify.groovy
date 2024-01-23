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

import java.util.Locale

def prop_size = 6

def loadProperties(String fileName) {
  def properties = new Properties()
  def file = new File(basedir, fileName)
  assert file.exists()
  def stream = new FileInputStream(file)
  properties.load(stream)
  return properties
}


def properties = loadProperties("target/classes/result.properties")
assert properties.size() == prop_size

def orig = properties.getProperty("os.name", "xxxx")
assert orig != "xxxx"

def transform = properties.getProperty("os_name", "xxxx")
assert transform != "xxxx"

def really_late_os_name = properties.getProperty("really_late_os_name", "xxxx")
assert really_late_os_name != "xxxx"

def group_transform = properties.getProperty("group.os_name", "xxxx")
assert group_transform != "xxxx"

def late_group_transform = properties.getProperty("late_group.os_name", "xxxx")
assert late_group_transform != "xxxx"

def really_late_group_transform = properties.getProperty("really_late_group.os_name", "xxxx")
assert really_late_group_transform != "xxxx"

assert transform == orig.replace(" ", "").toLowerCase(Locale.getDefault())
assert really_late_os_name == transform
assert transform == group_transform
assert transform == late_group_transform
assert transform == really_late_group_transform
