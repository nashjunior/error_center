package central_erro.demo.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import central_erro.demo.dto.DateDesserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Log {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NotNull
  @Enumerated(EnumType.STRING)
  private LogLevel level;

  @Column(name = "event_description", nullable = false)
  @NotNull
  private String description;

  @Column(nullable = false)
  @NotNull
  private String origin;

  @NotNull
  @JsonDeserialize(using = DateDesserializer.class)
  @Column(nullable = false)
  private LocalDateTime date;

  @NotNull
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long quantity;

}