package fx.feed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.radkur.fx.feed.PriceMapperService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PriceMapperServiceTest {
    PriceMapperService priceMapperService;

    @BeforeEach
    void init() {
        priceMapperService = new PriceMapperService();
    }

    @Test
    @DisplayName("Simple single line input test")
    void shouldProcessSimpleSingleLineInput() {
        //given
        final var input = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001";
        final var expectedLocalDateTime = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);

        //when
        final var prices = priceMapperService.mapToPrices(input);
        final var singlePriceResult = prices.get().get(0);

        //then
        assertThat(singlePriceResult.getId()).isEqualTo(106);
        assertThat(singlePriceResult.getInstrumentName()).isEqualTo("EUR/USD");
        assertThat(singlePriceResult.getBid()).isEqualTo(new BigDecimal("1.1000"));
        assertThat(singlePriceResult.getAsk()).isEqualTo(new BigDecimal("1.2000"));
        assertThat(singlePriceResult.getTimestamp()).isEqualTo(expectedLocalDateTime);
    }

    @Test
    @DisplayName("Single line input test with whitespaces")
    void shouldProcessSingleLineWithWhitespacesInput() {
        //given
        var input = "  106 , EUR/USD    ,  1.1000 , 1.2000  ,  01-06-2020 12:01:01:001          ";
        var expectedLocalDateTime = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);

        //when
        final var prices = priceMapperService.mapToPrices(input);
        final var singlePriceResult = prices.get().get(0);

        //then
        assertThat(singlePriceResult.getId()).isEqualTo(106);
        assertThat(singlePriceResult.getInstrumentName()).isEqualTo("EUR/USD");
        assertThat(singlePriceResult.getBid()).isEqualTo(new BigDecimal("1.1000"));
        assertThat(singlePriceResult.getAsk()).isEqualTo(new BigDecimal("1.2000"));
        assertThat(singlePriceResult.getTimestamp()).isEqualTo(expectedLocalDateTime);
    }

    @ParameterizedTest
    @MethodSource("faultyData")
    @DisplayName("Return empty list for faulty data")
    void shouldReturnEmptyListForFaultyData(String input) {

        //given && when
        final var prices = priceMapperService.mapToPrices(input);

        //then
        assertThat(prices).isEmpty();
    }

    private static Stream<Arguments> faultyData() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("1020 12:01:02:002"),
                Arguments.of("!@#!@#@$$^%&**(*)(^$%"),
                Arguments.of("108,,"),
                Arguments.of("108, GBP/USD, 1.2500,1.2560,33-06-2020 12:01:02:002"),
                Arguments.of("108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:0021")
        );
    }

    @Test
    @DisplayName("Null input test")
    void shouldProcessNullInput() {

        //given && when
        final var prices = priceMapperService.mapToPrices(null);

        //then
        assertThat(prices).isEmpty();

    }

    @ParameterizedTest
    @MethodSource("multipleLinesInput")
    @DisplayName("Multiple lines input test")
    void shouldProcessMultipleLinesInput(String input) {
        //given && when
        final var prices = priceMapperService.mapToPrices(input);

        //then
        assertThat(prices.get()).hasSize(5);
    }

    private static Stream<Arguments> multipleLinesInput() {
        final var multipleLinesInput = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001\n" +
                "107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002\n" +
                "108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002\n" +
                "109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100\n" +
                "110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110";
        final var multipleLinesWithIncorrectDataBetween = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001\n" +
                "…\n" +
                "107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002\n" +
                "…\n" +
                "108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002\n" +
                "…\n" +
                "109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100\n" +
                "…\n" +
                "110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110";
        return Stream.of(
                Arguments.of(multipleLinesInput),
                Arguments.of(multipleLinesWithIncorrectDataBetween)
        );
    }
}
