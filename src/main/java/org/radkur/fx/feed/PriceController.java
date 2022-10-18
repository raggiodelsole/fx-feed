package org.radkur.fx.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PriceController {
    private final PriceListenerService priceListenerService;

    @GetMapping("/prices")
    public List<Price> getPrices() {
        return priceListenerService.getAll();
    }

    @PostMapping("/invoke-on-message")
    public List<Price> mockIncomingMessage(@RequestBody String message) {
        priceListenerService.onMessage(message);
        return priceListenerService.getLastScrapped();
    }


}
