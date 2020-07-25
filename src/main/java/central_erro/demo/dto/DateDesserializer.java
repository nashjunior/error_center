package central_erro.demo.dto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateDesserializer extends JsonDeserializer<LocalDateTime> {
  private static final String[] DATE_FORMATS = new String[] { "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy" };

  @Override
  public LocalDateTime deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
      throws IOException, JsonProcessingException {
    if (paramJsonParser == null || "".equals(paramJsonParser.getText()))
      return null;
    String date = paramJsonParser.getText();

    for (String format : DATE_FORMATS) {
      System.out.println(format);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
      try {
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        return dateTime;
      } catch (Exception e) {
        // TODO: handle exception
      }
    }
    throw new JsonParseException(paramJsonParser,
        "Unparseable date: \"" + date + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
  }
}