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

/**
 * This service is responsible for mapping input into Prices
 */
@Slf4j
@Service
public class PriceMapperService {
    private static final String LINE_SEPARATOR = "\n";
    private static final String FIELD_SEPARATOR = ",";
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS");

    /**
     * This method convert string to optional containing list of prices.
     * It can skip lines which could not convert
     * @param csvString input for converting
     * @return optional with list or empty optional
     */
    public Optional<List<Price>> mapToPrices(String csvString) {
        log.info("Data: {}", csvString);
        var pricesArrayOpt = createPricesArray(csvString);
        if (pricesArrayOpt.isPresent()) {
            List<Price> priceList = createPriceList(pricesArrayOpt.get());
            if (!priceList.isEmpty()) {
                return of(priceList);
            }
        }
        log.error("Could not get data from input");
        return empty();

    }

    private Optional<Price> mapToPrice(String csvString) {
        var priceArray = csvString.split(FIELD_SEPARATOR);
        try {
            return of(createPrice(priceArray));
        } catch (RuntimeException e) {
            log.error("Could not get information from line. Skipping it");
            return empty();
        }
    }

    private Price createPrice(String[] priceArray) {
        BigDecimal bid = new BigDecimal(priceArray[2].trim());
        BigDecimal ask = new BigDecimal(priceArray[3].trim());
        if(bid.compareTo(ask) > 0){
            throw new ValidationError("Bid cannot be greater then ask");
        }
        return Price.builder()
                .id(Long.parseLong(priceArray[0].trim()))
                .instrumentName(priceArray[1].trim())
                .bid(bid)
                .ask(ask)
                .timestamp(LocalDateTime.parse(priceArray[4].trim(), formatter))
                .build();
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

}
