package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenSampleMethod;
import cn.edu.shou.missive.domain.MetroCenSurveillancePro;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/1/28.
 */
public interface MetroCenSurveillanceProRepository extends PagingAndSortingRepository<MetroCenSurveillancePro,Long> {

    public List<MetroCenSurveillancePro>findAll();


    @Query("select surveillancePro from MetroCenSurveillancePro surveillancePro where surveillancePro.id in (:surveillancePro)")

    public List<MetroCenSurveillancePro>getSurveillanceProByIds(@Param("surveillancePro")List surveillancePro);

    //根据Id查找名称
    @Query("select surveillancePro from MetroCenSurveillancePro surveillancePro where surveillancePro.id=:surveillanceProId")
    public MetroCenSurveillancePro getSurveillanceProById(@Param("surveillanceProId")long surveillanceProId);

}
