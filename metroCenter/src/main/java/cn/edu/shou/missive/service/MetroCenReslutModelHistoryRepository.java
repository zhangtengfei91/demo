package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenReslutModelHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhengxl on 15/6/26.
 */
public interface MetroCenReslutModelHistoryRepository extends PagingAndSortingRepository<MetroCenReslutModelHistory,Long> {
    public List<MetroCenReslutModelHistory> findAll();
}
