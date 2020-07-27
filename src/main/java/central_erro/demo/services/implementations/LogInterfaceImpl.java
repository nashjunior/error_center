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

  

  @Override
  public List<Log> findAllList() {
    return logRepo.findAll();
  }

  public List<?> findAllLogsByParam(final Map<String, String> params, final Map<String, String> ordersString,
      final Boolean isFilter, final Boolean isSized, final Boolean isPageable) {
    String sqlSearch = "SELECT l.* FROM Log l WHERE ";
    List<?> list = new ArrayList<>();
    if (isFilter) {
      for (final Map.Entry<String, String> param : params.entrySet()) {
        // verifica se parametro e tipo date
        if (param.getKey().equals("date") && isValidDate(param.getValue().toString())) {
          sqlSearch = sqlSearch
              .concat("date(" + param.getKey() + ")=to_date('" + param.getValue().toString() + "','DD/MM/YYYY') AND ");
          params.remove(param.getValue());
        }
        // verifica se o parametro não e de paginacao
        else if (!param.getKey().equals("page") && !param.getKey().equals("size") && !param.getKey().equals("order")) {
          sqlSearch = sqlSearch.concat(param.getKey() + "='" + param.getValue().toString() + "' AND ");
          params.remove(param.getValue());
        }
      }
      sqlSearch = sqlSearch.substring(0, sqlSearch.length() - 4);
    } else
      sqlSearch = sqlSearch.substring(0, sqlSearch.length() - 7);

    System.out.println(sqlSearch);
    final Query query = em.createNativeQuery(sqlSearch, Log.class);
    if (ordersString != null) {
      for (final Map.Entry<String, String> param : params.entrySet()) {
        if (param.getKey().equals("order")) {
          sqlSearch = sqlSearch.concat(" ORDER BY ");
          for (final Map.Entry<String, String> order : ordersString.entrySet()) {
            query.setParameter(order.getKey(), order.getValue());
          }
          sqlSearch = sqlSearch.substring(0, sqlSearch.length() - 1);
        }
      }
    }
    if (isSized)
      query.setMaxResults(Integer.parseInt(params.get("size")));
    if (isPageable)
      query.setFirstResult(Integer.parseInt(params.get("page")));

    try {
      list = query.getResultList();
    } catch (final RuntimeException e) {
      System.out.println(e.getMessage());
    }

    return list;
  }

  @Override
  public Log save(final Log log) {
    log.setQuantity(logRepo.countByLevel(log.getLevel())+1);
    return logRepo.save(log);
  }

  @Override
  public Optional<Log> findById(final Long id) {
    return logRepo.findById(id);
  }

  @Override
  public Boolean deleteById(final Long id) {
    try {
      logRepo.deleteById(id);
    } catch (final Exception e) {
      return false;
    }
    return true;
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

}