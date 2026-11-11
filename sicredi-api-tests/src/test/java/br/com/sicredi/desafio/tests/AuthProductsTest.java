package br.com.sicredi.desafio.tests;

import br.com.sicredi.desafio.base.BaseApiTest;
import br.com.sicredi.desafio.util.AuthHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

/**
 * GET /auth/products - Buscar produtos com autenticação.
 */
@Epic("Sicredi Desafio QE")
@Feature("GET /auth/products")
class AuthProductsTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @DisplayName("Deve retornar produtos quando autenticado com token válido")
    void deveRetornarProdutosComTokenValido() {
        String token = AuthHelper.obterTokenValido();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/auth/products")
        .then()
                .statusCode(200)
                .body("products.size()", greaterThan(0))
                .body("total", greaterThan(0));
    }

    @Test
    @DisplayName("Deve rejeitar a requisição quando nenhum header Authorization é enviado")
    @Description("A documentação apresenta tanto 401 quanto 403 como respostas de erro para "
            + "este endpoint; o teste aceita ambas para refletir fielmente o contrato "
            + "documentado (ver seção \"Melhorias\" do README sobre padronizar esse retorno).")
    void deveRetornarErroSemHeaderDeAutenticacao() {
        given()
        .when()
                .get("/auth/products")
        .then()
                .statusCode(is(oneOf(401, 403)));
    }

        @Test
    @DisplayName("Deve rejeitar a requisição com token inválido/mal formado")
    @Description("A documentação descreve o corpo de erro com um campo 'name' "
            + "(ex.: JsonWebTokenError), mas a API real não retorna esse campo para "
            + "token inválido — apenas 'message'. Ver seção Bugs do README.")
    void deveRetornarErroComTokenInvalido() {
        given()
                .header("Authorization", "Bearer token-invalido-123")
        .when()
                .get("/auth/products")
        .then()
                .statusCode(401)
                .body("message", org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyOrNullString()));
    }

    @Test
    @DisplayName("Deve rejeitar a requisição com header Authorization vazio")
    void deveRetornarErroComTokenVazio() {
        given()
                .header("Authorization", "Bearer ")
        .when()
                .get("/auth/products")
        .then()
                .statusCode(is(oneOf(401, 403)));
    }
}
