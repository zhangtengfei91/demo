package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenIdentifier;
import cn.edu.shou.missive.domain.MetroCenServiceWay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import sun.util.resources.cldr.ga.LocaleNames_ga;

import java.util.List;

/**
 * Created by seky on 15/4/23.
 */
public interface MetroCenIdentifierRepository extends PagingAndSortingRepository<MetroCenIdentifier,Long> {

    //查找
    @Query("select max(identifier.serialNumber) from MetroCenIdentifier identifier where identifier.sampleId<>0")
    public Object getMaxSerialNumber();

    //查找对应样本编号的条码
    @Query("select serialNumber from MetroCenIdentifier serialNumber where serialNumber.sampleId=:sampleId")
    public MetroCenIdentifier getSerialNumberBySampleId(@Param("sampleId")long sampleId);

    //根据Identifier查processId
    @Query("select sample.processId from MetroCenSample sample where  sample.id=:sampleId")
    public String getProcessId(@Param("sampleId") long sampleId);
    @Query("select identifier.sampleId from MetroCenIdentifier identifier where identifier.serialNumber =:identifier and identifier.sampleId<>0")
    public Object getSampleId(@Param("identifier")String identifier);

}



