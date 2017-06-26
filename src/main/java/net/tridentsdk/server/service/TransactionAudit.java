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
package net.tridentsdk.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.tridentsdk.service.Transaction;

import java.util.List;
import java.util.Map;

class TransactionAudit {
    private final Map<Integer, List<Transaction>> audits = Maps.newHashMap();

    public void put(int account, Transaction transaction) {
        synchronized (audits) {
            List<Transaction> transactions = audits.get(account);
            if (transactions == null)
                transactions = Lists.newArrayList();

            transactions.add(transaction);
            audits.put(account, transactions);
        }
    }

    public List<Transaction> transactionsFor(int account) {
        synchronized (audits) {
            return audits.get(account);
        }
    }

    public List<Transaction> concat() {
        List<Transaction> ts = Lists.newArrayList();
        audits.values().forEach(ts::addAll);

        return ts;
    }
}
