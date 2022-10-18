package org.radkur.fx.feed;

import lombok.Builder;

@Builder(toBuilder = true)
public record PriceDto(String name) {
}
