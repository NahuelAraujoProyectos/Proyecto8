package com.name.productstorage.controller;

import com.name.productstorage.model.Product;
import com.name.productstorage.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.saveProduct(product);
            return new ResponseEntity<>(savedProduct, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Integer id) {
        Optional<Product> productData = productService.getProductById(id);
        return productData.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Integer id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") Integer id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        // Validación del tipo de archivo
        String fileName = file.getOriginalFilename();
        if (fileName == null || !(fileName.endsWith(".png") || fileName.endsWith(".jpg"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Solo se permiten imágenes PNG o JPG.");
        }

        try {
            // Llamada al servicio para guardar la imagen codificada en base64
            productService.uploadImage(id, file);
            return ResponseEntity.ok("Imagen subida correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la imagen: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/download-image")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Integer id) {
        try {
            byte[] imageBytes = productService.downloadImage(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // O .IMAGE_PNG según el tipo de imagen
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }
}
