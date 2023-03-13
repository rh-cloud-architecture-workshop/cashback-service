package org.globex.retail.cashback.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "expense")
public class Expense extends PanacheEntityBase {

    @Id
    @Column(name = "order_id")
    public Long orderId;

    @Column(name = "customer_id")
    public String customer;

    @Column(name = "amount")
    public BigDecimal amount;

    @Column(name = "earned_cashback")
    public BigDecimal earnedCashback;

    @Column(name = "date")
    public Instant date;

    @Column(name = "cashback_id")
    public Long cashback;

    public static Builder builder(Long orderId) {
        return new Builder(orderId);
    }

    public static Builder builder(Expense expense) {
        return new Builder(expense);
    }

    public static class Builder {

        private final Expense expense;

        public Builder(Long orderId) {
            this.expense = new Expense();
            expense.orderId = orderId;
            expense.earnedCashback = BigDecimal.ZERO;
        }

        public Builder(Expense expense) {
            this.expense = expense;
        }

        public Builder withCustomer(String customer) {
            this.expense.customer = customer;
            return this;
        }

        public Builder withAmount(double amount) {
            this.expense.amount = BigDecimal.valueOf(amount);
            return this;
        }
        public Builder withDate(String date) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            ZonedDateTime zd = ZonedDateTime.parse(date, format);
            this.expense.date = zd.toInstant();
            return this;
        }

        public Expense build() {
            return expense;
        }
    }
}
