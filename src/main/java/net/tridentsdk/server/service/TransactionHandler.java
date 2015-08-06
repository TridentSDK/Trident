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

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.Lists;
import net.tridentsdk.Trident;
import net.tridentsdk.service.Transaction;
import net.tridentsdk.service.Transactions;
import net.tridentsdk.util.TridentLogger;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Performs API transactions
 *
 * @author The TridentSDK Team
 */
public class TransactionHandler extends ForwardingCollection<Transaction> implements Transactions {
    private final ConcurrentMap<Object, TransactionAudit> transactions = new ConcurrentHashMap<>();
    private final AtomicInteger transactionIds = new AtomicInteger(2);

    /**
     * Do not instantiate.
     * <p>
     * <p>To access this handler, use this code:
     * <pre><code>
     *     Transactions handler = Registered.transactions();
     * </code></pre></p>
     */
    public TransactionHandler() {
        if (!Trident.isTrident())
            TridentLogger.get().error(new IllegalAccessException("This class should only be instantiated by Trident"));
    }

    @Override
    public int newAcount() {
        return transactionIds.incrementAndGet();
    }

    @Override
    public int globalEconomy() {
        return 1;
    }

    @Override
    public int globalExchange() {
        return 2;
    }

    @Override
    public void deposit(int account, Transaction transaction) {
        TransactionAudit audit = transactions.computeIfAbsent(transaction.receiver(), (k) -> new TransactionAudit());

        audit.put(account, transaction);
        transaction.doTransaction(Transaction.Type.DEPOSIT);
    }

    @Override
    public boolean withdraw(int account, final Transaction transaction) {
        TransactionAudit audit = transactions.get(transaction.sender());
        if (audit == null)
            return false;

        Transaction withdrawl =
                new Transaction(
                        transaction.item(),
                        transaction.sender(),
                        transaction.receiver(),
                        -Math.abs(transaction.amount())) {
                    @Override
                    public void doTransaction(Type type) {
                        transaction.doTransaction(type);
                    }
                };

        audit.put(account, withdrawl);
        withdrawl.doTransaction(Transaction.Type.WITHDRAW);
        return true;
    }

    @Override
    public int amount(int account, Object person, Object type) {
        TransactionAudit audit = transactions.get(person);
        if (audit == null)
            return Integer.MIN_VALUE;

        List<Transaction> queue = audit.transactionsFor(account);
        if (queue == null)
            return Integer.MAX_VALUE;

        int amount = 0;
        for (Transaction transaction : queue) {
            if (type.equals(transaction.item())) {
                amount += transaction.amount();
            }
        }

        return amount;
    }

    @Override
    protected Collection<Transaction> delegate() {
        List<Transaction> transactions = Lists.newArrayList();
        this.transactions.values().forEach(transactionAudit -> transactions.addAll(transactionAudit.concat()));
        return transactions;
    }
}