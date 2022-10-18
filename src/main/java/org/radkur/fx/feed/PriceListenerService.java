package org.radkur.fx.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PriceListenerService {
    private final PriceMapperService priceMapperService;
    private final Map<Long, Price> wannaBeDb = new HashMap<>();

    private List<Price> lastScrapped;

    public void onMessage(String message) {
        var prices = priceMapperService.mapToPrices(message);
        prices.ifPresent(this::addPricesToDb);
    }

    public void addPricesToDb(List<Price> prices) {
        lastScrapped = prices;
        prices.sort(Comparator.comparing(Price::getTimestamp));
        prices.forEach(price -> wannaBeDb.put(price.getId(), price));

    }

    public List<Price> getLastScrapped(){
        return lastScrapped;
    }

    public List<Price> getAll() {
        return wannaBeDb.values().stream().toList();
    }
}


