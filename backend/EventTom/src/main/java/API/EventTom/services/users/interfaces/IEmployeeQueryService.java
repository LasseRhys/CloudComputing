package API.EventTom.services.users.interfaces;

import API.EventTom.dto.EmployeeDTO;
import API.EventTom.services.common.IBaseQueryService;

import java.util.List;

public interface IEmployeeQueryService extends IBaseQueryService<EmployeeDTO, Long> {
    EmployeeDTO getEmployeeByEmployeeNumber(String employeeNumber);
    List<EmployeeDTO> getAllEventManagers();

}
