package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenLocationEnvironment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/3/4.
 */
public interface MetroCenLocationEnvironmentRepository extends PagingAndSortingRepository<MetroCenLocationEnvironment,Long> {

    //根据证书ID获取环境及说明数据
    @Query("select location from MetroCenLocationEnvironment location where location.certificateId=:certificateId")
    public MetroCenLocationEnvironment getLocationInfoByCertificateId(@Param("certificateId")long certificateId);
    //根据地址模糊获取环境及说明数据
    @Query("select location from MetroCenLocationEnvironment location where location.location like:location")
    public List<MetroCenLocationEnvironment> getLocationInfoByLocation(@Param("location")String location);

}
