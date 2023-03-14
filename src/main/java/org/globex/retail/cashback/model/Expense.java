package org.globex.retail.cashback.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "expense")
@NamedQueries({
        @NamedQuery(name = "Expense.getByCustomer", query = "from Expense where customer = :customerId")
})
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

    @JsonProperty("earnedCashback_f")
    public String getEarnedCashbackFormatted(){
        return NumberFormat.getCurrencyInstance(Locale.US).format(earnedCashback);
    }

    @JsonProperty("amount_f")
    public String getAmountFormatted() {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    @JsonProperty("date_f")
    public String getDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT).withLocale(Locale.US).withZone(ZoneId.of("UTC"));
        return formatter.format(date);
    }

    public static List<Expense> findByCustomer(String customerId) {
        return find("#Expense.getByCustomer", Parameters.with("customerId", customerId)).list();
    }

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
