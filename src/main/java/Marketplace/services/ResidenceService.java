
package Marketplace.services;

import java.sql.SQLException;
import java.util.List;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.response.ResidenceProofResponseDto;

public interface ResidenceService {
    ResponseDataDto<List<ResidenceProofResponseDto>> getResidenceProofs(Long userId) throws SQLException;
}
