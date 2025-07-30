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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

        log.info("{} EDIT → editing listingId={}", LOG_TXT, req.getListingId());

        /*
         * ──────────────────────────────────────────────────────────────
         * 1) Fotos actuales
         * ───────────────────────────────────────────────────────────
         */
        IListingImagesUrlsDto current = listingRepository.getListingImages(req.getListingId());

        String currentMain = current != null ? current.getMainImage() : null;
        String[] currentAux = {
                current != null ? current.getAux1() : null,
                current != null ? current.getAux2() : null,
                current != null ? current.getAux3() : null,
                current != null ? current.getAux4() : null
        };

        /*
         * ──────────────────────────────────────────────────────────────
         * 2) Portada
         * ───────────────────────────────────────────────────────────
         */
        String finalMain = currentMain;

        if (mainImage != null) { // nueva portada por archivo
            String uploaded = s3Service.uploadFile(mainImage);
            if (currentMain != null) {
                s3Service.deleteFile(s3Service.extractKey(currentMain));
            }
            finalMain = uploaded;
        } else if (req.getMainImage() != null // portada enviada como string distinta
                && !req.getMainImage().equals(currentMain)) {
            finalMain = req.getMainImage();
        }
        /* Caso contrario: portada sin cambios */

        /*
         * ──────────────────────────────────────────────────────────────
         * 3) Auxiliares (4 slots exactos)
         * ───────────────────────────────────────────────────────────
         */
        String[] finalAux = Arrays.copyOf(currentAux, 4);
        MultipartFile[] parts = { image1, image2, image3, image4 };
        List<String> reqUrls = req.getImagesUrl(); // puede ser null

        boolean imagesChanged = false;

        for (int i = 0; i < 4; i++) {
            String desiredUrl = finalAux[i]; // por defecto “lo que hay”

            // a) Si vino array de URLS → ese es el estado deseado (url o null)
            if (reqUrls != null) {
                desiredUrl = reqUrls.get(i); // puede ser null
            }

            // b) Si vino archivo → reemplaza sí o sí al slot
            if (parts[i] != null) {
                imagesChanged = true;
                if (finalAux[i] != null) {
                    s3Service.deleteFile(s3Service.extractKey(finalAux[i]));
                }
                desiredUrl = s3Service.uploadFile(parts[i]);
            }
            // c) Sin archivo, pero desiredUrl == null y había algo → borrar
            else if (desiredUrl == null && currentAux[i] != null) {
                imagesChanged = true;
                s3Service.deleteFile(s3Service.extractKey(currentAux[i]));
            }

            finalAux[i] = desiredUrl; // queda estado final del slot
        }

        /*
         * ──────────────────────────────────────────────────────────────
         * 4) CSV auxiliares (solo si cambió al menos un slot)
         * ───────────────────────────────────────────────────────────
         */
        String imgsCsv = null;
        if (imagesChanged) {
            String sep = String.valueOf((char) 31); // CHAR(31)
            imgsCsv = Arrays.stream(finalAux)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(sep));
            if (imgsCsv.isEmpty()) {
                imgsCsv = null; // pasar null para borrar todos
            }
        }

        /*
         * ──────────────────────────────────────────────────────────────
         * 5) CSV categorías
         * ───────────────────────────────────────────────────────────
         */
        String catsCsv = (req.getCategoriesId() == null)
                ? null
                : String.join(",", req.getCategoriesId());

        /*
         * ──────────────────────────────────────────────────────────────
         * 6) Guardar publicación principal
         * ───────────────────────────────────────────────────────────
         */
        ResponseDto response = listingCUDRepository.editListing(
                req.getListingId(),
                req.getTitle(),
                req.getDescription(),
                req.getPrice() != null ? req.getPrice().doubleValue() : null,
                finalMain,
                req.getAcceptsBarter(), req.getAcceptsCash(),
                req.getAcceptsTransfer(), req.getAcceptsCard(),
                req.getType(), req.getBrand(), catsCsv);

        /*
         * ──────────────────────────────────────────────────────────────
         * 7) Guardar auxiliares (solo si hubo cambios)
         * ───────────────────────────────────────────────────────────
         */
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
