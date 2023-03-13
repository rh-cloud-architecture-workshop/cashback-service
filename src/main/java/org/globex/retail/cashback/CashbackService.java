package org.globex.retail.cashback;

import io.quarkus.panache.common.Parameters;
import io.vertx.core.json.JsonObject;
import org.globex.retail.cashback.model.Cashback;
import org.globex.retail.cashback.model.Expense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class CashbackService {

    private static final Logger log = LoggerFactory.getLogger(CashbackService.class);

    private static final BigDecimal cashbackPercentage = new BigDecimal(2).movePointLeft(2);

    @Transactional
    public void processExpense(JsonObject expenseAsJson) {

        Long orderId = expenseAsJson.getLong("orderId");
        Expense expense = Expense.findById(orderId);
        if (expense == null) {
            log.debug("Expense does not exist in the database. Creating new expense for order " + orderId);
            expense = Expense.builder(orderId).withCustomer(expenseAsJson.getString("customer"))
                    .withAmount(expenseAsJson.getDouble("total")).withDate(expenseAsJson.getString("date")).build();
            expense.persist();
        } else {
            Expense.builder(expense).withAmount(expenseAsJson.getDouble("total"));
        }
        Cashback cashback = Cashback.find("#Cashback.getByCustomer", Parameters.with("customerId", expense.customer))
                .firstResult();
        if (cashback == null) {
            log.debug("No cashback wallet exists. Creating new cashback for customer " + expense.customer);
            cashback = new Cashback.Builder(expense.customer).build();
            cashback.persistAndFlush();
        }
        expense.cashback = cashback.cashbackId;
        calculateCashback(expense, cashback);
    }

    private void calculateCashback(Expense expense, Cashback cashback) {

        BigDecimal originalExpenseCashback = expense.earnedCashback == null? BigDecimal.ZERO : expense.earnedCashback;
        BigDecimal newExpenseCashback = cashbackPercentage.multiply(expense.amount).setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal amount = cashback.amount == null? BigDecimal.ZERO : cashback.amount;
        cashback.amount = amount.subtract(originalExpenseCashback).add(newExpenseCashback);
        expense.earnedCashback = newExpenseCashback;
    }

}
