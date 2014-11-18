/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
