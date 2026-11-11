package br.com.sicredi.desafio.tests;

import br.com.sicredi.desafio.base.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * GET /test - Buscar o status da aplicação.
 */
@Epic("Sicredi Desafio QE")
@Feature("GET /test")
class StatusEndpointTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @DisplayName("Deve retornar status 200 com status=ok e method=GET")
    @Description("Verifica o health-check da API, usado como smoke test antes das demais suítes.")
    void deveRetornarStatusOkDaAplicacao() {
        given()
        .when()
                .get("/test")
        .then()
                .statusCode(200)
                .body("status", equalTo("ok"))
                .body("method", equalTo("GET"));
    }
}
