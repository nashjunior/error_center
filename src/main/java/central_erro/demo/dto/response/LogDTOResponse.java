package central_erro.demo.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import central_erro.demo.deserializers.DateSerializer;
import central_erro.demo.entities.Log;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDTOResponse {
  private Long id;
  private String origin;

  @JsonSerialize(using = DateSerializer.class)
  private LocalDateTime date;
  private Long quantity;

  public LogDTOResponse (Object log){
    this.date = ((Log) log).getDate();
    this.origin = ((Log) log).getOrigin();
    this.id = ((Log) log).getId();
    this.quantity = ((Log) log).getQuantity();
  }
}