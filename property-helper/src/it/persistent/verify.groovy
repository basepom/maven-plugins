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

def prop_size = 3

def loadProperties(String fileName) {
  def properties = new Properties()
  def file = new File(basedir, fileName)
  assert file.exists()
  def stream = new FileInputStream(file)
  properties.load(stream)
  return properties
}


def properties = loadProperties("target/classes/persistent.properties")
assert properties.size() == prop_size

def testProperties = loadProperties("target/test-classes/persistent.properties")
assert testProperties.size() == prop_size

def buildProperties = loadProperties("target//build.properties")
assert buildProperties.size() == prop_size

def buildId = properties.getProperty("build.id", "xxxx")
def testBuildId = testProperties.getProperty("test.build.id", "xxxx")
def buildIdProp = buildProperties.getProperty("build.id", "xxxx")

def buildTime = properties.getProperty("build.time", "xxxx")
def testBuildTime = testProperties.getProperty("test.build.time", "xxxx")
def buildTimeProp = buildProperties.getProperty("build.time", "xxxx")

def unformattedTime = properties.getProperty("unformatted.time", "xxxx")
def testUnformattedTime = testProperties.getProperty("test.unformatted.time", "xxxx")
def unformattedTimeProp = buildProperties.getProperty("unformatted.time", "xxxx")

assert !buildId.equals("xxxx")
assert !testBuildId.equals("xxxx")
assert !buildIdProp.equals("xxxx")

assert buildId.equals(testBuildId)
assert buildId.equals(buildIdProp)

assert !buildTime.equals("xxxx")
assert !testBuildTime.equals("xxxx")
assert !buildTimeProp.equals("xxxx")

assert buildTime.equals(testBuildTime)
assert buildTime.equals(buildTimeProp)

assert !unformattedTime.equals("xxxx")
assert !testUnformattedTime.equals("xxxx")
assert !unformattedTimeProp.equals("xxxx")

def unformattedTimeValue = new DateTime(Long.parseLong(unformattedTimeProp))

assert unformattedTime.equals(testUnformattedTime)
assert unformattedTime.equals(unformattedTimeValue.toString())
