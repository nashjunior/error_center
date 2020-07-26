package central_erro.demo.services.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import central_erro.demo.entities.Log;

public interface LogInterface extends GenericInterface<Log>{
  
  @Override
  Page<Log> findAll(Pageable page);

  @Override
  List<Log> findAllList();

  Optional<Log> findById(Long id);

  Boolean deleteById(Long id);

  List<?> findAllLogsByParam(Map<String, String> params, Map <String, String> ordersString , Boolean isSized, Boolean isPageable);

}