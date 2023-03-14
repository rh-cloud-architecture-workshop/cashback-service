package org.globex.retail.cashback.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

@Entity(name = "Cashback")
@Table(name = "cashback")
@NamedQueries({
        @NamedQuery(name = "Cashback.getByCustomer", query = "from Cashback where customerId = :customerId")
})
@SequenceGenerator(name="CashbackIdSeq", sequenceName="cashback_id_seq", allocationSize = 1)
public class Cashback extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="CashbackIdSeq")
    @Column(name = "cashback_id")
    public long cashbackId;

    @Column(name = "customer_id")
    public String customerId;

    @Column(name = "amount")
    public BigDecimal amount;

    public static Optional<Cashback> findByCustomer(String customerId) {
        return find("#Cashback.getByCustomer", Parameters.with("customerId", customerId)).firstResultOptional();
    }

    @JsonProperty("amount_f")
    public String getAmountFormatted() {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    public static Builder builder(String customerId) {
        return new Builder(customerId);
    }

    public static class Builder {

        private final Cashback cashback;

        public Builder(String customerId) {
            cashback = new Cashback();
            cashback.customerId = customerId;
            cashback.amount = new BigDecimal("0.00");
        }

        public Builder amount(BigDecimal amount) {
            cashback.amount = amount;
            return this;
        }

        public Cashback build() {
            return cashback;
        }
    }
}
