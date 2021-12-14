package guru.springframework.client;

import guru.springframework.model.BeerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static guru.springframework.config.WebClientProperties.BEER_V1_PATH;
import static guru.springframework.config.WebClientProperties.BEER_V1_PATH_UUID;
import static guru.springframework.config.WebClientProperties.BEER_V1_PATH_UPC;

/**
 * @author alejandrolopez
 */
@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    private final WebClient webClient;

    @Override
    public Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path(BEER_V1_PATH + "/" + id.toString())
                        .queryParamIfPresent("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
                        .build()
                ).retrieve()
                .bodyToMono(BeerDto.class);
    }

    @Override
    public Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName, String beerStyle, Boolean showInventoryOnHand) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path(BEER_V1_PATH)
                        .queryParamIfPresent("pageNumber", Optional.ofNullable(pageNumber))
                        .queryParamIfPresent("pageSize", Optional.ofNullable(pageSize))
                        .queryParamIfPresent("beerName", Optional.ofNullable(beerName))
                        .queryParamIfPresent("beerStyle", Optional.ofNullable(beerStyle))
                        .queryParamIfPresent("showInventoryOnhand", Optional.ofNullable(showInventoryOnHand))
                        .build()
                ).retrieve()
                .bodyToMono(BeerPagedList.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto) {
        return webClient.post().uri(uriBuilder -> uriBuilder.path(BEER_V1_PATH).build())
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> updateBeer(UUID beerId, BeerDto beerDto) {
        return webClient.put().uri(uriBuilder -> uriBuilder.path(BEER_V1_PATH_UUID)
                        .build(beerId))
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteBeerById(UUID id) {
        return webClient.delete().uri(uriBuilder -> uriBuilder.path(BEER_V1_PATH_UUID)
                        .build(id))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<BeerDto> getBeerByUPC(String upc) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path(BEER_V1_PATH_UPC)
                        .build(upc))
                .retrieve()
                .bodyToMono(BeerDto.class);
    }
}
