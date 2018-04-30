package org.mlt.evsosample.application;

import org.mlt.eso.Events;
import org.mlt.eso.stores.JDBCEventStore;
import org.mlt.evsosample.domain.*;
import org.mlt.evsosample.infrastructure.EventSourcingInventory;
import org.mlt.evsosample.infrastructure.EventSourcingOrderRepository;
import org.mlt.evsosample.infrastructure.EventSourcingProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {

    private DataSource dataSource;
    private final JDBCEventStore eventStore;

    @Bean
    public JDBCEventStore eventStore() {
        return eventStore;
    }

    @Bean
    public EventCollectorInterceptor interceptor() {
        return new EventCollectorInterceptor();
    }

    @Bean
    public ProductRepository productRepository() {
        return new EventSourcingProductRepository(eventStore);
    }

    @Bean
    public EventSourcingInventory productInventory() {
        return new EventSourcingInventory(eventStore);
    }

    @Bean
    public OrderRepository orderRepository() { return new EventSourcingOrderRepository(eventStore); }

    @Autowired
    public Application(DataSource dataSource) {
        this.dataSource = dataSource;

        Events.registerEventType("ProductCreated", ProductCreated.class);
        Events.registerEventType("ProductAddedToInventory", ProductAddedToInventory.class);
        Events.registerEventType("ProductRemovedFromInventory", ProductAddedToInventory.class);
        Events.registerEventType("OrderCreated", OrderCreated.class);
        Events.registerEventType("OrderDispatched", OrderDispatched.class);
        Events.registerEventType("OrderLineAdded", OrderLineAdded.class);

        eventStore = new JDBCEventStore(dataSource);
        eventStore.createSchema();
    }
}
