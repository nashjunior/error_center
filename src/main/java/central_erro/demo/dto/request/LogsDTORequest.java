package central_erro.demo.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import central_erro.demo.dto.DateDesserializer;
import central_erro.demo.dto.DateSerializer;
import central_erro.demo.entities.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogsDTORequest {
  private Long id;
  private LogLevel level;
  private String description;
  private String origin;
  @JsonDeserialize(using = DateDesserializer.class)
  @JsonSerialize(using = DateSerializer.class)
  private LocalDateTime date;
  private Long quantity;
}