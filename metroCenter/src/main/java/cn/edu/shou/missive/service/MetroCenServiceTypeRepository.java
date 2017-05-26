package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenServiceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/1/23.
 */
public interface MetroCenServiceTypeRepository extends PagingAndSortingRepository<MetroCenServiceType,Long> {

    public List<MetroCenServiceType>findAll();

    //根据Id查找名称
    @Query("select serviceType from MetroCenServiceType serviceType where serviceType.id=:typeId")
    public MetroCenServiceType getTypeNameById(@Param("typeId")long typeId);

}
