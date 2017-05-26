package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenCertificateBackGround;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by seky on 15/3/13.
 */
public interface MetroCenCertificateBackGroundRepository extends PagingAndSortingRepository<MetroCenCertificateBackGround,Long> {

    //根据模板Id返回背景图片信息
    @Query("select backGround from MetroCenCertificateBackGround backGround where backGround.modelId=:modelId")
    public MetroCenCertificateBackGround getBackGroundByModelId(@Param("modelId")long modelId);
}
