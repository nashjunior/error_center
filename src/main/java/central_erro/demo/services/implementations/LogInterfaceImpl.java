package central_erro.demo.services.implementations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import central_erro.demo.entities.Log;
import central_erro.demo.repos.LogRepo;
import central_erro.demo.services.interfaces.LogInterface;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class LogInterfaceImpl implements LogInterface {

  @Autowired
  EntityManager em;

  @Autowired
  private LogRepo logRepo;

  public List<?> findAllLogsByParam(Map<String, String> params, Map <String, String> ordersString , Boolean isSized, Boolean isOrderSizePage) {
    String sqlSearch = "SELECT l.* from Log l where ";
    List<?> list = new ArrayList<>();
    for (Map.Entry<String, String> param : params.entrySet()) {
      //verifica se parametro e tipo date
      if (param.getKey().equals("date") && isValidDate(param.getValue().toString())) {
        sqlSearch = sqlSearch
            .concat("date(" + param.getKey() + ")=to_date('" + param.getValue().toString() + "','DD/MM/YYYY') AND ");
        params.remove(param.getValue());
      }
      //verifica se o parametro não e de paginacao 
      else if (!param.getKey().equals("page") && !param.getKey().equals("size") && !param.getKey().equals("order")) {
        sqlSearch = sqlSearch.concat(param.getKey() + "='" + param.getValue().toString() + "' AND ");
        params.remove(param.getValue());
      }
    }
    sqlSearch = sqlSearch.substring(0, sqlSearch.length() - 4);
    if (ordersString != null || isSized || isOrderSizePage) {
      for (Map.Entry<String, String> param : params.entrySet()) {
        if(param.getKey().equals("order")) {
          sqlSearch = sqlSearch.concat(" ORDER BY ");
          for (Map.Entry<String, String> order : ordersString.entrySet()) {
            sqlSearch = order.getValue().trim().isEmpty() ? sqlSearch.concat(order.getKey()) : sqlSearch.concat(order.getKey()+""+order.getValue().toUpperCase());
            sqlSearch = sqlSearch.concat(",");
          }
          sqlSearch = sqlSearch.substring(0, sqlSearch.length() - 1);
        }
        else if(param.getKey().equals("size")){
          sqlSearch = sqlSearch.concat(" LIMIT "+param.getValue());
        }
        else if (param.getKey().equals("page")) {
          sqlSearch = sqlSearch.concat(" OFFSET "+param.getValue());
        }
      }
    }

    System.out.println(sqlSearch);
    Query query = em.createNativeQuery(sqlSearch, Log.class);
    try {
      list = query.getResultList();
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }

    return list;
  }

  @Override
  public Page<Log> findAll(Pageable page) {
    return logRepo.findAll(page);
  }

  @Override
  public Log save(Log log) {
    return logRepo.save(log);
  }

  public static Boolean isValidDate(final String inDate) {
    final SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    dateFormat2.setLenient(false);
    try {
      dateFormat2.parse(inDate.trim());
      return true;
    } catch (final ParseException pe) {
      return false;
    }
  }

  @Override
  public Optional<Log> findById(Long id) {
    return logRepo.findById(id);
  }

  public Boolean deleteById(Long id) {
    try {
      logRepo.deleteById(id);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  @Override
  public int getPageNumber() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getPageSize() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getOffset() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Sort getSort() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pageable next() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pageable previousOrFirst() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pageable first() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasPrevious() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public List<Log> findAllList() {
    return logRepo.findAll();
  }

}