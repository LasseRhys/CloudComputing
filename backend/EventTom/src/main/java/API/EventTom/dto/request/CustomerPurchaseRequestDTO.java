package API.EventTom.dto.request;

import API.EventTom.dto.response.UserDTO;

public record CustomerPurchaseRequestDTO(UserDTO user,
                                         String customerNumber) {
}
