package central_erro.demo.dto.response;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTOResponse {
  @NotNull
  private Long id;
  @NotNull
  private String email;
  @NotNull
  private String password;
  @NotNull
  private String jwt;
}