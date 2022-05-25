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

def prop_size = 5

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
assert !orig.equals("xxxx")

def transform = properties.getProperty("os_name", "xxxx")
assert !transform.equals("xxxx")

def group_transform = properties.getProperty("group.os_name", "xxxx")
assert !group_transform.equals("xxxx")

def late_group_transform = properties.getProperty("late_group.os_name", "xxxx")
assert !late_group_transform.equals("xxxx")

def really_late_group_transform = properties.getProperty("really_late_group.os_name", "xxxx")
assert !really_late_group_transform.equals("xxxx")

assert transform.equals(orig.replace(" ", "").toLowerCase(Locale.ENGLISH))
assert transform.equals(group_transform)
assert transform.equals(late_group_transform)
assert transform.equals(really_late_group_transform)
