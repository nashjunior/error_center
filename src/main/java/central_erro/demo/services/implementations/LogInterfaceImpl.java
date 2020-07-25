package central_erro.demo.services.implementations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

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