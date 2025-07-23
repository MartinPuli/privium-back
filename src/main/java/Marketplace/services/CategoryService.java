package Marketplace.services;

import Marketplace.dtos.request.CategoryRequestDto;
import Marketplace.dtos.response.CategoryResponseDto;
import Marketplace.commons.dtos.ResponseDataDto;

import java.sql.SQLException;
import java.util.List;

public interface CategoryService {
    ResponseDataDto<List<CategoryResponseDto>> getCategories(CategoryRequestDto requestDto) throws SQLException;
}
