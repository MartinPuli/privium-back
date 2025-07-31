package Marketplace.services.impl;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.request.CategoryRequestDto;
import Marketplace.dtos.response.CategoryResponseDto;
import Marketplace.projections.ICategoryDto;
import Marketplace.repositories.ICategoryRepository;
import Marketplace.services.CategoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);
     private static final String LOG_TXT = "CategoryService";
    private static final String GET_TXT = "[getCategories]";


    @Autowired
    private ICategoryRepository categoryRepository;

    @Override
    @Cacheable("categories")
    public ResponseDataDto<List<CategoryResponseDto>> getCategories(CategoryRequestDto requestDto) throws SQLException {

        log.info(LOG_TXT + GET_TXT + " Obteniendo categorias");

        List<ICategoryDto> raw = categoryRepository.getCategories(requestDto.getRootId(), requestDto.getLeafId());

        List<CategoryResponseDto> data = raw.stream()
                .map(CategoryResponseDto::fromProjection)
                .collect(Collectors.toList());

        return ResponseDataDto.<List<CategoryResponseDto>>builder()
                .code(200)
                .description("Listado de categorías")
                .data(data)
                .build();
    }
}
