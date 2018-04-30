package org.mlt.evsosample.application;

import org.mlt.evsosample.application.dtos.*;
import org.mlt.evsosample.domain.InventoryItem;
import org.mlt.evsosample.domain.Product;
import org.mlt.evsosample.domain.ProductId;
import org.mlt.evsosample.domain.ProductRepository;
import org.mlt.evsosample.infrastructure.EventSourcingInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EventSourcingInventory inventory;

    @RequestMapping(path="/product", method = RequestMethod.POST)
    public ProductIdDTO createProduct(@RequestBody ProductDTO dto) {
        Product prod = new Product(dto.getName(), dto.getUnitPrice());
        return new ProductIdDTO(prod.getId());
    }

    @RequestMapping(path="/product/{productId}", method = RequestMethod.GET)
    public ProductDTO getProduct(@PathVariable("productId") String productId) {
        Product p = productRepository.findById(new ProductId(productId));
        if(p==null) {
            throw new ProductNotFoundException();
        }
        return new ProductDTO(p.getId().getUUID(), p.getName(), p.getUnitPrice());
    }

    @RequestMapping(path="/inventory", method = RequestMethod.POST)
    public void addProductToInventory(@RequestBody InventoryAddDTO dto) {
        Product p = productRepository.findById(new ProductId(dto.getProductId()));
        if(p==null) {
            throw new ProductNotFoundException();
        }
        p.addToInventory(dto.getAmount());
    }

    @RequestMapping(path="/inventory", method = RequestMethod.GET)
    public List<InventoryItemDTO> getAllProductsInInventory() {
        List<InventoryItem> items = inventory.listAll();
        List<InventoryItemDTO> dtos = new ArrayList<>();
        for(InventoryItem i : items) {
            dtos.add(new InventoryItemDTO(i.getProductId().getUUID(), i.getAmount()));
        }
        return dtos;
    }

    @RequestMapping(path="/inventory/totalvalue", method = RequestMethod.GET)
    public TotalValueDTO getInventoryTotalValue() {
        return new TotalValueDTO(inventory.totalValue());
    }

}
