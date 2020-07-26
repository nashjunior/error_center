package central_erro.demo.controllers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
import central_erro.demo.dto.response.LogDTOResponse;
import central_erro.demo.entities.Log;
import central_erro.demo.services.interfaces.LogInterface;

@RestController
@RequestMapping("/api/logs")
public class Logs {

  @Autowired
  LogInterface logInterface;

  @GetMapping
  public ResponseEntity<List<LogDTOResponse>> getAllLogs(@RequestParam Map<String, String> reqParam) {
    List<?> logs;
    List <LogDTOResponse> responses;
    int expectedValue = reqParam.size();
    if(expectedValue==0) {
      responses = logInterface.findAllLogsByParam(null, null, false, false, false).stream()
      .map(log -> new LogDTOResponse(log)).collect(Collectors.toList());
      return ResponseEntity.ok(responses);
    }
    Map <String, String> ordersString =new HashMap<>();
    reqParam.entrySet().removeIf(param -> param.getValue()==null);

    Boolean isOrdered = containsOrder(reqParam);
    if(isOrdered){
      if(!isArray(reqParam.get("order"))) return erro();
      List<String> orderTypes = new ArrayList<>(Arrays.asList(reqParam.get("order").split("(?![^)(]*\\([^)(]*?\\)\\)),(?![^\\[]*\\])")));
      ordersString =orderTypes.stream().map(orderType -> orderType.replaceAll("\\[|\\]", "")).map(orderType -> {
        if(orderType.contains(",")) return orderType.split(",");
        else return new String[]{orderType, ""};
      }).collect(Collectors.toMap(e -> e[0], e -> e[1]));
    } 
    Boolean isPageable = containsPage(reqParam);
    Boolean isSized = containsSize(reqParam);

    Boolean isSizePage = containsSizePage(isSized, isPageable);
    Boolean isOrderSize = containsOrderSize(isOrdered, isSized);
    Boolean isOrderPage = containsOrderPage(isOrdered, isPageable);
    Boolean isOrderSizePage = isOrderSize && isSizePage ? true : false;
    Boolean isFilterOrderSizePage = isOrderSizePage && expectedValue > 3 ? true : false;
    Boolean isFilter2Params = (isOrderSize || isOrderPage || isSizePage) && expectedValue > 2 ? true : false;
    Boolean isFilter1Param = (isOrdered || isSized || isPageable) && (!isOrderSize && !isOrderPage && !isSizePage) && expectedValue > 1 ? true : false;
    Boolean isFilter = (isFilterOrderSizePage || isFilter2Params || isFilter1Param)  &&(!isFilter1Param || !isFilter2Params) && expectedValue>0 ? true : false;
    if (isFilter) {
      reqParam = removeOtherFilter(reqParam, isOrdered, isPageable, isSized, isSizePage, isOrderSize, isOrderPage, isPageable);
      if (reqParam.size() != expectedValue) return erro(); 
    }

    if (isPageable && (!isSized && !isOrderSizePage))
    {
      reqParam.put("size", "10");
      isSized = containsSize(reqParam);
      isOrderSize = containsOrderSize(isOrdered, isSized);
      isSizePage = containsSizePage(isSized, isPageable);
      isOrderSizePage = isOrderSize && isSizePage ? true : false;
    }
    reqParam = orderMap(reqParam, isOrdered, isSized, isOrderSizePage);
    
    if(isOrdered) logs = logInterface.findAllLogsByParam(reqParam, ordersString, isFilter,isSized, isPageable);
    else logs = logInterface.findAllLogsByParam(reqParam, ordersString,isFilter ,isSized, isPageable);
    if(logs ==null) return erro();
    
     responses = logs.stream().map(log -> new LogDTOResponse(log)).collect(Collectors.toList());

    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LogDTOResponse> show(@PathVariable("id")Long id) {
    Optional<Log> log = logInterface.findById(id);
    return log.isPresent() ?ResponseEntity.ok(new LogDTOResponse(log.get())) :  ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<LogDTOResponse> createLog(@Valid @RequestBody final LogsDTORequest incomingLog) {
    ModelMapper modelMapper = new ModelMapper();
    Log newLog = modelMapper.map(incomingLog, Log.class);
    logInterface.save(newLog);
    LogDTOResponse logDTOResponse = new LogDTOResponse(newLog);
    return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(logDTOResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Log> deleteLog(@PathVariable("id") Long id) {
    return logInterface.deleteById(id) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
  }

  public ResponseEntity<List<LogDTOResponse>> erro() {
    return ResponseEntity.badRequest().build();
  }

  private Boolean containsOrder(Map <String,String> reqParam) {
    return reqParam.containsKey("order");
  }

  private Boolean containsSize(Map <String,String> reqParam) {
    return reqParam.containsKey("size");
  }

  private Boolean containsPage(Map <String,String> reqParam) {
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

  private Map <String, String> removeOtherFilter(Map <String,String> reqParam, Boolean isOrdered,
  Boolean isPageable, Boolean isSized, Boolean isSizePage, Boolean isOrderSize, Boolean isOrderPage,Boolean isOrderSizePage){
    reqParam.entrySet().removeIf(param -> {
      Boolean contains = true;
      if(isOrderSizePage) contains = !param.getKey().equals("order") && !param.getKey().equals("page") && !param.getKey().equals("size");
      else if (isOrderSize) contains = !param.getKey().equals("order") && !param.getKey().equals("size");
      else if (isOrderPage) contains = !param.getKey().equals("order") && !param.getKey().equals("page");
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

  private Map <String, String> orderMap(Map <String,String> reqParam, Boolean isOrdered, Boolean isSized, Boolean isOrderSizePage) {
    List<Map.Entry<String, String>> list2= new ArrayList<>(reqParam.entrySet());
    
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

  private Boolean isArray(String values) {
    Stack<Character> stack  = new Stack<Character>();
    for(int i = 0; i < values.length(); i++) {
      char c = values.charAt(i);
      if(c == '[') stack.push(c);
      if(c == ']' && (stack.isEmpty() || stack.pop() != '[')) return false;
    }
    return stack.isEmpty();
  }
}