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


import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentTaskScheduler;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.server.threads.ThreadsHandler;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.BooleanResult1;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

@JCStressTest
@Outcome(id = "[true]", expect = Expect.ACCEPTABLE, desc = "Executor does its job")
@Outcome(expect = Expect.FORBIDDEN)
public class ExecutorTest {
    static {
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMap<>();
            }
        });
        Factories.init(ThreadsHandler.create());
        Factories.init(TridentTaskScheduler.create());
    }

    private final ConcurrentTaskExecutor<?> factory = ConcurrentTaskExecutor.create(2, "test");
    private final AtomicReference<State> reference = new AtomicReference<>();

    @Actor
    public void mutate(final State state, BooleanResult1 result1) {
        factory.execute(() -> reference.set(state));
    }

    @Arbiter
    public void check(State state, BooleanResult1 result1) {
        result1.r1 = this.reference.get() == state;
    }

    @org.openjdk.jcstress.annotations.State
    public static class State {
    }
}