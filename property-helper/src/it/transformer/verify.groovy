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

import org.joda.time.DateTime
import java.util.Locale

def prop_size = 12

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

def stringProperties = loadProperties("strings.properties")
assert stringProperties.size() == 1
def val = stringProperties.getProperty("value", "xxxx")
assert !val.equals("xxxx")

def value = properties.getProperty("value", "xxxx")
assert !value.equals("xxxx")
assert value.trim().equals(val.trim())

def lowercase = properties.getProperty("lowercase", "xxxx")
assert !lowercase.equals("xxxx")
assert lowercase.trim().equals(val.toLowerCase(Locale.ENGLISH).trim())

def uppercase = properties.getProperty("uppercase", "xxxx")
assert !uppercase.equals("xxxx")
assert uppercase.trim().equals(val.toUpperCase(Locale.ENGLISH).trim())

def remove_whitespace = properties.getProperty("remove_whitespace", "xxxx")
assert !remove_whitespace.equals("xxxx")
assert remove_whitespace.equals("Hello,World:This-is_A-Test!")

def underscore_for_whitespace = properties.getProperty("underscore_for_whitespace", "xxxx")
assert !underscore_for_whitespace.equals("xxxx")
assert underscore_for_whitespace.equals("_Hello,_World:_This-is_A-Test!_")

def dash_for_whitespace = properties.getProperty("dash_for_whitespace", "xxxx")
assert !dash_for_whitespace.equals("xxxx")
assert dash_for_whitespace.equals("-Hello,-World:-This-is_A-Test!-")

def use_underscore = properties.getProperty("use_underscore", "xxxx")
assert !use_underscore.equals("xxxx")
assert use_underscore.equals("_Hello,_World:_This_is_A_Test!_")

def use_dash = properties.getProperty("use_dash", "xxxx")
assert !use_dash.equals("xxxx")
assert use_dash.equals("-Hello,-World:-This-is-A-Test!-")

def trim = properties.getProperty("trim", "xxxx")
assert !trim.equals("xxxx")
assert trim.equals(val.trim())

def combined = properties.getProperty("combined", "xxxx")
assert !combined.equals("xxxx")
assert combined.trim().equals("-HELLO,-WORLD:-THIS-IS-A-TEST!-")

def trim_first = properties.getProperty("trim_first", "xxxx")
assert !trim_first.equals("xxxx")
assert trim_first.equals("Hello,_World:_This_is_A_Test!")

def trim_last = properties.getProperty("trim_last", "xxxx")
assert !trim_last.equals("xxxx")
assert trim_last.equals("_Hello,_World:_This_is_A_Test!_")

