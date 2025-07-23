package Marketplace.controllers;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.request.CategoryRequestDto;
import Marketplace.dtos.response.CategoryResponseDto;
import Marketplace.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/privium/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private static final String LOG_TXT = "CategoryController";
    private static final String GET_TXT = "[getCategories]";

    @Autowired
    private CategoryService categoryService;

    /**
     * POST sin body  →  toda la tabla.
     * POST con body  →  filtra por rootId o leafId.
     */
    @PostMapping(value = "/getCategories", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDataDto<List<CategoryResponseDto>>> getCategories(
            @RequestHeader(value = TextConstant.USER_HEADER) Long userId,
            @RequestBody(required = false) CategoryRequestDto categoryRequestDto
    ) throws SQLException {
        log.info(LOG_TXT + GET_TXT + " Inicio recuperación de categorias, userId={}", userId);

        ResponseDataDto<List<CategoryResponseDto>> resp =
                categoryService.getCategories(categoryRequestDto);

        log.info(LOG_TXT + GET_TXT + " Finalizada recuperación de categorias, userId={}", userId);

        return ResponseEntity.ok(resp);
    }
}
