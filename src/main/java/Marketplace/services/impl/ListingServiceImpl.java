package Marketplace.services.impl;

import Marketplace.dtos.request.ListingRequestDto;
import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.request.ListListingsRequestDto;
import Marketplace.dtos.response.ListingInfoResponseDto;
import Marketplace.dtos.response.ListingResponseDto;
import Marketplace.models.User;
import Marketplace.projections.IListingCategoryDto;
import Marketplace.projections.IListingDto;
import Marketplace.projections.IListingImageDto;
import Marketplace.repositories.IListingCUDRepository;
import Marketplace.repositories.IListingRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.ListingService;
import Marketplace.services.S3Service;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListingServiceImpl implements ListingService {

        private static final Logger log = LoggerFactory.getLogger(ListingServiceImpl.class);
        private static final String LOG_TXT = "ListingService";
        private static final String LIST_TXT = "[listListings]";
        private static final String CREATE_TXT = "[createListing]";

        @Autowired
        private IListingRepository listingRepository;

        @Autowired
        private IListingCUDRepository listingCUDRepository;

        @Autowired
        private S3Service s3Service;

        @Autowired
        private IUserRepository userRepository;

        @Override
        public ResponseDataDto<ListingInfoResponseDto> getListingInfo(Long listingId) throws SQLException {
                log.info("ListingInfoService.getListingInfo listingId={}", listingId);

                List<IListingCategoryDto> cats = listingRepository.getListingCategories(listingId);

                List<IListingImageDto> imgs = listingRepository.getAuxImages(listingId);

                ListingInfoResponseDto infoDto = ListingInfoResponseDto.convertEntityToDto(cats, imgs);

                return ResponseDataDto.<ListingInfoResponseDto>builder()
                                .code(200)
                                .description("Información de publicación")
                                .data(infoDto)
                                .build();
        }

        @Override
        public ResponseDataDto<List<ListingResponseDto>> listListings(ListListingsRequestDto request)
                        throws SQLException {
                log.info(LOG_TXT + LIST_TXT + " Recuperando publicaciones. userId={}, status={}",
                                request.getUserId(), request.getStatus());

                String catSep = ",";
                String catsCsv = null;
                if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                        catsCsv = request.getCategoryIds().stream().collect(Collectors.joining(catSep));
                }

                List<IListingDto> raw = listingRepository.listListings(
                                request.getUserId(), request.getStatus(), request.getSearchTerm(),
                                request.getCreatedFrom(), request.getCreatedTo(), catsCsv, request.getSortOrder(),
                                request.getCountryId(), request.getCenterCountryId(), request.getMaxDistanceKm(),
                                request.getConditionFilter(), request.getBrandFilter(), request.getType(),
                                request.getAcceptsBarter(), request.getAcceptsCash(), request.getAcceptsTransfer(),
                                request.getAcceptsCard(), request.getMinPrice(), request.getMaxPrice(),
                                request.getListingId(), request.getNotShownListing(), request.getNotShownUser(),
                                request.getPage(), request.getPageSize());

                List<ListingResponseDto> data = raw.stream()
                                .map(ListingResponseDto::convertEntityToDto)
                                .collect(Collectors.toList());

                return ResponseDataDto.<List<ListingResponseDto>>builder()
                                .code(200)
                                .description("Listado de publicaciones")
                                .data(data)
                                .build();
        }

        @Override
        @Transactional
        public ResponseDataDto<ListingResponseDto> addListingWithImages(
                        Long userId,
                        ListingRequestDto request,
                        MultipartFile mainImage,
                        List<MultipartFile> images) throws Exception {

                log.info(LOG_TXT + CREATE_TXT + " Creando publicacion. {}", request);

                String mainImageUrl = null;
                List<String> auxUrls = new ArrayList<>();
                try {
                        // 1) Subir la imagen principal
                        mainImageUrl = s3Service.uploadPublic(mainImage);

                        // 2) Subir imágenes auxiliares (hasta 4)
                        if (images != null) {
                                int limit = Math.min(images.size(), 4);
                                for (int i = 0; i < limit; i++) {
                                        MultipartFile img = images.get(i);
                                        if (img != null) {
                                                auxUrls.add(s3Service.uploadPublic(img));
                                        }
                                }
                        }

                        // 3) Preparar CSVs
                        String catSep = ",";
                        String imgSep = String.valueOf((char) 31);
                        String catsCsv = (request.getCategoriesId() != null)
                                        ? String.join(catSep, request.getCategoriesId())
                                        : null;
                        String imgsCsv = auxUrls.isEmpty() ? null : String.join(imgSep, auxUrls);

                        // 4) Llamar al SP AddListing (guarda mainImageUrl)
                        IListingDto created = listingRepository.addListing(
                                        userId,
                                        request.getTitle(),
                                        request.getDescription(),
                                        request.getBrand(),
                                        request.getPrice() != null ? request.getPrice().doubleValue() : null,
                                        mainImageUrl,
                                        request.getCondition(),
                                        request.getAcceptsBarter() ? 1 : 0,
                                        request.getAcceptsCash() ? 1 : 0,
                                        request.getAcceptsTransfer() ? 1 : 0,
                                        request.getAcceptsCard() ? 1 : 0,
                                        request.getType(),
                                        catsCsv);

                        // 5) Guardar URLs auxiliares
                        if (imgsCsv != null) {
                                listingCUDRepository.setAuxImages(created.getId(), imgsCsv);
                        }

                        // 6) Mapear DTO de respuesta
                        ListingResponseDto dto = ListingResponseDto.convertEntityToDto(created);

                        return ResponseDataDto.<ListingResponseDto>builder()
                                        .code(200)
                                        .description("Publicación creada correctamente")
                                        .data(dto)
                                        .build();
                } catch (Exception e) {
                        if (mainImageUrl != null) {
                                try {
                                        s3Service.deletePublic(s3Service.extractKey(mainImageUrl));
                                } catch (Exception ex) {
                                        log.error("Error limpiando main image", ex);
                                }
                        }
                        for (String url : auxUrls) {
                                try {
                                        s3Service.deletePublic(s3Service.extractKey(url));
                                } catch (Exception ex) {
                                        log.error("Error limpiando aux image", ex);
                                }
                        }
                        throw e;
                }
        }

        // =======================================================
        // Obtener datos del vendedor
        // =======================================================
        @Override
        public ResponseDataDto<User> getSeller(ListListingsRequestDto request) throws SQLException {

                Long sellerId = request.getUserId(); // lo enviamos dentro del body
                log.info("{}[getSeller] Recuperando vendedor id={}", LOG_TXT, sellerId);

                User seller = userRepository.findById(sellerId);

                return ResponseDataDto.<User>builder()
                                .code(seller != null ? 200 : 404)
                                .description(seller != null ? "Datos del vendedor" : "Vendedor no encontrado")
                                .data(seller)
                                .build();
        }

}
