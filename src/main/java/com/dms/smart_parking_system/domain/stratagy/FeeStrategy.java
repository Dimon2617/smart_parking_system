package com.dms.smart_parking_system.domain.stratagy;

import java.math.BigDecimal;
import java.time.Duration;

public interface FeeStrategy {
    BigDecimal calculateFee(Duration duration);
}