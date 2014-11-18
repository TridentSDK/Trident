/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.api.reflect;

import com.esotericsoftware.reflectasm.MethodAccess;

public class FastMethod {

    private final MethodAccess access;
    private final String name;
    private final Object instance;

    FastMethod(Object instance, MethodAccess access, String name) {
        this.access = access;
        this.name = name;
        this.instance = instance;
    }

    public Object invoke(Object... args) {
        return this.access.invoke(this.instance, this.name, args);
    }

    public Object invoke() {
        return this.access.invoke(this.instance, this.name);
    }
}
