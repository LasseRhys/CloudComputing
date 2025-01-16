    package API.EventTom.services.vouchers;


    import API.EventTom.dto.VoucherDTO;
    import API.EventTom.dto.response.VoucherResponseDTO;
    import API.EventTom.mappers.StandardDTOMapper;
    import API.EventTom.models.event.Voucher;
    import API.EventTom.models.event.VoucherType;
    import API.EventTom.repositories.VoucherRepository;
    import API.EventTom.services.common.BaseQueryService;
    import API.EventTom.services.vouchers.interfaces.IVoucherQueryService;
    import lombok.AllArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    public class VoucherQueryServiceImpl extends BaseQueryService<Voucher, VoucherDTO, Long> implements IVoucherQueryService {

        VoucherRepository voucherRepository;

        public VoucherQueryServiceImpl(VoucherRepository voucherRepository,
                                       StandardDTOMapper standardDTOMapper) {
            super(voucherRepository,
                    standardDTOMapper,
                    standardDTOMapper::mapVoucherToVoucherDTO,
                    "Voucher");
            this.voucherRepository = voucherRepository;
        }

        @Override
        public List<VoucherResponseDTO> getVouchersByCustomerId(Long userId) {
            List<Voucher> vouchers = voucherRepository.findByUserId(userId);

            return vouchers.stream()
                    .map(VoucherResponseDTO::fromVoucher)
                    .collect(Collectors.toList());
        }

        @Override
        public List<VoucherDTO> findAllByUserId(Long userId) {
            List<Voucher> vouchers = voucherRepository.findByUserId(userId);
            return vouchers.stream()
                    .map(mapperFunction)
                    .collect(Collectors.toList());
        }

        @Override
        public Page<VoucherDTO> findAllByUserId(Long userId, Pageable pageable) {
            Page<Voucher> voucherPage = voucherRepository.findByUserId(userId, pageable);
            return voucherPage.map(mapperFunction);
        }

        @Override
        public List<VoucherType> getAllVoucherTypes() {
            return List.of(VoucherType.values());
        }

    }
