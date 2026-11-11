package br.com.sicredi.desafio.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

/**
 * Classe base para todas as classes de teste da suíte.
 * <p>
 * Centraliza a configuração do RestAssured (URL base, content-type
 * padrão e o filtro do Allure, que anexa request/response de cada
 * chamada ao relatório) para que cada classe de teste fique focada
 * apenas nos cenários, e não em configuração de infraestrutura.
 * <p>
 * A URL base pode ser sobrescrita via propriedade de sistema
 * {@code -Dapi.baseUrl=...}, o que facilita apontar a suíte para
 * outro ambiente (homologação, mock local, etc.) sem alterar código.
 */
public abstract class BaseApiTest {

    protected static final String BASE_URL =
            System.getProperty("api.baseUrl", "https://dummyjson.com");

    @BeforeAll
    static void globalSetup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.requestSpecification = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
}
