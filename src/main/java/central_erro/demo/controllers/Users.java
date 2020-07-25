package central_erro.demo.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import central_erro.demo.dto.request.UserDTORequest;
import central_erro.demo.entities.User;
import central_erro.demo.repos.UserRepo;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/")
public class Users {

  @Autowired
  private UserRepo userRepo;


  @GetMapping("/login")
  private ResponseEntity<?> login(@Valid @RequestBody UserDTORequest user) throws NoSuchAlgorithmException {
    Optional<User> login = userRepo.findByEmail(user.getEmail());
    if (!login.isPresent()) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok().build();
  }

  @PostMapping("/signup")
  private ResponseEntity<User> createUse(@Valid @RequestBody UserDTORequest user) {
    if (userRepo.findByEmail(user.getEmail()).isPresent()) {
      return ResponseEntity.badRequest().build();
    }
    User newUser = userRepo.save(user.buildUser());
    return newUser == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(newUser);
  }

}