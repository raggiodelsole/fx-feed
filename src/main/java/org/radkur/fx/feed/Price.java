package org.radkur.fx.feed;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@Getter
public class Price {
    private final long id;
    private final String instrumentName;
    private final BigDecimal bid;
    private final BigDecimal ask;
    private final LocalDateTime timestamp;
}
