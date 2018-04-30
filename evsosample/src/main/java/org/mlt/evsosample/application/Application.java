package org.mlt.evsosample.application;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hsqldb.jdbc.JDBCDataSource;
import org.mlt.eso.Events;
import org.mlt.eso.stores.JDBCEventStore;
import org.mlt.evsosample.application.dtos.InventoryAddDTO;
import org.mlt.evsosample.application.dtos.ProductDTO;
import org.mlt.evsosample.domain.*;
import org.mlt.evsosample.infrastructure.EventSourcingInventory;
import org.mlt.evsosample.infrastructure.EventSourcingProductRepository;
import spark.ResponseTransformer;

import javax.sql.DataSource;

import java.util.function.Function;

import static spark.Spark.*;

public class Application {

    private final JDBCEventStore eventStore;
    private DataSource dataSource;

    private final ProductRepository productRepository;
    private final EventSourcingInventory inventory;

    private DataSource createHsqlDbDatasource() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("cannot load hsqldb jdbc driver");
        }

        JDBCDataSource ds = new JDBCDataSource();
        ds.setURL("jdbc:hsqldb:mem:events");
        return ds;
    }

    public Application() {
        Events.registerEventType("ProductCreated", ProductCreated.class);
        Events.registerEventType("ProductAddedToInventory", ProductAddedToInventory.class);
        Events.registerEventType("ProductRemovedFromInventory", ProductAddedToInventory.class);

        dataSource = createHsqlDbDatasource();
        eventStore = new JDBCEventStore(dataSource);
        eventStore.createSchema();

        productRepository = new EventSourcingProductRepository(eventStore);
        inventory = new EventSourcingInventory(eventStore);
    }

    private ResponseTransformer json() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return model -> mapper.writeValueAsString(model);
    }

    private <C,R> BodyParsingHandler<C> withBodyAs(Class<C> c, Function<C, R> fn) {
        return new BodyParsingHandler<C>(c, eventStore) {
            @Override
            protected R execute(C body) {
                return fn.apply(body);
            }
        };
    }

    public void run() {
        post("/product", "application/json", withBodyAs(ProductDTO.class, (body) -> {
                Product prod = new Product(body.getName(), body.getUnitPrice());
                return prod.getId();
        }), json());

        post("/inventory", "application/json", withBodyAs(InventoryAddDTO.class, (body) -> {
            Product prod = productRepository.findById(new ProductId(body.getProductId()));
            if(prod==null) {
                throw new RuntimeException("Product not found");
            }
            prod.addToInventory(body.getAmount());
            return "OK";
        }));

        get("/inventory", (req, resp) -> inventory.listAll(), json());
        get("/product/:id", (req,resp) -> productRepository.findById(new ProductId(req.params(":id"))), json());
    }
}
