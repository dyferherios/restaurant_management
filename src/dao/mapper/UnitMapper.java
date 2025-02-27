package dao.mapper;

import entity.Unit;

import static entity.Unit.*;


public class UnitMapper {
    public Unit mapFromResultSet(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        return switch (stringValue) {
            case "U" -> U;
            case "G" -> G;
            case "L" -> L;
            default -> throw new IllegalArgumentException("Unknown unit value " + stringValue);
        };
    }
}