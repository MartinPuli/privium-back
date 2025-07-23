package Marketplace.projections;

/**
 * Proyección para las categorías asociadas a una publicación (listing),
 * tal como las devuelve el SP getListingCategories.
 */
public interface IListingCategoryDto {
    String getCategoryId();
    String getDescription();
}
