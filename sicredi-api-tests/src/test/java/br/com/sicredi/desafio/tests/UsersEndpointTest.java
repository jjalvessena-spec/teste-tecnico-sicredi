package br.com.sicredi.desafio.tests;

import br.com.sicredi.desafio.base.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

/**
 * GET /users - Buscar usuário para autenticação.
 */
@Epic("Sicredi Desafio QE")
@Feature("GET /users")
class UsersEndpointTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @DisplayName("Deve retornar a lista de usuários com sucesso")
    void deveRetornarListaDeUsuariosComSucesso() {
        given()
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .body("users", notNullValue())
                .body("users.size()", greaterThan(0))
                .body("total", greaterThan(0));
    }

    @Test
    @DisplayName("O primeiro usuário deve conter username e password (campos de negócio)")
    @Description("A própria documentação destaca que username e password são os campos "
            + "relevantes para o negócio, pois alimentam o fluxo de autenticação.")
    void deveConterCamposDeNegocioNoPrimeiroUsuario() {
        Response response = given()
                .when()
                .get("/users")
                .then()
                .extract().response();

        String username = response.jsonPath().getString("users[0].username");
        String password = response.jsonPath().getString("users[0].password");

        assertThat(username).as("username do primeiro usuário").isNotBlank();
        assertThat(password).as("password do primeiro usuário").isNotBlank();
    }

    @Test
    @DisplayName("Deve retornar metadados de paginação consistentes")
    void deveRetornarMetadadosDePaginacao() {
        given()
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .body("skip", equalTo(0))
                .body("limit", greaterThan(0));
    }
}
