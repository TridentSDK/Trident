/*
 * Copyright (c) 2014, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.openjdk.jcstress.tests.trident;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.BooleanResult1;

@JCStressTest
@Outcome(id = "[true, true]", expect = Expect.ACCEPTABLE, desc = "JMM works like it should")
@Outcome(id = "[false, true]", expect = Expect.FORBIDDEN, desc = "Volatile array does not work")
@Outcome(expect = Expect.FORBIDDEN)
public class ArrayTest {
    // volatileArray
    private Item[] items = new Item[10];

    /**
     * Tests visibility of an object array
     */
    @Actor
    public void volatileArray(Item item, BooleanResult1 result1) {
        items[0] = item;
        items = items;
    }

    @Arbiter
    public void check(Item item, BooleanResult1 result1) {
        if (items[0] == item)
            result1.r1 = true;
    }

    @State
    public static class Item {
    }
}
