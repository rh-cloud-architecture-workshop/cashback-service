package org.globex.retail.cashback.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import javax.persistence.*;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "customer")
public class Customer extends PanacheEntityBase {

    @Id
    @Column(name = "customer_id")
    public String customerId;

    @Column(name = "name")
    public String name;

    @Transient
    public Cashback cashback;

    @Transient
    public List<Expense> expenses;

    public static PagedCustomerList findCustomers(String name, int pageIndex, int pageSize) {
        PanacheQuery<PanacheEntityBase> customersQuery;
        if (name == null || name.isEmpty() || name.isBlank()) {
            customersQuery = findAll().page(Page.of(pageIndex, pageSize));
        } else {
            customersQuery = find("upper(name) like '%" + name.toUpperCase(Locale.ROOT) + "%'").page(Page.of(pageIndex, pageSize));
        }
        return PagedCustomerList.builder()
                .withCustomers(customersQuery.list()).withPageCount(customersQuery.pageCount())
                .withPage(pageIndex).withPageSize(pageSize).build();
    }

}
