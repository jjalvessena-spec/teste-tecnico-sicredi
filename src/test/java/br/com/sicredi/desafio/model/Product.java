package br.com.sicredi.desafio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representa um produto retornado pela API. Contém apenas os campos
 * relevantes para as validações da suíte; os demais campos do payload
 * (imagens, reviews, dimensões, etc.) são ignorados na desserialização.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private Integer id;
    private String title;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private String brand;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
