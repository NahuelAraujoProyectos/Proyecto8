package com.name.productstorage.service;

import com.name.productstorage.model.Product;
import com.name.productstorage.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Integer id, Product productDetails) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }



    public void uploadImage(Integer productId, MultipartFile file) throws IOException {
        // Obtener el producto
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Convertir el archivo a base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        // Guardar la imagen en el campo image del producto
        product.setImage(base64Image);
        productRepository.save(product);
    }

    public byte[] downloadImage(Integer productId) {
        // Obtener el producto
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Decodificar la imagen de base64 a byte[]
        return Base64.getDecoder().decode(product.getImage());
    }
}
