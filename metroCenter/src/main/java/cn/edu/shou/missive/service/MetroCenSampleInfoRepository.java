package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenSample;
import cn.edu.shou.missive.domain.MetroCenSampleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/4/9.
 */
public interface MetroCenSampleInfoRepository extends PagingAndSortingRepository<MetroCenSampleInfo, Long> {


    public List<MetroCenSampleInfo>findAll();

    //根据出厂编号返回样品信息，因为出厂编号是唯一性
    @Query("select sampleInfo from MetroCenSampleInfo sampleInfo where sampleInfo.factoryCode=:factoryCode")
    public MetroCenSampleInfo findByFactoryCode(@Param("factoryCode")String factoryCode);

    //查询样品信息数据表,根据搜索关键字
    @Query("select sampleInfo from MetroCenSampleInfo sampleInfo where sampleInfo.factoryCode like :factoryCode")
    public List<MetroCenSampleInfo>getSampleInfoBySearch(@Param("factoryCode")String factoryCode);



}
