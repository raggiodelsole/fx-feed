package org.radkur.fx.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Slf4j
@Service
public class PriceMapperService {
    private static final String LINE_SEPARATOR = "\n";
    private static final String FIELD_SEPARATOR = ",";
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS");

    public Optional<List<Price>> mapToPrices(String csvString) {
        log.info("Data: {}", csvString);
        var pricesArrayOpt = createPricesArray(csvString);
        if (pricesArrayOpt.isPresent()) {
            return of(createPriceList(pricesArrayOpt.get()));
        }
        log.error("Could not get data from input");
        return empty();

    }

    //106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001
    private Optional<Price> mapToPrice(String csvString) {
        var priceArray = csvString.split(FIELD_SEPARATOR);

        try {
            return of(Price.builder()
                    .id(Long.parseLong(priceArray[0].trim()))
                    .instrumentName(priceArray[1].trim())
                    .bid(new BigDecimal(priceArray[2].trim()))
                    .ask(new BigDecimal(priceArray[3].trim()))
                    .timestamp(LocalDateTime.parse(priceArray[4].trim(), formatter))
                    .build());
        } catch (RuntimeException e) {
            log.error("Could not get information from line. Skipping it");
            return empty();
        }
    }

    private static Optional<String[]> createPricesArray(String csvString) {
        try {
            return of(csvString.split(LINE_SEPARATOR));
        } catch (RuntimeException e) {
            log.error("Could not split pinto sub-prices. Aborting process");
            return empty();
        }
    }

    private List<Price> createPriceList(String[] pricesArray) {
        List<Price> prices = new ArrayList<>();
        for (String subString : pricesArray) {
            mapToPrice(subString).ifPresent(prices::add);
        }
        return prices;
    }

    public DateTimeFormatter getFormatter() {
        return this.formatter;
    }
}
