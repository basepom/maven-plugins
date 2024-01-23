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
assert val != "xxxx"

def value = properties.getProperty("value", "xxxx")
assert value != "xxxx"
assert value.trim() == val.trim()

def lowercase = properties.getProperty("lowercase", "xxxx")
assert lowercase != "xxxx"
assert lowercase.trim() == val.toLowerCase(Locale.ENGLISH).trim()

def uppercase = properties.getProperty("uppercase", "xxxx")
assert uppercase != "xxxx"
assert uppercase.trim() == val.toUpperCase(Locale.ENGLISH).trim()

def remove_whitespace = properties.getProperty("remove_whitespace", "xxxx")
assert remove_whitespace != "xxxx"
assert remove_whitespace == "Hello,World:This-is_A-Test!"

def underscore_for_whitespace = properties.getProperty("underscore_for_whitespace", "xxxx")
assert underscore_for_whitespace != "xxxx"
assert underscore_for_whitespace == "_Hello,_World:_This-is_A-Test!_"

def dash_for_whitespace = properties.getProperty("dash_for_whitespace", "xxxx")
assert dash_for_whitespace != "xxxx"
assert dash_for_whitespace == "-Hello,-World:-This-is_A-Test!-"

def use_underscore = properties.getProperty("use_underscore", "xxxx")
assert use_underscore != "xxxx"
assert use_underscore == "_Hello,_World:_This_is_A_Test!_"

def use_dash = properties.getProperty("use_dash", "xxxx")
assert use_dash != "xxxx"
assert use_dash == "-Hello,-World:-This-is-A-Test!-"

def trim = properties.getProperty("trim", "xxxx")
assert trim != "xxxx"
assert trim == val.trim()

def combined = properties.getProperty("combined", "xxxx")
assert combined != "xxxx"
assert combined.trim() == "-HELLO,-WORLD:-THIS-IS-A-TEST!-"

def trim_first = properties.getProperty("trim_first", "xxxx")
assert trim_first != "xxxx"
assert trim_first == "Hello,_World:_This_is_A_Test!"

def trim_last = properties.getProperty("trim_last", "xxxx")
assert trim_last != "xxxx"
assert trim_last == "_Hello,_World:_This_is_A_Test!_"

