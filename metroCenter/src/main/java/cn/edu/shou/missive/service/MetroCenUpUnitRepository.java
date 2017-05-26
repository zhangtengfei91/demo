package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenUpUnit;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhengxl on 15/7/2.
 */
public interface MetroCenUpUnitRepository extends PagingAndSortingRepository<MetroCenUpUnit,Long> {
    List<MetroCenUpUnit> findAll();
}
