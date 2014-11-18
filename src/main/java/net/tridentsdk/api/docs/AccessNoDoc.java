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
package net.tridentsdk.api.docs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * This annotation is for the use of documentation. This means that the access-level is not broad enough so that extra
 * documentation is needed. This should be annotated on classes that are less than {@code protected}, and not
 * documented
 * <p/>
 * <p>This annotation is inherited because only the classes that are subtypes of the non-documented classes have to be
 * within the same package (under protected) OR private (under protected), so they are also not documented.</p>
 * <p/>
 * <p>This annotation doesn't do anything</p>
 *
 * @author The TridentSDK Team
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
public @interface AccessNoDoc {
}
