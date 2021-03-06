package com.github.bmsantos.graphql.apigen.resolvers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import com.github.bmsantos.graphql.dataloaders.DataLoaders;
import com.github.bmsantos.graphql.model.customer.Customer;
import com.github.bmsantos.graphql.model.customer.Customer.Unresolved;
import com.github.bmsantos.graphql.rest.RestClient;
import com.google.common.collect.Lists;
import io.engagingspaces.vertx.dataloader.DataLoader;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;

import static com.github.bmsantos.graphql.utils.CompletableObserver.completableObserver;
import static com.github.bmsantos.graphql.utils.VertxCompletableFutureUtils.completedVertxCompletableFuture;
import static io.vertx.core.CompositeFuture.all;
import static io.vertx.core.Future.future;
import static io.vertx.core.Vertx.currentContext;
import static io.vertx.core.logging.LoggerFactory.getLogger;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static me.escoffier.vertx.completablefuture.VertxCompletableFuture.from;

public class CustomerResolver implements Customer.AsyncResolver {
  private static final Logger log = getLogger(CustomerResolver.class);

  @Override
  public CompletableFuture<List<Customer>> resolve(final List<Customer> unresolved) {
    return null;
  }

  @Override
  public CompletableFuture<List<Customer>> resolve(final Object context, final List<Customer> unresolved) {
    if (!requireNonNull(unresolved).isEmpty()) {
      final Customer customer = unresolved.get(0);
      return processRentalCustomer(((DataLoaders)context).getCustomerDataLoader(), requireNonNull(customer));
    }
    return completedVertxCompletableFuture(null);
  }

  private CompletableFuture<List<Customer>> processRentalCustomer(final DataLoader<Long, List<Customer>> dataLoader, final Customer customer) {
    if (customer.getClass().equals(Unresolved.class)) {
      final Long id = customer.getId();
      log.debug("Fetching customer for rental id: " + id);
      try {
        return from(currentContext(), dataLoader.load(id));
      } catch (final Exception e) {
        log.error("Failed to fetch customer with id: " + id, e);
      } finally {
        dataLoader.dispatch();
      }
    }
    return completedVertxCompletableFuture(null);
  }
}