/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.util;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch that does not throw a checked exception,
 * useful for preventing boilerplate in lambdas and places
 * where an exception should be rethrown by a
 * {@link RuntimeException}.
 */
public class UncheckedCdl extends CountDownLatch {
    public UncheckedCdl(int count) {
        super(count);
    }

    @Override
    public void await() {
        try {
            super.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}