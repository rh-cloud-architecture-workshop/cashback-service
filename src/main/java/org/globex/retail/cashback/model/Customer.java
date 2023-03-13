package org.globex.retail.cashback.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "customer")
public class Customer extends PanacheEntityBase {

    @Id
    @Column(name = "customer_id")
    public String customerId;

    @Column(name = "name")
    public String name;

}
