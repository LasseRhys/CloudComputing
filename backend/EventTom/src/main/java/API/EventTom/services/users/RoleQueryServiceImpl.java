package API.EventTom.services.users;


import API.EventTom.dto.RoleDTO;
import API.EventTom.mappers.StandardDTOMapper;
import API.EventTom.models.user.Role;
import API.EventTom.repositories.RoleRepository;
import API.EventTom.repositories.VoucherRepository;
import API.EventTom.services.common.BaseQueryService;
import API.EventTom.services.users.interfaces.IRoleQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleQueryServiceImpl extends BaseQueryService<Role, RoleDTO, Long> implements IRoleQueryService {


    public RoleQueryServiceImpl(RoleRepository roleRepository,
                                StandardDTOMapper standardDTOMapper) {
        super(roleRepository,
                standardDTOMapper,
                standardDTOMapper::mapRoleToRoleDTO,
                "Voucher");
    }

    @Override
    public List<RoleDTO> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Page<RoleDTO> findAllByUserId(Long userId, Pageable pageable) {
        return null;
    }
}
