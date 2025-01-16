package API.EventTom.controller.voucher;

import API.EventTom.dto.VoucherDTO;
import API.EventTom.dto.response.VoucherResponseDTO;
import API.EventTom.dto.request.BulkVoucherGenerationRequestDTO;
import API.EventTom.dto.request.VoucherGenerationRequestDTO;
import API.EventTom.config.security.AuthenticatedUserId;
import API.EventTom.models.event.Voucher;
import API.EventTom.models.event.VoucherType;
import API.EventTom.services.vouchers.interfaces.IVoucherClaimService;
import API.EventTom.services.vouchers.interfaces.IVoucherGenerationService;
import API.EventTom.services.vouchers.interfaces.IVoucherQueryService;
import API.EventTom.services.vouchers.interfaces.IVoucherUsageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@Validated
public class VoucherController {
    private final IVoucherGenerationService voucherGenerationService;
    private final IVoucherClaimService voucherClaimService;
    private final IVoucherUsageService voucherUsageService;
    private final IVoucherQueryService voucherQueryService;

    @GetMapping("all")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<Page<VoucherDTO>> getAllVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(voucherQueryService.getAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<VoucherResponseDTO> generateVoucher(
            @Valid @RequestBody VoucherGenerationRequestDTO request) {
        Voucher voucher = voucherGenerationService.generateVoucher(
                request.getAmount(),
                request.getValidityDays(),
                request.getType()
        );
        return ResponseEntity.ok(VoucherResponseDTO.fromVoucher(voucher));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<List<VoucherResponseDTO>> generateBulkVouchers(
            @Valid @RequestBody BulkVoucherGenerationRequestDTO request) {
        List<Voucher> vouchers = voucherGenerationService.generateBulkVouchers(
                request
        );
        return ResponseEntity.ok(
                vouchers.stream()
                        .map(VoucherResponseDTO::fromVoucher)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{code}/validate")
    public ResponseEntity<VoucherResponseDTO> validateVoucher(@PathVariable String code, @AuthenticatedUserId Long userId) {
        Voucher voucher = voucherUsageService.validateVoucher(code, userId);
        return ResponseEntity.ok(VoucherResponseDTO.fromVoucher(voucher));
    }

    @PutMapping("/{code}/claim")
    public ResponseEntity<VoucherResponseDTO> claimVoucher(
            @PathVariable String code,
            @AuthenticatedUserId Long userId) {
        Voucher voucher = voucherClaimService.claimVoucher(code, userId);
        return ResponseEntity.ok(VoucherResponseDTO.fromVoucher(voucher));
    }

    @GetMapping("/my-vouchers")
    public ResponseEntity<Page<VoucherDTO>> getMyVouchers(
            @AuthenticatedUserId Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "used") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VoucherDTO> vouchers = voucherQueryService.findAllByUserId(userId, pageable);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/user-vouchers")
    public ResponseEntity<List<VoucherDTO>> getMyVouchers(
            @AuthenticatedUserId Long userId
    ) {
        List<VoucherDTO> vouchers = voucherQueryService.findAllByUserId(userId);
        return ResponseEntity.ok(vouchers);
    }


    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public ResponseEntity<List<VoucherType>> getVoucherTypes() {
        List<VoucherType> types = voucherQueryService.getAllVoucherTypes();
        return ResponseEntity.ok(types);
    }
}