package central_erro.demo.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import central_erro.demo.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTORequest {
  
  @NotBlank
  @NotNull
  private String email;
  @NotBlank
  @NotNull
  private String password;
  @NotBlank
  @NotNull
  private Boolean admin;

  public User buildUser() {
    new User();
    return User.builder().email(this.getEmail()).password(this.getPassword()).admin(this.getAdmin()).build();
  }
}