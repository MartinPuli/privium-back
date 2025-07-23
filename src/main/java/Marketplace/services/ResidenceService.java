
package Marketplace.services;

import java.sql.SQLException;
import java.util.List;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.dtos.response.ResidenceProofResponseDto;
import jakarta.mail.MessagingException;

public interface ResidenceService {
    public ResponseDataDto<List<ResidenceProofResponseDto>> getResidenceProofs(Long userId) throws SQLException;
    public ResponseDto approveResidence(Long executorId, UserRequestDto req) throws SQLException, MessagingException;
}
