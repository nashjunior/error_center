package central_erro.demo.services.interfaces;

import java.util.List;

public interface GenericInterface<T> {
  List<T> findAllList();
  T save(T object);

}