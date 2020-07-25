package central_erro.demo.controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import central_erro.demo.dto.request.LogsDTORequest;
import central_erro.demo.entities.Log;
import central_erro.demo.services.interfaces.LogInterface;

@RestController
@RequestMapping("/api/logs")
public class Logs {

  @Autowired
  LogInterface logInterface;

  @Autowired
  ObjectMapper objectMapper;


  @GetMapping
  public ResponseEntity<List<?>> getAllLogs(@RequestParam Map<String, Object> reqParam) {
    int expectedValue = reqParam.size();
    reqParam.entrySet().removeIf(param -> param.getValue().toString().trim().isEmpty());


    Boolean isOrdered = containsOrder(reqParam);
    Boolean isPageable = containsPage(reqParam);
    Boolean isSized = containsSize(reqParam);

    Boolean isSizePage = containsSizePage(isSized, isPageable);
    Boolean isOrderSize = containsOrderSize(isOrdered, isSized);
    Boolean isOrderPage = containsOrderPage(isOrdered, isPageable);
    Boolean isOrderSizePage = isOrderSize && isSizePage ? true : false;
    Boolean isFilter = (isOrderSizePage && expectedValue>3) ? true : 
                              (isOrderSize && expectedValue > 2 ? true : 
                                (isSizePage && expectedValue >2 ? true : 
                                  ((isOrdered || isSized || isPageable) && expectedValue>1 ? true : false
                                  )
                                ) 
                              );

    if (isFilter) {
      reqParam = removeOtherFilter(reqParam, isOrdered, isPageable, isSized, isSizePage, isOrderSize, isOrderPage, isOrderSizePage);
      if (reqParam.size() != expectedValue) return erro();
      if (isPageable || isOrderPage) 
      {
        reqParam.put("size", "10");
        isSized = containsSize(reqParam);
        isOrderSize = containsOrderSize(isOrdered, isSized);
        isSizePage = containsSizePage(isSized, isPageable);
        isOrderSizePage = isOrderSize && isSizePage ? true : false;
      }
       
      reqParam = orderMap(reqParam, isOrdered, isSized, isOrderSizePage);
      reqParam.forEach((key,value) -> System.out.println(key+" : " + value.toString()));
      System.out.println("--------------------");

      
      
      
    }
    return ResponseEntity.ok().build();
    
  }

  @PostMapping
  @ResponseBody
  public ResponseEntity<Log> createLog(@Valid @RequestBody final LogsDTORequest incomingLog) {
    ModelMapper modelMapper = new ModelMapper();
    Log newLog = modelMapper.map(incomingLog, Log.class);
    logInterface.save(newLog);
    return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(newLog);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Log> deleteLog(@PathVariable("id") Long id) {
    return logInterface.deleteById(id) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
  }

  public ResponseEntity<List<?>> erro() {
    return ResponseEntity.badRequest().build();
  }

  private Boolean containsOrder(Map <String,Object> reqParam) {
    return reqParam.containsKey("order");
  }

  private Boolean containsSize(Map <String,Object> reqParam) {
    return reqParam.containsKey("size");
  }

  private Boolean containsPage(Map <String,Object> reqParam) {
    return reqParam.containsKey("page");
  }

  private Boolean containsOrderSize(Boolean isOrdered, Boolean isSized) {
    return isOrdered && isSized ? true : false;
  }

  private Boolean containsOrderPage(Boolean isOrdered, Boolean isPageable) {
    return isOrdered && isPageable ? true : false;
  }

  private Boolean containsSizePage(Boolean isSized, Boolean isPageable) {
    return isSized && isPageable ? true : false;
  }

  private Map <String, Object> removeOtherFilter(Map <String,Object> reqParam, Boolean isOrdered,
  Boolean isPageable, Boolean isSized, Boolean isSizePage, Boolean isOrderSize, Boolean isOrderPage,Boolean isOrderSizePage){
    reqParam.entrySet().removeIf(param -> {
      Boolean contains = true;
      if(isOrderSizePage) contains = !param.getKey().equals("order") && !param.getKey().equals("page") && !param.getKey().equals("size");
      else if (isOrderSize) contains = !param.getKey().equals("order") && !param.getKey().equals("size");
      else if (isSizePage) contains = !param.getKey().equals("page") && !param.getKey().equals("size");
      else if (isOrdered) contains = !param.getKey().equals("order");
      else if (isSized) contains = !param.getKey().equals("size");
      else if (isPageable) contains = !param.getKey().equals("page");

      for (Field field : Log.class.getDeclaredFields()) {
        if(field.getName().equals(param.getKey()) || !contains) return false;
      }
      return true;
    });
    return reqParam;
  }

  private Map <String, Object> orderMap(Map <String,Object> reqParam, Boolean isOrdered, Boolean isSized, Boolean isOrderSizePage) {
    List<Map.Entry<String, Object>> list2= new ArrayList<>(reqParam.entrySet());
    
    //Comparando as chaves e colocando o size como ultima chave
    Collections.sort(list2, (obj1, obj2) -> {
      String key1= obj1.getKey();
      String  key2 = obj2.getKey();
      if(isOrderSizePage) {
        if(key1.equals("order") && (key2.equals("page") || key2.equals("size"))) return -1;
        if(key2.equals("order") && (key1.equals("page") || key1.equals("size"))) return 1;
        if(key1.equals("order") && (!key2.equals("page") && !key2.equals("size"))) return 1;
        if(key2.equals("order") && (!key1.equals("page") && !key1.equals("size"))) return -1;
      }
      if(isSized) {
        if(!obj1.getKey().equals("size") && !obj1.getKey().equals("page")) return -1;
      }
      return 0;
    });
    reqParam.clear();
    list2.forEach(obj -> reqParam.put(obj.getKey(), obj.getValue()));
    return reqParam;
  }
}