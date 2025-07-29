package Marketplace.repositories;

import Marketplace.models.Category;
import Marketplace.projections.ICategoryDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICategoryRepository extends CrudRepository<Category, String> {

    @Query(value =
        "CALL dbo.GetCategories (:rootId, :leafId)",
        nativeQuery = true)
    List<ICategoryDto> getCategories(
            @Param("rootId") String rootId,
            @Param("leafId") String leafId);
}
