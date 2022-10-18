package org.radkur.fx.feed;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@Getter
public class Price {
    //106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001

    private long id;
    private String instrumentName;
    private BigDecimal bid;
    private BigDecimal ask;
    private LocalDateTime timestamp;
}
