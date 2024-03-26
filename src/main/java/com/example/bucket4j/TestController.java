package com.example.bucket4j;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final Bucket bucket;

    @GetMapping(value = "/api/test")
    public ResponseEntity<String> test() {

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // API 호출시 토큰 1개를 소비
        if (probe.isConsumed()) {
            return ResponseEntity.ok().header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens())).build();
        }

        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill)).build();
    }

}
