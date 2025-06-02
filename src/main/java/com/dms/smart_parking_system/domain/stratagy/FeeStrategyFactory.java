package com.dms.smart_parking_system.domain.stratagy;

import com.dms.smart_parking_system.domain.model.VehicleType;

public class FeeStrategyFactory {
    public static FeeStrategy getStrategy(VehicleType type) {
        return switch (type) {
            case CAR -> new CarFeeStrategy();
            case MOTORCYCLE -> new MotorcycleFeeStrategy();
            case TRUCK -> new TruckFeeStrategy();
        };
    }
}