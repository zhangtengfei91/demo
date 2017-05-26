package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenProcessType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by Administrator on 2015/3/9 0009.
 */
public interface MetroCenProcessTypeRepository extends PagingAndSortingRepository<MetroCenProcessType,Long> {
    List<MetroCenProcessType>findAll();
}
