package br.com.sicredi.desafio.model;

/**
 * Corpo da requisição de POST /products/add.
 * <p>
 * Oferece fábricas estáticas para os cenários mais comuns usados nos
 * testes (produto válido completo e produto mínimo, apenas com título),
 * evitando duplicar a montagem do payload em cada classe de teste.
 */
public class AddProductRequest {

    private String title;
    private String description;
    private Double price;
    private Double discountPercentage;
    private Double rating;
    private Integer stock;
    private String brand;
    private String category;
    private String thumbnail;

    public AddProductRequest() {
    }

    public static AddProductRequest produtoValido() {
        AddProductRequest produto = new AddProductRequest();
        produto.setTitle("Perfume Oil");
        produto.setDescription("Mega Discount, Impression of A...");
        produto.setPrice(13.0);
        produto.setDiscountPercentage(8.4);
        produto.setRating(4.26);
        produto.setStock(65);
        produto.setBrand("Impression of Acqua Di Gio");
        produto.setCategory("fragrances");
        produto.setThumbnail("https://i.dummyjson.com/data/products/11/thumnail.jpg");
        return produto;
    }

    public static AddProductRequest produtoComTituloEmBranco() {
        AddProductRequest produto = produtoValido();
        produto.setTitle("");
        return produto;
    }

    public static AddProductRequest produtoApenasComTitulo(String titulo) {
        AddProductRequest produto = new AddProductRequest();
        produto.setTitle(titulo);
        return produto;
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

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
