package API.EventTom.services.users.interfaces;

import API.EventTom.dto.CustomerDTO;
import API.EventTom.dto.response.DashboardInformation;
import API.EventTom.services.common.IBaseQueryService;

public interface ICustomerQueryService extends IBaseQueryService<CustomerDTO, Long> {
    CustomerDTO getCustomerByCustomerNumber(String customerNumber);
    DashboardInformation getCustomerDashboardInformationByUserId(Long userId);
}
