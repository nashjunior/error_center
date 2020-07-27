package central_erro.demo.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import central_erro.demo.entities.Log;
import central_erro.demo.entities.LogLevel;

@Repository
public interface LogRepo extends JpaRepository <Log, Long> {
  Optional <Log> findById(Long id);

  Long countByLevel(LogLevel logLevel);


}