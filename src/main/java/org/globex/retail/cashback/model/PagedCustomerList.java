package org.globex.retail.cashback.model;

import java.util.List;

public class PagedCustomerList {

    private List<Customer> customers;

    private int pageCount;

    private int page;

    private int pageSize;

    public List<Customer> getCustomers() {
        return customers;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final PagedCustomerList pagedCustomerList;

        public Builder() {
            this.pagedCustomerList = new PagedCustomerList();
        }

        public Builder withCustomers(List<Customer> customers) {
            this.pagedCustomerList.customers = customers;
            return this;
        }

        public Builder withPageCount(int pageCount) {
            this.pagedCustomerList.pageCount = pageCount;
            return this;
        }

        public Builder withPage(int page) {
            this.pagedCustomerList.page = page;
            return this;
        }

        public Builder withPageSize(int pageSize) {
            this.pagedCustomerList.pageSize = pageSize;
            return this;
        }

        public PagedCustomerList build() {
            return pagedCustomerList;
        }

    }

}
