package central_erro.demo.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import central_erro.demo.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User,Long>{

	Optional<User> findByEmail(String email);
  
}