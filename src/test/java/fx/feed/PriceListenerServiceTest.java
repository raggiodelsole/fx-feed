package fx.feed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.radkur.fx.feed.PriceListenerService;
import org.radkur.fx.feed.PriceMapperService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PriceListenerServiceTest {
    PriceListenerService priceListenerService;
    PriceMapperService priceMapperService;

    @BeforeEach
    void init() {
        priceMapperService = new PriceMapperService();
        priceListenerService = new PriceListenerService(priceMapperService);
    }

    @Test
    @DisplayName("Simple single line input test")
    void shouldProcessSimpleSingleLineInput() {
        //given
        var input = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001";
        var expectedLocalDateTime = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);

        //when
        priceListenerService.onMessage(input);

        //then
        assertThat(priceListenerService.getAll().get(0).getId()).isEqualTo(106);
        assertThat(priceListenerService.getAll().get(0).getInstrumentName()).isEqualTo("EUR/USD");
        assertThat(priceListenerService.getAll().get(0).getBid()).isEqualTo(new BigDecimal("1.1000"));
        assertThat(priceListenerService.getAll().get(0).getAsk()).isEqualTo(new BigDecimal("1.2000"));
        assertThat(priceListenerService.getAll().get(0).getTimestamp()).isEqualTo(expectedLocalDateTime);
    }

    @Test
    void getAll() {
    }
}