package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenLabName;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by seky on 15/1/23.
 */
public interface MetroCenLabNameRepository extends PagingAndSortingRepository<MetroCenLabName,Long> {
    List<MetroCenLabName>findAll();
}
