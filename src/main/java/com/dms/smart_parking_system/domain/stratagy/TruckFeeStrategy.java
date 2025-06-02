package com.dms.smart_parking_system.domain.stratagy;

import java.math.BigDecimal;
import java.time.Duration;

public class TruckFeeStrategy implements FeeStrategy {

    public BigDecimal calculateFee(Duration duration) {
        return BigDecimal.valueOf(duration.toHours() * 3.0);
    }

}