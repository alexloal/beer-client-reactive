package guru.springframework.client;

import guru.springframework.config.WebClientConfig;
import guru.springframework.model.BeerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void listBeers() {
        final BeerPagedList pagedList = beerClient.listBeers(null, null, null, null, null).block();

        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isPositive();
    }

    @Test
    void listBeersPageSize10() {
        final BeerPagedList pagedList = beerClient.listBeers(1, 10, null, null, null).block();

        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(10);
    }

    @Test
    void listBeersNoRecords() {
        final BeerPagedList pagedList = beerClient.listBeers(10, 20, null, null, null).block();

        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isZero();
    }

    @Disabled("API returning inventory when should not be")
    @Test
    void getBeerById() {
        final UUID beerId = beerClient.listBeers(null, null, null, null, null).block().getContent().get(0).getId();

        final BeerDto beerDto = beerClient.getBeerById(beerId, false).block();

        assertThat(beerDto.getId()).isEqualTo(beerId);
        assertThat(beerDto.getQuantityOnHand()).isNull();
    }

    @Test
    void getBeerByIdShowInventoryTrue() {
        final UUID beerId = beerClient.listBeers(null, null, null, null, null).block().getContent().get(0).getId();

        final BeerDto beerDto = beerClient.getBeerById(beerId, true).block();

        assertThat(beerDto.getId()).isEqualTo(beerId);
        assertThat(beerDto.getQuantityOnHand()).isNotNull();
    }

    @Test
    void getBeerByUPC() {
        final String upc = beerClient.listBeers(null, null, null, null, null).block().getContent().get(0).getUpc();

        final BeerDto beerDto = beerClient.getBeerByUPC(upc).block();

        assertThat(beerDto.getUpc()).isEqualTo(upc);
    }

    @Test
    void createBeer() {
        final BeerDto beerDto = BeerDto.builder()
                .beerName("Dogfishhead 90 Min IPA")
                .beerStyle("IPA")
                .upc("234848549559")
                .price(new BigDecimal("10.99"))
                .build();

        final ResponseEntity<Void> responseEntity = beerClient.createBeer(beerDto).block();

        assertThat(responseEntity.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void updateBeer() {
        final BeerDto beerDto = beerClient.listBeers(null, null, null, null, null).block().getContent().get(0);

        BeerDto updatedBeer = BeerDto.builder()
                .beerName("Really Good Beer")
                .beerStyle(beerDto.getBeerStyle())
                .price(beerDto.getPrice())
                .upc(beerDto.getUpc())
                .build();

        final ResponseEntity<Void> responseEntity = beerClient.updateBeer(beerDto.getId(), updatedBeer).block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void deleteBeerById() {
        final BeerDto beerDto = beerClient.listBeers(null, null, null, null, null).block().getContent().get(0);

        final ResponseEntity<Void> responseEntity = beerClient.deleteBeerById(beerDto.getId()).block();
        assertThat(responseEntity.getStatusCode()).isEqualTo(NO_CONTENT);
    }
}
