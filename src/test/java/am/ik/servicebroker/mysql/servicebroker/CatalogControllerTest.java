package am.ik.servicebroker.mysql.servicebroker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CatalogControllerTest {
	@LocalServerPort
	int port;
	WebTestClient webClient;

	@Before
    public void setup() {
        this.webClient = WebTestClient.bindToServer() //
                .baseUrl("http://localhost:" + port) //
                .build();
    }

	@Test
	public void catalogUnauthorized() {
		this.webClient.get().uri("/v2/catalog") //
				.exchange() //
				.expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void catalogWrongUser() {
		this.webClient.get().uri("/v2/catalog") //
				.header(HttpHeaders.AUTHORIZATION, "Basic Zm9vOmJhcg==") //
				.exchange() //
				.expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void catalog() {
		this.webClient.get().uri("/v2/catalog") //
				.header(HttpHeaders.AUTHORIZATION, "Basic dGVzdDpiYXI=") //
				.exchange() //
				.expectStatus().isEqualTo(HttpStatus.OK) //
				.expectBody() //
				.jsonPath("$.services.length()").isEqualTo(1) //
				.jsonPath("$.services[0].id")
				.isEqualTo("445d9bca-ba54-11e7-8f28-89aab798d359") //
				.jsonPath("$.services[0].name").isEqualTo("shared-mysql") //
				.jsonPath("$.services[0].bindable").isEqualTo(true) //
				.jsonPath("$.services[0].tags.length()").isEqualTo(1) //
				.jsonPath("$.services[0].tags[0]").isEqualTo("mysql") //
				.jsonPath("$.services[0].metadata.displayName").isEqualTo("Shared MySQL") //
				.jsonPath("$.services[0].plans[0].id")
				.isEqualTo("517cf274-ba54-11e7-8f28-89aab798d359") //
				.jsonPath("$.services[0].plans[0].name").isEqualTo("shared");
	}
}