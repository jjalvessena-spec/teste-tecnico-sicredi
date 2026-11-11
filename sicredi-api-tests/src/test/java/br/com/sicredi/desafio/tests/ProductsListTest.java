package br.com.sicredi.desafio.tests;

import br.com.sicredi.desafio.base.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * GET /products - Buscar todos os produtos.
 */
@Epic("Sicredi Desafio QE")
@Feature("GET /products")
class ProductsListTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @DisplayName("Deve listar produtos com sucesso e retornar metadados de paginação")
    void deveListarTodosOsProdutosComSucesso() {
        given()
        .when()
                .get("/products")
        .then()
                .statusCode(200)
                .body("products.size()", greaterThan(0))
                .body("total", greaterThan(0))
                .body("limit", greaterThan(0));
    }

    @Test
    @DisplayName("Deve respeitar o parâmetro de paginação limit")
    @Description("Cenário exploratório: o parâmetro 'limit' não aparece no exemplo da "
            + "documentação, mas pertence ao mesmo endpoint documentado e é útil para "
            + "validar o comportamento de paginação da API.")
    void deveRespeitarParametroLimit() {
        given()
                .queryParam("limit", 5)
        .when()
                .get("/products")
        .then()
                .statusCode(200)
                .body("products.size()", org.hamcrest.Matchers.equalTo(5))
                .body("limit", org.hamcrest.Matchers.equalTo(5));
    }

    @Test
    @DisplayName("Deve respeitar o parâmetro de paginação skip")
    void deveRespeitarParametroSkip() {
        int primeiroIdSemSkip = given()
                .queryParam("limit", 5)
        .when()
                .get("/products")
        .then()
                .statusCode(200)
                .extract().jsonPath().getInt("products[0].id");

        int primeiroIdComSkip = given()
                .queryParam("limit", 5)
                .queryParam("skip", 5)
        .when()
                .get("/products")
        .then()
                .statusCode(200)
                .extract().jsonPath().getInt("products[0].id");

        assertThat(primeiroIdComSkip)
                .as("id do primeiro produto deve mudar ao aplicar skip")
                .isNotEqualTo(primeiroIdSemSkip);
    }
}
