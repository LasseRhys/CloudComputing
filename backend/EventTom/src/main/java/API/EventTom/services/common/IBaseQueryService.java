package API.EventTom.services.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBaseQueryService<D, ID> {
    D getById(ID id);
    List<D> getAll();
    Page<D> getAll(Pageable pageable);
    List<D> findAllByUserId(Long userId);
    Page<D> findAllByUserId(Long userId, Pageable pageable);

}
