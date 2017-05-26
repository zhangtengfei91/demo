package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenReslutIllustrate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by seky on 15/3/4.
 */
public interface MetroCenReslutIllustrateRepository extends PagingAndSortingRepository<MetroCenReslutIllustrate,Long> {

    //根据证书ID获取结果说明
    @Query("select result from MetroCenReslutIllustrate result where result.certificateId=:certificateId")
    public MetroCenReslutIllustrate getResultIllustrateByCertificateId(@Param("certificateId")long certificateId);
}
