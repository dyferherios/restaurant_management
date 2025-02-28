package dao.mapper;

public class Criteria {
    private final String column;
    private final Object value;
    private final String operator;
    private final String logicalOperator; // L'opérateur logique entre les critères
    private final String order;  // Le type de tri (ASC ou DESC)

    // Constructor
    public Criteria(String column, Object value, String operator, String logicalOperator, String order) {
        this.column = column;
        this.value = value;
        this.operator = operator != null ? operator : "=";
        this.logicalOperator = logicalOperator != null ? logicalOperator : "AND"; // Par défaut "AND"
        this.order = order != null ? order : "ASC"; // Par défaut "ASC"
    }

    // Getters
    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public String getOperator() {
        return operator;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public String getOrder() {
        return order;
    }
}
