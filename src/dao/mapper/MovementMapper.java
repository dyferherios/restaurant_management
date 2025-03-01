package dao.mapper;

import entity.MovementType;

import static entity.MovementType.IN;
import static entity.MovementType.OUT;

public class MovementMapper {
    public MovementType mapFromResultSet(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        return switch (stringValue) {
            case "IN" -> IN;
            case "OUT" -> OUT;
            default -> throw new IllegalArgumentException("Unknown unit value " + stringValue);
        };
    }
}
