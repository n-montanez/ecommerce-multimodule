package com.montanez.products_catalog.data;

import java.util.List;
import java.util.UUID;

import com.montanez.products_catalog.model.Product;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RequestScoped
public class ProductDao {
    @PersistenceContext(name  = "products-jpa-unit")
    EntityManager em;

    public void createProduct(Product product) {
        em.persist(product);
    }
    
    public Product readProduct(UUID productId) {
        return em.find(Product.class, productId);
    }
    
    public void updateProduct(Product product) {
        em.merge(product);
    }
    
    public void deleteProduct(Product product) {
        em.remove(product);
    }
    
    public List<Product> readAllProducts() {
        return em.createNamedQuery("Product.findAll", Product.class).getResultList();
    }
}
