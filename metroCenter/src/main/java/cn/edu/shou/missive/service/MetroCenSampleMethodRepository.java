package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenCharacterService;
import cn.edu.shou.missive.domain.MetroCenSampleMethod;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/1/23.
 */
public interface MetroCenSampleMethodRepository extends PagingAndSortingRepository<MetroCenSampleMethod,Long> {

    public List<MetroCenSampleMethod>findAll();

    //根据Id查找名称
    @Query("select sampleMethod from MetroCenSampleMethod sampleMethod where sampleMethod.id=:methodId")
    public MetroCenSampleMethod getSampleMethodById(@Param("methodId")long methodId);

}
