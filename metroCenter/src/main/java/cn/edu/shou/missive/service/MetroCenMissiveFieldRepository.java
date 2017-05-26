package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenMissiveField;
import cn.edu.shou.missive.domain.MissiveField;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by Administrator on 2015/3/16 0016.
 */
public interface MetroCenMissiveFieldRepository extends PagingAndSortingRepository<MetroCenMissiveField,Long> {
    List<MetroCenMissiveField>findAll();

}
