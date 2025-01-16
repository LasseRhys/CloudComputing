package API.EventTom.services.common;

import API.EventTom.exceptions.notFoundExceptions.ResourceNotFoundException;
import API.EventTom.mappers.StandardDTOMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
public abstract class BaseQueryService<T, D, ID> implements IBaseQueryService<D, ID> {

    protected final JpaRepository<T, ID> repository;
    protected final StandardDTOMapper mapper;
    protected final Function<T, D> mapperFunction;
    protected final String entityName;

    protected BaseQueryService(
            JpaRepository<T, ID> repository,
            StandardDTOMapper mapper,
            Function<T, D> mapperFunction,
            String entityName) {
        this.repository = repository;
        this.mapper = mapper;
        this.mapperFunction = mapperFunction;
        this.entityName = entityName;
    }

    public List<D> getAll() {
        List<T> entities = repository.findAll();
        return entities.stream()
                .map(mapperFunction)
                .collect(Collectors.toList());
    }

    public Page<D> getAll(Pageable pageable) {
        Page<T> entityPage = repository.findAll(pageable);
        return entityPage.map(mapperFunction);
    }

    public D getById(ID id) {
        return repository.findById(id)
                .map(mapperFunction)
                .orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with id: " + id));
    }


    public abstract List<D> findAllByUserId(Long userId);
    public abstract Page<D> findAllByUserId(Long userId, Pageable pageable);

}