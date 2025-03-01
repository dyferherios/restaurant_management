package dao;

import db.DataSource;
import entity.Ingredient;
import entity.MovementType;
import entity.StockMovement;
import entity.Unit;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StockMovementDao {
    private final DataSource dataSource = new DataSource();
    private final Connection connection = dataSource.getConnection();

    public StockMovementDao() {
    }

    public List<StockMovement> findByIngredientId(String ingredientId) {
        List<StockMovement> movements = new ArrayList<>();
        if (connection != null) {
                String sql = "SELECT movement_type, quantity, unit, movement_date FROM stock_movement WHERE ingredient_id = ? ORDER BY movement_date";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, ingredientId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        MovementType type = MovementType.valueOf(rs.getString("movement_type"));
                        double quantity = rs.getDouble("quantity");
                        Unit unit = Unit.valueOf(rs.getString("unit"));
                        LocalDateTime date = rs.getTimestamp("movement_date").toLocalDateTime();

                        movements.add(new StockMovement(type, quantity, unit, date));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Error fetching stock movements", e);
                }
        }
        return movements;
    }

}
