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

package org.basepom.mojo.repack;

import org.springframework.boot.loader.tools.Layout;
import org.springframework.boot.loader.tools.Layouts.Expanded;
import org.springframework.boot.loader.tools.Layouts.Jar;
import org.springframework.boot.loader.tools.Layouts.None;
import org.springframework.boot.loader.tools.Layouts.War;

/**
 * Archive layout types.
 */
enum LayoutType {

    /**
     * Jar Layout.
     */
    JAR(new Jar()),

    /**
     * War Layout.
     */
    WAR(new War()),

    /**
     * Zip Layout.
     */
    ZIP(new Expanded()),

    /**
     * Directory Layout.
     */
    DIR(new Expanded()),

    /**
     * No Layout.
     */
    NONE(new None());

    private final Layout layout;

    LayoutType(Layout layout) {
        this.layout = layout;
    }

    public Layout layout() {
        return this.layout;
    }
}
