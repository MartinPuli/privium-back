package Marketplace.services.impl;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ListingRequestDto;
import Marketplace.projections.IListingImagesUrlsDto;
import Marketplace.repositories.IListingCUDRepository;
import Marketplace.repositories.IListingRepository;
import Marketplace.services.ListingCUDService;
import Marketplace.services.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ListingCUDServiceImpl implements ListingCUDService {

    private static final Logger log = LoggerFactory.getLogger(ListingCUDServiceImpl.class);
    private static final String LOG_TXT = "ListingCUDService";
    private static final String EDIT_TXT = "[editListing]";
    private static final String STATUS_TXT = "[manageStatus]";

    @Autowired
    private IListingCUDRepository listingCUDRepository;
    @Autowired
    private IListingRepository listingRepository;
    @Autowired
    private S3Service s3Service;

    /* ────────────────────────── EDIT ────────────────────────── */
    @Override
    public ResponseDto editListingWithImages(
            Long userId,
            ListingRequestDto req,
            MultipartFile mainImage,
            MultipartFile image1,
            MultipartFile image2,
            MultipartFile image3,
            MultipartFile image4) throws Exception {

        log.info(LOG_TXT + EDIT_TXT + " Editando publicacion listingId={}", req.getListingId());

        // ── URLs actuales ─────────────────────────────────────
        IListingImagesUrlsDto current = listingRepository.getListingImages(req.getListingId());

        // ── Principal ─────────────────────────────────────────
        String finalMain = (current != null) ? current.getMainImage() : null;
        if (mainImage != null) {
            // Subir primero, borrar después (evita quedar sin imagen si falla el upload)
            String newMain = s3Service.uploadFile(mainImage);
            if (finalMain != null) {
                s3Service.deleteFile(s3Service.extractKey(finalMain));
            }
            finalMain = newMain;
        }

        // ── Auxiliares (máx 4) ────────────────────────────────
        String[] finalAux = {
                current != null ? current.getAux1() : null,
                current != null ? current.getAux2() : null,
                current != null ? current.getAux3() : null,
                current != null ? current.getAux4() : null
        };

        boolean imagesChanged = false;
        MultipartFile[] parts = { image1, image2, image3, image4 };
        for (int i = 0; i < parts.length; i++) {
            MultipartFile part = parts[i];
            if (part != null) {
                imagesChanged = true;
                if (finalAux[i] != null) {
                    s3Service.deleteFile(s3Service.extractKey(finalAux[i]));
                }
                finalAux[i] = s3Service.uploadFile(part);
            }
        }

        // ── CSV de categorías y auxiliares ────────────────────
        String catsCsv = (req.getCategoriesId() == null)
                ? null
                : String.join(",", req.getCategoriesId());

        // Sólo construir imgsCsv si efectivamente vamos a persistir cambios de
        // auxiliares
        String imgsCsv = null;
        if (imagesChanged) {
            String imgSep = String.valueOf((char) 31); // CHAR(31)
            imgsCsv = Arrays.stream(finalAux)
                    .filter(Objects::nonNull) // evita NPE
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()) // ignora vacíos
                    .collect(Collectors.joining(imgSep));
            if (imgsCsv.isEmpty()) {
                // Si después del filtrado no queda nada, pasamos null para que el SP borre las
                // auxiliares
                imgsCsv = null;
            }
        }

        // ── Guardar publicación ───────────────────────────────
        ResponseDto response = listingCUDRepository.editListing(
                req.getListingId(),
                req.getTitle(),
                req.getDescription(),
                req.getPrice() != null ? req.getPrice().doubleValue() : null,
                finalMain,
                req.getAcceptsBarter(), req.getAcceptsCash(),
                req.getAcceptsTransfer(), req.getAcceptsCard(),
                req.getType(), req.getBrand(), catsCsv);

        // Persistir auxiliares sólo si hubo cambios en archivos
        if (imagesChanged) {
            listingCUDRepository.setAuxImages(req.getListingId(), imgsCsv);
        }

        return response;
    }

    /* ───────────────────────── STATUS ───────────────────────── */
    @Override
    public ResponseDto manageStatus(ListingRequestDto req) throws SQLException {
        log.info(LOG_TXT + STATUS_TXT + " Acción={} listingId={}", req.getAction(), req.getListingId());

        /* Si es DELETE → eliminar imágenes de S3 */
        if ("DELETE".equalsIgnoreCase(req.getAction())) {
            IListingImagesUrlsDto imgs = listingRepository.getListingImages(req.getListingId());

            /* principal */
            if (imgs != null && imgs.getMainImage() != null)
                s3Service.deleteFile(s3Service.extractKey(imgs.getMainImage()));

            /* auxiliares */
            Arrays.asList(imgs.getAux1(), imgs.getAux2(), imgs.getAux3(), imgs.getAux4())
                    .forEach(url -> {
                        if (url != null)
                            s3Service.deleteFile(s3Service.extractKey(url));
                    });
        }

        return listingCUDRepository.manageStatus(req.getAction(), req.getListingId());
    }
}
