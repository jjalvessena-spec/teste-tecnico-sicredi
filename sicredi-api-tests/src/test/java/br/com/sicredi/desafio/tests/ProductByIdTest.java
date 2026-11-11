package br.com.sicredi.desafio.tests;

import br.com.sicredi.desafio.base.BaseApiTest;
import br.com.sicredi.desafio.model.Product;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * GET /products/{id} - Buscar apenas um produto por id.
 */
@Epic("Sicredi Desafio QE")
@Feature("GET /products/{id}")
class ProductByIdTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @DisplayName("Deve retornar um produto válido pelo id")
    void deveRetornarProdutoValidoPorId() {
        Product produto = given()
        .when()
                .get("/products/{id}", 1)
        .then()
                .statusCode(200)
                .extract().as(Product.class);

        assertThat(produto.getId()).isEqualTo(1);
        assertThat(produto.getTitle()).isNotBlank();
        assertThat(produto.getPrice()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve retornar 404 para um id de produto inexistente")
    void deveRetornarErro404ParaIdInexistente() {
        given()
        .when()
                .get("/products/{id}", 0)
        .then()
                .statusCode(404)
                .body("message", containsString("not found"));
    }

    @Test
    @DisplayName("Deve retornar 404 para um id não numérico")
    void deveRetornarErroParaIdNaoNumerico() {
        given()
        .when()
                .get("/products/{id}", "abc")
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 404 para um id negativo")
    void deveRetornarErroParaIdNegativo() {
        given()
        .when()
                .get("/products/{id}", -1)
        .then()
                .statusCode(404);
    }
}
