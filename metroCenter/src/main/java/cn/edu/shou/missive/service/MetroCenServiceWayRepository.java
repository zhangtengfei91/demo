package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenServiceType;
import cn.edu.shou.missive.domain.MetroCenServiceWay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/1/23.
 */
public interface MetroCenServiceWayRepository extends PagingAndSortingRepository<MetroCenServiceWay,Long> {
    public List<MetroCenServiceWay>findAll();

    //根据Id查找名称
    @Query("select serviceWay from MetroCenServiceWay serviceWay where serviceWay.id=:serviceId")
    public MetroCenServiceWay getServiceWayById(@Param("serviceId")long serviceId);
}
