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

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.ConfigFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentScheduler;
import net.tridentsdk.server.threads.ThreadsManager;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.BooleanResult4;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;

@JCStressTest
@Outcome(id = "[true, true, true, true]", expect = Expect.ACCEPTABLE, desc = "Latches work")
@Outcome(expect = Expect.FORBIDDEN)
@State
public class LatchInitTest {
    @Actor
    public void collect(BooleanResult4 result4) {
        if (Factories.collect() != null)
            result4.r1 = true;
    }

    @Actor
    public void thread(BooleanResult4 result4) {
        if (Factories.threads() != null)
            result4.r2 = true;
    }

    @Actor
    public void config(BooleanResult4 result4) {
        if (Factories.configs() != null)
            result4.r3 = true;
    }

    @Actor
    public void task(BooleanResult4 result4) {
        if (Factories.tasks() != null)
            result4.r4 = true;
    }

    @Actor
    public void setup() {
        Factories.init(new ConfigFactory() {
            @Override
            public JsonConfig serverConfig() {
                return new JsonConfig(Paths.get("/topkek"));
            }
        });
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(new TridentScheduler());
        Factories.init(new ThreadsManager());
    }
}
