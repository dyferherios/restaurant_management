package dao.operations;

import dao.entity.Criteria.Criteria;
import dao.entity.Ingredient;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CrudOperations<E> {
    List<E> getAll(int page, int size) throws SQLException;

    E findById(Long id);


    List<E> filterByCriteria(List<Criteria> criterias, int page, int size, Map<String, String> sort);

    // Both create (if does not exist) or update (if exist) entities
    List<E> saveAll(List<E> entities);
}
