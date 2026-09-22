package tests;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.model.ApiPost;
import tests.listeners.ExtentTestListener;

@Listeners(ExtentTestListener.class)
public class RestAssuredApiTest {

    @Test(groups = {"smoke", "api"})
    public void getPostByIdReturnsSuccess() {
        Response response = given()
            .baseUri("https://jsonplaceholder.typicode.com")
            .basePath("/posts/1")
        .when()
            .get()
        .then()
            .statusCode(200)
            .extract()
            .response();

        int id = response.jsonPath().getInt("id");
        String title = response.jsonPath().getString("title");

        Assert.assertEquals(id, 1, "Expected post id to be 1");
        Assert.assertNotNull(title, "Expected title to be present");
        Assert.assertFalse(title.isBlank(), "Expected title to be non-empty");
    }

    @Test(groups = {"smoke", "api"})
    public void getPostByIdDeserializesToPojo() {
        ApiPost post = given()
            .baseUri("https://jsonplaceholder.typicode.com")
            .basePath("/posts/1")
        .when()
            .get()
        .then()
            .statusCode(200)
            .extract()
            .as(ApiPost.class);

        Assert.assertEquals(post.getId(), 1, "Expected post id to be 1");
        Assert.assertTrue(post.getUserId() > 0, "Expected userId to be populated");
        Assert.assertNotNull(post.getTitle(), "Expected title to be present");
        Assert.assertFalse(post.getTitle().isBlank(), "Expected title to be non-empty");
    }

    @Test(groups = {"regression", "api", "contract"})
    public void getPostByIdMatchesContract() {
        given()
            .baseUri("https://jsonplaceholder.typicode.com")
            .basePath("/posts/1")
        .when()
            .get()
        .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    @Test(groups = {"regression", "api", "mock"})
    public void mockedGetPostByIdReturnsStubbedResponse() {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());

        try {
            wireMockServer.start();
            System.out.println("WireMock base URL: " + wireMockServer.baseUrl());
            configureFor("localhost", wireMockServer.port());
            stubFor(
                get("/posts/1")
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"userId\":10,\"id\":1,\"title\":\"mock title\",\"body\":\"mock body\"}")
                    )
            );

            Response response = given()
                .baseUri(wireMockServer.baseUrl())
                .basePath("/posts/1")
            .when()
                .get()
            .then()
                .statusCode(200)
                .extract()
                .response();

            Assert.assertEquals(response.jsonPath().getInt("userId"), 10, "Expected mocked userId to match stubbed response");
            Assert.assertEquals(response.jsonPath().getString("title"), "mock title", "Expected mocked title to match stubbed response");
        } finally {
            wireMockServer.stop();
        }
    }

    @Test(groups = {"regression", "api", "mock", "json-compare"})
    public void mockedGetPostByIdMatchesExpectedJsonStrictly() {
        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        String expectedJson = "{\"userId\":10,\"id\":1,\"title\":\"mock title\",\"body\":\"mock body\"}";

        try {
            wireMockServer.start();
            configureFor("localhost", wireMockServer.port());
            stubFor(
                get("/posts/1")
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(expectedJson)
                    )
            );

            String actualJson = given()
                .baseUri(wireMockServer.baseUrl())
                .basePath("/posts/1")
            .when()
                .get()
            .then()
                .statusCode(200)
                .extract()
                .asString();

            Object expected = JSONValue.parse(expectedJson);
            Object actual = JSONValue.parse(actualJson);
            Assert.assertEquals(actual, expected, "Expected actual JSON to match the expected JSON exactly");
        } finally {
            wireMockServer.stop();
        }
    }

    @Test(groups = {"smoke", "api"})
    public void createPostReturnsCreated() {
        JSONObject payload = new JSONObject();
        payload.put("title", "framework post");
        payload.put("body", "This is a sample body");
        payload.put("userId", 101);

        Response response = given()
            .baseUri("https://jsonplaceholder.typicode.com")
            .contentType(ContentType.JSON)
            .body(payload.toJSONString())
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .extract()
            .response();

        Assert.assertEquals(response.jsonPath().getString("title"), "framework post", "Title should match request payload");
        Assert.assertEquals(response.jsonPath().getInt("userId"), 101, "userId should match request payload");
    }

    @Test(groups = {"regression", "api"})
    public void createPostDeserializesResponseToPojo() {
        ApiPost payload = new ApiPost(101, 0, "framework pojo post", "This is a POJO body");

        ApiPost createdPost = given()
            .baseUri("https://jsonplaceholder.typicode.com")
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .extract()
            .as(ApiPost.class);

        Assert.assertEquals(createdPost.getTitle(), payload.getTitle(), "Expected response title to match payload");
        Assert.assertEquals(createdPost.getBody(), payload.getBody(), "Expected response body to match payload");
        Assert.assertEquals(createdPost.getUserId(), payload.getUserId(), "Expected response userId to match payload");
    }
}
