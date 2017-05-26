package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenStandardEquipmentHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhengxl on 15/6/26.
 */
public interface MetroCenStandardEquipmentHistoryRepository extends PagingAndSortingRepository<MetroCenStandardEquipmentHistory,Long> {
    public List<MetroCenStandardEquipmentHistory> findAll();
}

