package br.com.sicredi.desafio.tests;

import br.com.sicredi.desafio.base.BaseApiTest;
import br.com.sicredi.desafio.model.AddProductRequest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * POST /products/add - Criação de produto.
 * <p>
 * A própria documentação informa que os produtos criados não são
 * persistidos: a API apenas simula a criação e devolve um payload
 * com um novo id, sem gravar nada de fato. Isso é explorado
 * explicitamente no teste {@link #produtoCriadoNaoDeveSerPersistido()}.
 */
@Epic("Sicredi Desafio QE")
@Feature("POST /products/add")
class ProductsAddTest extends BaseApiTest {

    @Test
    @Tag("smoke")
    @DisplayName("Deve criar um produto com dados válidos")
    void deveCriarProdutoComDadosValidos() {
        given()
                .body(AddProductRequest.produtoValido())
        .when()
                .post("/products/add")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Perfume Oil"));
    }

    @Test
    @DisplayName("Produto criado não deve ser persistido (conforme observação da documentação)")
    @Description("Cria um produto e em seguida consulta GET /products/{id} com o id retornado: "
            + "como a criação não é persistida, a busca deve resultar em 404.")
    void produtoCriadoNaoDeveSerPersistido() {
        Response criacao = given()
                .body(AddProductRequest.produtoValido())
        .when()
                .post("/products/add")
        .then()
                .statusCode(201)
                .extract().response();

        int idCriado = criacao.jsonPath().getInt("id");

        given()
        .when()
                .get("/products/{id}", idCriado)
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve aceitar criação com apenas o campo título preenchido")
    void deveCriarProdutoApenasComTitulo() {
        given()
                .body(AddProductRequest.produtoApenasComTitulo("Produto Mínimo QE"))
        .when()
                .post("/products/add")
        .then()
                .statusCode(201)
                .body("title", equalTo("Produto Mínimo QE"));
    }

    @Test
    @DisplayName("Documenta o comportamento da API ao criar produto com título em branco")
    @Description("A API não realiza validação de negócio nesse endpoint (apenas simula a "
            + "criação, sem persistir), portanto um título em branco também retorna 201. "
            + "Ver seção \"Bugs / Melhorias\" do README para a recomendação de adicionar "
            + "validação de campos obrigatórios.")
    void comportamentoAoCriarProdutoComTituloEmBranco() {
        given()
                .body(AddProductRequest.produtoComTituloEmBranco())
        .when()
                .post("/products/add")
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Deve rejeitar requisição com corpo JSON malformado")
    void deveRetornarErroComBodyMalformado() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"title\": \"produto sem fechar chaves\" ")
        .when()
                .post("/products/add")
        .then()
                .statusCode(400);
    }
}
