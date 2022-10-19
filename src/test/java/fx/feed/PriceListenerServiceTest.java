package fx.feed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.radkur.fx.feed.PriceListenerService;
import org.radkur.fx.feed.PriceMapperService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static fx.feed.PriceAssembler.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class PriceListenerServiceTest {

    private PriceMapperService priceMapperService;
    private PriceListenerService priceListenerService;

    @BeforeEach
    void init() {
        priceMapperService = mock(PriceMapperService.class);
        priceListenerService = new PriceListenerService(priceMapperService);
    }

    @Test
    @DisplayName("Should correctly add commission")
    void shouldProcessSimpleSingleLineInput() {
        //given
        var input = "mockedInput";
        var localDateTime = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);
        given(priceMapperService.mapToPrices(input)).willReturn(assembleSinglePositionList());

        //when
        priceListenerService.onMessage(input);

        //then
        final var priceFromDb = priceListenerService.getAll().get(0);
        assertThat(priceFromDb.getId()).isEqualTo(106);
        assertThat(priceFromDb.getInstrumentName()).isEqualTo("EUR/USD");
        assertThat(priceFromDb.getBid()).isEqualTo(new BigDecimal("1.0989"));
        assertThat(priceFromDb.getAsk()).isEqualTo(new BigDecimal("1.2012"));
        assertThat(priceFromDb.getTimestamp()).isEqualTo(localDateTime);
    }

    @Test
    @DisplayName("Should correctly add commission")
    void shouldOverridePriceWhenLatestTimestamp() {
        //given
        var input = "mockedInput";
        var localDateTime = LocalDateTime.of(2021, 6, 1, 12, 1, 1, 1000000);
        given(priceMapperService.mapToPrices(input)).willReturn(assembleTwoPriceList());

        //when
        priceListenerService.onMessage(input);

        //then
        final var priceFromDb = priceListenerService.getAll().get(0);
        assertThat(priceFromDb.getId()).isEqualTo(106);
        assertThat(priceFromDb.getInstrumentName()).isEqualTo("EUR/USD");
        assertThat(priceFromDb.getBid()).isEqualTo(new BigDecimal("2.0979"));
        assertThat(priceFromDb.getAsk()).isEqualTo(new BigDecimal("3.2032"));
        assertThat(priceFromDb.getTimestamp()).isEqualTo(localDateTime);
    }

    @Test
    @DisplayName("Should override already saved price")
    void shouldOverrideAlreadySavedPrice() {
        var input = "mockedInput1";
        var input2 = "mockedInput2";
        var localDateTime2 = LocalDateTime.of(2021, 6, 1, 12, 1, 1, 1000000);
        given(priceMapperService.mapToPrices(input)).willReturn(assembleSinglePositionList());
        given(priceMapperService.mapToPrices(input2)).willReturn(assembleDifferentSinglePositionList());

        //when
        priceListenerService.onMessage(input);
        priceListenerService.onMessage(input2);

        //then
        final var priceFromDb = priceListenerService.getAll().get(0);
        assertThat(priceFromDb.getId()).isEqualTo(106);
        assertThat(priceFromDb.getInstrumentName()).isEqualTo("EUR/USD");
        assertThat(priceFromDb.getBid()).isEqualTo(new BigDecimal("2.0979"));
        assertThat(priceFromDb.getAsk()).isEqualTo(new BigDecimal("3.2032"));
        assertThat(priceFromDb.getTimestamp()).isEqualTo(localDateTime2);
        assertThat(priceListenerService.getAll()).hasSize(1);
    }

}