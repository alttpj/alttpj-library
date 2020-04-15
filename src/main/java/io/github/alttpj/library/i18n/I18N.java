/*
 * Copyright 2020-2020 the ALttPJ Team @ https://github.com/alttpj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alttpj.library.i18n;

import java.io.InputStream;
import java.util.PropertyResourceBundle;

public class I18N {

    private static final String RESOURCE_NAME = "/alttpj.lib.properties";

    public static String getString(final String key) {
        return getString(I18N.class.getCanonicalName(), RESOURCE_NAME, key);
    }

    /**
     * Returns the message string with the specified key from the
     * "properties" file in the package containing the class with
     * the specified name.
     */
    protected static String getString(final String className, final String resourceName, final String key) {
        try {
            final InputStream stream = Class.forName(className).getResourceAsStream(resourceName);
            final PropertyResourceBundle bundle = new PropertyResourceBundle(stream);

            return (String) bundle.handleGetObject(key);
        } catch (final Throwable i18nEx) {
            throw new RuntimeException(i18nEx); // Chain the exception.
        }
    }
}
