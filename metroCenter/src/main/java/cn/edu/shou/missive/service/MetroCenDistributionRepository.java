package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenDistribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by seky on 15/1/21.
 */
public interface MetroCenDistributionRepository extends PagingAndSortingRepository<MetroCenDistribution,Long> {

    //获取全部分发的样品
    List<MetroCenDistribution>findAll();
    //根据流程Id获取分发信息
    @Query("select distribution from MetroCenDistribution distribution where distribution.processId=:processId")
    public List<MetroCenDistribution>getDistributionByProcessId(@Param("processId")String processId);
    //根据流程Id获取分发信息
    @Query("select distribution from MetroCenDistribution distribution where distribution.processId=:processId and distribution.statusName.id=:statusName")
    public List<MetroCenDistribution>getDistributionByProcessId(@Param("processId")String processId,@Param("statusName")long statusName);

    //根据用户ID获取该用户已分发的样品
    @Query("select distribution from MetroCenDistribution distribution where distribution.distributionId=:distributionId order by distribution.createdDate desc")
    public Page<MetroCenDistribution> getDistributionByDistributionId(@Param("distributionId") long distributionId,Pageable pageable);

    @Transactional
    @Modifying
    @Query (value="delete from MetroCenDistribution distribution where  distribution.sampleId=:sampleId")
    public void deleteDistribution(@Param("sampleId")long sampleId);

    //根据样品ID获取该样品信息
    @Query("select distribution from MetroCenDistribution distribution where distribution.sampleId=:sampleId")
    public MetroCenDistribution getDistributionBySampleId(@Param("sampleId")long sampleId);

    //根据样品ID获取该样品信息列表
    @Query("select distribution from MetroCenDistribution distribution where distribution.sampleId=:sampleId")
    public List<MetroCenDistribution> getDistributionsBySampleId(@Param("sampleId")long sampleId);

    //根据样品ID、流程编号、样品流转状态获取样品流转信息
    @Query("select distribution from MetroCenDistribution distribution where distribution.sampleId=:sampleId " +
            "and distribution.processId=:processId and distribution.taskId=:taskId and (distribution.statusName.id=:statusId or distribution.statusName.id=:returnStatusId)")
    public MetroCenDistribution getDistributionBySampleId(@Param("sampleId")long sampleId,@Param("processId")String processId,
                                                          @Param("statusId")long statusId,@Param("returnStatusId")long returnStatusId,@Param("taskId")String taskId);

    //根据检定员ID，获取该检定员所有检定的样品
    @Query("select distribution from MetroCenDistribution distribution where distribution.accreditedId.id=:accreditedId and distribution.statusName.id<>4 and distribution.certifiSubmit<>1")
    public List<MetroCenDistribution>getDistributionByAccreditedId(@Param("accreditedId")long accreditedId);

    //根据检定员Id，获取该检定员所有未取的样品
    @Query("select  distribution from MetroCenDistribution distribution where distribution.accreditedId.id=:accreditedId and distribution.certifiSubmit<>1 and distribution.take=:takeInfo")
    public List<MetroCenDistribution>getTakeSampleByAccreditedId(@Param("accreditedId")long accreditedId,@Param("takeInfo")String takeInfo);

    //根据检定员Id，获取该检定员所有未还的样品
    @Query("select  distribution from MetroCenDistribution distribution where distribution.accreditedId.id=:accreditedId and distribution.back=:backInfo")
    public List<MetroCenDistribution>getBackSampleByAccreditedId(@Param("accreditedId")long accreditedId,@Param("backInfo")String backInfo);

    //根据processId获取已监测项目(1)和未检测项目(0)
    @Query("select distribution from MetroCenDistribution distribution where distribution.processId=:processId and distribution.certifiSubmit=:certifiSubmit")
    public List<MetroCenDistribution> getSurveillanceProList(@Param("certifiSubmit")int certifiSubmit,@Param("processId")String processId);

    //根据检定者Id,获取该检定员所有未取的样品Id
   // @Query("select distributionIds from MetroCenDistribution distribution where distribution.accreditedId.id=:accreditedId and distribution.certifiSubmit<>1 and distributionIds.take=:takeInfo")
   // public List<MetroCenDistribution>getDistributionIdsByAccreditedId(@Param("accreditedId")long accreditedId,@Param("takeInfo")String takeInfo);

}
