package Marketplace.projections;

/**
 * Proyección para las imágenes auxiliares de una publicación (listing),
 * tal como las devuelve el SP getAuxImages.
 */
public interface IListingImageDto {
    Integer getImgNumber();
    String getImgUrl();
}
