package org.radkur.fx.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This service is responsible for performing listing for message and
 *
 */
@Service
@RequiredArgsConstructor
public class PriceListenerService {
    private static final BigDecimal COMMISSION = BigDecimal.valueOf(0.001);
    private static final MathContext MATH_CONTEXT = new MathContext(5, RoundingMode.HALF_DOWN); //here we can make or lose some $ by modifying Rounding mode
    private final PriceMapperService priceMapperService;
    private final Map<Long, Price> wannaBeDb = new HashMap<>(); //This field wants to become a database when it grows up :P
    private List<Price> lastScrapped;

    /**
     * This method is responsible for operations on prices passed as csvString
     * converting it to positions and saving to wannaBeDb db mock
     *  * It add commission and overrides instruments based on id
     *
     * @param message is inputString
     */
    public void onMessage(String message) {
        var prices = priceMapperService.mapToPrices(message);
        prices.ifPresent(this::processNewPrices);
    }

    private Price addCommissions(Price price) {
        return Price.builder()
                .id(price.getId())
                .instrumentName(price.getInstrumentName())
                .bid(getBidValueWithCommission(price))
                .ask(getAskValueWithCommission(price))
                .timestamp(price.getTimestamp())
                .build();
    }

    private BigDecimal getBidValueWithCommission(Price price) {
        return (price.getBid().subtract(price.getBid().multiply(COMMISSION)).round(MATH_CONTEXT));
    }

    private BigDecimal getAskValueWithCommission(Price price) {
        return (price.getAsk().add(price.getAsk().multiply(COMMISSION)).round(MATH_CONTEXT));
    }

    public void processNewPrices(List<Price> prices) {
        lastScrapped = prices;
        prices = createPricesWithLatestTimestamp(prices);
        prices = createPricesWithCommission(prices);
        prices.forEach(price -> wannaBeDb.put(price.getId(), price));

    }

    private List<Price> createPricesWithCommission(List<Price> prices) {
        return prices.stream().map(this::addCommissions).toList();
    }

    private List<Price> createPricesWithLatestTimestamp(List<Price> prices) {
        var latestPrices = new HashMap<String, Price>();
        for (Price price : prices) {
            if (priceNotPresentOnList(latestPrices, price) || priceHasLatestTimestamp(latestPrices, price)) {
                latestPrices.put(price.getInstrumentName(), price);
            }
        }
        return latestPrices.values().stream().toList();
    }

    private static boolean priceHasLatestTimestamp(HashMap<String, Price> latestPrices, Price price) {
        return latestPrices.get(price.getInstrumentName()).getTimestamp().isBefore(price.getTimestamp());
    }

    private static boolean priceNotPresentOnList(HashMap<String, Price> latestPrices, Price price) {
        return !latestPrices.containsKey(price.getInstrumentName());
    }

    public List<Price> getLastScrapped() {
        return lastScrapped;
    }

    public List<Price> getAll() {
        return wannaBeDb.values().stream().toList();
    }
}


