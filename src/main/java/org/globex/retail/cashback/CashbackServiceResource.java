package org.globex.retail.cashback;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class CashbackServiceResource {

    private final static Logger LOGGER = LoggerFactory.getLogger(CashbackServiceResource.class);

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
}
