package Marketplace.services.impl;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ListingRequestDto;
import Marketplace.projections.IListingImagesUrlsDto;
import Marketplace.repositories.IListingCUDRepository;
import Marketplace.repositories.IListingRepository;
import Marketplace.services.ListingCUDService;
import Marketplace.services.S3Service;
import jakarta.transaction.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.ArrayList;
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
    private static final String DELETE_IMG_TXT = "[deleteListingImage]";

    @Autowired
    private IListingCUDRepository listingCUDRepository;
    @Autowired
    private IListingRepository listingRepository;
    @Autowired
    private S3Service s3Service;

    /* ────────────────────────── EDIT ────────────────────────── */
    @Override
    @Transactional
    public ResponseDto editListingWithImages(
            Long userId,
            ListingRequestDto req,
            MultipartFile mainImage,
            MultipartFile image1,
            MultipartFile image2,
            MultipartFile image3,
            MultipartFile image4) throws Exception {

        log.info("{} EDIT → editing listingId={}", LOG_TXT, req.getListingId());

        String uploadedMain = null;
        Set<String> uploadedAux = new HashSet<>();
        List<String> deleteKeys = new ArrayList<>();
        try {

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

        // Si viene archivo y es diferente al actual (por URL), sube y reemplaza
        if (mainImage != null) {
            // Si el archivo es diferente al actual (por nombre o checksum, aquí solo por URL)
            String uploaded = s3Service.uploadPublic(mainImage);
            if (!Objects.equals(uploaded, currentMain)) {
                uploadedMain = uploaded;
                if (currentMain != null) {
                    deleteKeys.add(currentMain);
                }
                finalMain = uploadedMain;
            }
        } else if (req.getMainImage() != null && !req.getMainImage().equals(currentMain)) {
            // Si viene string distinto, reemplaza
            if (currentMain != null) {
                deleteKeys.add(currentMain);
            }
            finalMain = req.getMainImage();
        }
        // Caso contrario: portada sin cambios

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
            if (reqUrls != null && i < reqUrls.size()) {
                desiredUrl = reqUrls.get(i); // puede ser null
            }

            // b) Si vino archivo → reemplaza sí o sí al slot
            if (parts[i] != null) {
                imagesChanged = true;
                if (finalAux[i] != null) {
                    deleteKeys.add(finalAux[i]);
                }
                desiredUrl = s3Service.uploadPublic(parts[i]);
                uploadedAux.add(desiredUrl);
            }
            // c) Sin archivo, pero desiredUrl == null y había algo → borrar
            else if (desiredUrl == null && currentAux[i] != null) {
                imagesChanged = true;
                deleteKeys.add(currentAux[i]);
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

        List<String> keysForDeletion = new ArrayList<>(deleteKeys);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (String url : keysForDeletion) {
                    try {
                        s3Service.deletePublic(s3Service.extractKey(url));
                    } catch (Exception ex) {
                        log.error("Error deleting old image", ex);
                    }
                }
            }
        });

        return response;
        } catch (Exception e) {
            if (uploadedMain != null) {
                try {
                    s3Service.deletePublic(s3Service.extractKey(uploadedMain));
                } catch (Exception ex) {
                    log.error("Error limpiando main image", ex);
                }
            }
            for (String url : uploadedAux) {
                try {
                    s3Service.deletePublic(s3Service.extractKey(url));
                } catch (Exception ex) {
                    log.error("Error limpiando aux image", ex);
                }
            }
            throw e;
        }
    }

    /* ───────────────────────── STATUS ───────────────────────── */
    @Override
    @Transactional
    public ResponseDto manageStatus(ListingRequestDto req) throws SQLException {
        log.info(LOG_TXT + STATUS_TXT + " Acción={} listingId={}", req.getAction(), req.getListingId());

        /* Si es DELETE → eliminar imágenes de S3 */
        if ("DELETE".equalsIgnoreCase(req.getAction())) {
            IListingImagesUrlsDto imgs = listingRepository.getListingImages(req.getListingId());

            ResponseDto resp = listingCUDRepository.manageStatus(req.getAction(), req.getListingId());

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    if (imgs == null) {
                        return;
                    }
                    try {
                        if (imgs.getMainImage() != null) {
                            s3Service.deletePublic(s3Service.extractKey(imgs.getMainImage()));
                        }
                        Arrays.asList(imgs.getAux1(), imgs.getAux2(), imgs.getAux3(), imgs.getAux4())
                                .forEach(url -> {
                                    if (url != null) {
                                        try {
                                            s3Service.deletePublic(s3Service.extractKey(url));
                                        } catch (Exception ex) {
                                            log.error("Error deleting aux image", ex);
                                        }
                                    }
                                });
                    } catch (Exception ex) {
                        log.error("Error deleting listing images", ex);
                    }
                }
            });

            return resp;
        }

        return listingCUDRepository.manageStatus(req.getAction(), req.getListingId());
    }

    /* ─────────────────────── DELETE IMAGE ────────────────────── */
    @Override
    public ResponseDto deleteListingImage(Long listingId, Integer imageNumber) throws Exception {
        log.info(LOG_TXT + DELETE_IMG_TXT + " listingId={} img={}", listingId, imageNumber);

        if (imageNumber == null || imageNumber < 1 || imageNumber > 4) {
            return ResponseDto.builder()
                    .code(400)
                    .description("Número de imagen inválido")
                    .build();
        }

        IListingImagesUrlsDto current = listingRepository.getListingImages(listingId);
        if (current == null) {
            return ResponseDto.builder()
                    .code(404)
                    .description("Publicación no encontrada")
                    .build();
        }

        String[] aux = {
                current.getAux1(),
                current.getAux2(),
                current.getAux3(),
                current.getAux4()
        };

        int idx = imageNumber - 1;
        if (aux[idx] == null) {
            return ResponseDto.builder()
                    .code(404)
                    .description("Imagen no encontrada")
                    .build();
        }

        // Delete from S3 and remove from array
        s3Service.deleteFile(s3Service.extractKey(aux[idx]));
        aux[idx] = null;

        // Build CSV with remaining images
        String imgSep = String.valueOf((char) 31);
        String csv = Arrays.stream(aux).collect(Collectors.joining(imgSep));
        if (csv.isBlank())
            csv = null;

        listingCUDRepository.setAuxImages(listingId, csv);

        return ResponseDto.builder()
                .code(200)
                .description("Imagen eliminada correctamente")
                .build();
    }
}
