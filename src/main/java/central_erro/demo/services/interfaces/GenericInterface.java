package central_erro.demo.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenericInterface<T> extends Pageable{
  Page<T> findAll(Pageable page);
  List<T> findAllList();
  T save(T object);

}