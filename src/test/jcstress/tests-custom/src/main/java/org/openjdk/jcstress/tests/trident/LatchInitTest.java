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

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.CountDownLatch;

@JCStressTest
@Outcome(getID = "[true, true, true, true]", expect = Expect.ACCEPTABLE, desc = "Latches work")
@Outcome(expect = Expect.FORBIDDEN)
public class LatchInitTest {
    private final Object object = new Object();

    @Actor
    public void count(HeldValueLatch<Object> latch, BooleanResult1 result1) {
        latch.countDown(object);
    }

    @Arbiter
    public void await(HeldValueLatch<Object> latch, BooleanResult1 result1) {
        try {
            result1.r1 = latch.await() == object;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @State
    @ThreadSafe
    public static class HeldValueLatch<V> {
        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile V value;

        /**
         * Sets the value in the latch
         *
         * <p>The effects of setting this only once is unspecified</p>
         *
         * <p>This is unsynchronized because all actions prior to counting down <em>happens-before</em> another thread
         * awaits the value</p>
         *
         * @param value the value to set to the latch
         */
        public void countDown(V value) {
            this.value = value;
            latch.countDown();
        }

        /**
         * Acquires the value held be the latch, or blocks to wait for the value to become available
         *
         * @return the value held by the latch
         * @throws InterruptedException if the operation was interrupted while blocked
         */
        public V await() throws InterruptedException {
            latch.await();
            return value;
        }
    }
}
