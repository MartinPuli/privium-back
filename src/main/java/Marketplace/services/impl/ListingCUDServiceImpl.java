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
    public ResponseDto editListingWithImages(
            Long userId,
            ListingRequestDto req,
            MultipartFile mainImage,
            MultipartFile image1,
            MultipartFile image2,
            MultipartFile image3,
            MultipartFile image4) throws Exception {

        log.info(LOG_TXT + EDIT_TXT + " Editando publicacion listingId={}",req.getListingId());

        /* ── URLs actuales ───────────────────────────────────── */
        IListingImagesUrlsDto current = listingRepository.getListingImages(req.getListingId());

        /* ── Principal ───────────────────────────────────────── */
        String finalMain = (current != null) ? current.getMainImage() : null;
        if (mainImage != null) {
            if (finalMain != null)
                s3Service.deleteFile(s3Service.extractKey(finalMain));
            finalMain = s3Service.uploadFile(mainImage);
        }

        /* ── Auxiliares (máx 4) ──────────────────────────────── */
        // Vector con las URLs actuales en orden 0‑3
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
                if (finalAux[i] != null)
                    s3Service.deleteFile(s3Service.extractKey(finalAux[i]));
                finalAux[i] = s3Service.uploadFile(part);
            }
        }

        /* ── CSV de categorías y auxiliares ──────────────────── */
        String catSep = ",";
        String imgSep = String.valueOf((char) 31);
        String catsCsv = (req.getCategoriesId() == null) ? null
                : String.join(catSep, req.getCategoriesId());

        // Auxiliares: sólo URLs no‑null en el orden 1‑4
        String imgsCsv = Arrays.stream(finalAux)
                .collect(Collectors.joining(imgSep));
        if (imgsCsv.isBlank())
            imgsCsv = null;

        /* ── Guardar publicación ─────────────────────────────── */
        ResponseDto response = listingCUDRepository.editListing(
                req.getListingId(),
                req.getTitle(),
                req.getDescription(),
                req.getPrice() != null ? req.getPrice().doubleValue() : null,
                finalMain,
                req.getAcceptsBarter(), req.getAcceptsCash(),
                req.getAcceptsTransfer(), req.getAcceptsCard(),
                req.getType(), req.getBrand(), catsCsv);

        /* Auxiliares en BD (si hubo cambios) */
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
