package fx.feed;

import org.radkur.fx.feed.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;

public class PriceAssembler {
    private long id = 106;
    private String instrumentName = "EUR/USD";
    private BigDecimal bid = new BigDecimal("1.1000");
    private BigDecimal ask = new BigDecimal("1.2000");
    private LocalDateTime timestamp = LocalDateTime.of(2020, 6, 1, 12, 1, 1, 1000000);
    private static LocalDateTime newerTimestamp = LocalDateTime.of(2021, 6, 1, 12, 1, 1, 1000000);

    public static PriceAssembler assemblePriceAssembler() {
        return new PriceAssembler();
    }

    public PriceAssembler withBid(BigDecimal bid) {
        this.bid = bid;
        return this;
    }

    public PriceAssembler withAsk(BigDecimal ask) {
        this.ask = ask;
        return this;
    }

    public PriceAssembler withTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Price toEntity() {
        return Price.builder()
                .id(id)
                .instrumentName(instrumentName)
                .bid(bid)
                .ask(ask)
                .timestamp(timestamp)
                .build();
    }

    public static Optional<List<Price>> assembleSinglePositionList() {
        return of(List.of(assemblePriceAssembler().toEntity()));
    }

    public static Optional<List<Price>> assembleDifferentSinglePositionList() {
        final var price = assemblePriceAssembler().withBid(new BigDecimal("2.1000")).withAsk(new BigDecimal("3.2000")).withTimestamp(newerTimestamp).toEntity();

        return of(List.of(price));
    }

    public static Optional<List<Price>> assembleTwoPriceList() {
        final var olderPrice = assemblePriceAssembler().toEntity();
        final var newerPrice = assemblePriceAssembler().withBid(new BigDecimal("2.1000")).withAsk(new BigDecimal("3.2000")).withTimestamp(newerTimestamp).toEntity();
        return of(List.of(olderPrice, newerPrice));
    }
}
