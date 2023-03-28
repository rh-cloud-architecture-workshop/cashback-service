package org.globex.retail.cashback;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.globex.retail.cashback.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class CashbackServiceResource {

    private final static Logger LOGGER = LoggerFactory.getLogger(CashbackServiceResource.class);

    @ConfigProperty(name = "page.size.default")
    int defaultPageSize;

    @Inject
    CashbackService cashbackService;

    @POST
    @Path("/expense")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> processExpense(String expense) {
        return Uni.createFrom().item(() -> new JsonObject(expense)).emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().<Void>transform(json -> {cashbackService.processExpense(json); return null;})
                .onItem().transform(v -> Response.ok().build())
                .onFailure().recoverWithItem(throwable -> {
                    LOGGER.error("Error when processing Expense", throwable);
                    return Response.status(500).build();
                });
    }

    @GET
    @Path("/customer")
    @Produces("application/json")
    public Uni<Response> getCustomers(@QueryParam("page") @DefaultValue("0") int pageIndex,
                                @QueryParam("size") int pageSize,
                                @QueryParam("name") @DefaultValue("") String name) {

        final int fPageSize = (pageSize == 0) ? defaultPageSize : pageSize;
        LOGGER.debug("Searching for: " + name +", pageSize " + pageSize + ", pageIndex "+pageIndex);

        return Uni.createFrom().item(() -> name).emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(p -> cashbackService.findCustomers(name, pageIndex, fPageSize))
                .onItem().transform(pagedCustomerList -> Response.ok(pagedCustomerList).build())
                .onFailure().recoverWithItem(throwable -> {
                    LOGGER.error("Error when querying for customers", throwable);
                    return Response.status(500).build();
                });
    }

    @POST
    @Path("/customer/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createCustomer(@PathParam("customerId") String customerId, String customerAsJson) {
        if (customerAsJson == null) {
            return Uni.createFrom().item(() -> Response.status(Response.Status.BAD_REQUEST).build());
        }
        JsonObject json = new JsonObject(customerAsJson);
        String customerName = json.getString("customerName");
        if (customerName == null || customerName.isBlank()) {
            return Uni.createFrom().item(() -> Response.status(Response.Status.BAD_REQUEST).build());
        }
        return Uni.createFrom().item(() -> {
                    Customer customer = new Customer();
                    customer.customerId = customerId;
                    customer.name = customerName;
                    return customer;
                })
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(customer -> {
                    cashbackService.createCustomer(customer);
                    return null;
                })
                .onItem().transform(v -> Response.ok().build())
                .onFailure().recoverWithItem(throwable -> {
                    LOGGER.error("Error when creating customer", throwable);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                });
    }

    @PUT
    @Path("/customer/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> updateCustomer(@PathParam("customerId") String customerId, String customerAsJson) {
        if (customerAsJson == null) {
            return Uni.createFrom().item(() -> Response.status(Response.Status.BAD_REQUEST).build());
        }
        JsonObject json = new JsonObject(customerAsJson);
        String customerName = json.getString("customerName");
        if (customerName == null || customerName.isBlank()) {
            return Uni.createFrom().item(() -> Response.status(Response.Status.BAD_REQUEST).build());
        }
        return Uni.createFrom().item(() -> {
                    Customer customer = new Customer();
                    customer.customerId = customerId;
                    customer.name = customerName;
                    return customer;
                })
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(customer -> {
                    cashbackService.updateCustomer(customer);
                    return null;
                })
                .onItem().transform(v -> Response.ok().build())
                .onFailure().recoverWithItem(throwable -> {
                    LOGGER.error("Error when creating customer", throwable);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                });
    }
}
