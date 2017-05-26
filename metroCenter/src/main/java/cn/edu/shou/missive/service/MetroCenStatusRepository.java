package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by seky on 15/1/26.
 */
public interface MetroCenStatusRepository extends PagingAndSortingRepository<MetroCenStatus,Long> {

    public List<MetroCenStatus>findAll();
}
