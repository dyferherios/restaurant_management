package dao.operations;

import dao.entity.Criteria.Criteria;
import db.DataSource;
import dao.entity.Price;
import lombok.SneakyThrows;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PriceCrudOperations implements CrudOperations<Price> {
    private final DataSource dataSource = new DataSource();

    @Override
    public List<Price> getAll(int page, int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Price findById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Price> filterByIngredientIdAndCriteria(Long ingredientId, List<Criteria> criterias, Map<String, String> sort){
        List<Price> prices = new ArrayList<>();
        StringBuilder query = new StringBuilder("select distinct p.id, p.id_ingredient, p.amount, p.date_value from price p ");
        if (!criterias.isEmpty()) {
            query.append(" where ");
            for (Criteria criteria : criterias) {
                StringBuilder conditions = new StringBuilder();
                if (criteria.getValue() instanceof Date || criteria.getValue() instanceof LocalDate) {
                    conditions.append("p.date_value").append(" ").append(criteria.getOperation()).append(" '").append(criteria.getValue()).append("'");
                } else if ("amount".equals(criteria.getKey())) {
                    conditions.append("p.amount").append(" ").append(criteria.getOperation()).append(" ").append(criteria.getValue());
                }
                query.append(conditions);
                if(criterias.indexOf(criteria) < criterias.size()-1){
                    query.append(" ").append(criteria.getConjunction().toLowerCase()).append(" ");
                }
            }

        }

        query.append(" and p.id_ingredient=").append(ingredientId);

        System.out.println(query.toString());

        if (!sort.isEmpty()) {
            query.append(" order by ");
            List<String> orderClauses = sort.entrySet().stream()
                    .map(entry -> "p." + entry.getKey() + " " + entry.getValue())
                    .toList();
            query.append(String.join(", ", orderClauses));
        }

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query.toString());
            while (resultSet.next()) {
                prices.add(mapFromResultSet(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return prices;
    }

    @Override
    public List<Price> filterByCriteria(List<Criteria> criterias, int page, int size, Map<String, String> sort) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @SneakyThrows
    @Override
    public List<Price> saveAll(List<Price> entities) {
        List<Price> prices = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("insert into price (id, amount, date_value, id_ingredient) values (?, ?, ?, ?)"
                             + " on conflict (id) do nothing"
                             + " returning id, amount, date_value, id_ingredient");) {
            entities.forEach(entityToSave -> {
                try {
                    statement.setLong(1, entityToSave.getId());
                    statement.setDouble(2, entityToSave.getAmount());
                    statement.setDate(3, Date.valueOf(entityToSave.getDateValue()));
                    statement.setLong(4, entityToSave.getIngredient().getId());
                    statement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    prices.add(mapFromResultSet(resultSet));
                }
            }
            return prices;
        }
    }

    public List<Price> findByIdIngredient(Long idIngredient) {
        List<Price> prices = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select p.id, p.amount, p.date_value from price p"
                     + " join ingredient i on p.id_ingredient = i.id"
                     + " where p.id_ingredient = ?")) {
            statement.setLong(1, idIngredient);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Price price = mapFromResultSet(resultSet);
                    prices.add(price);
                }
                return prices;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Price mapFromResultSet(ResultSet resultSet) throws SQLException {
        Price price = new Price();
        price.setId(resultSet.getLong("id"));
        price.setAmount(resultSet.getDouble("amount"));
        price.setDateValue(resultSet.getDate("date_value").toLocalDate());
        return price;
    }
}
