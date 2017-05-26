package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.ACT_RU_TASK;
import cn.edu.shou.missive.domain.MetroCenCertificate;
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
public interface MetroCenHistotyRepository extends PagingAndSortingRepository<ACT_RU_TASK,Long> {

    //根据流程Id获取流程信息对象
    @Query("select runTask from ACT_RU_TASK runTask where runTask.PROC_INST_ID_=:processId")
    public ACT_RU_TASK getRunTaskByProcessId(@Param("processId") String processId);


    @Query("select runTask from ACT_RU_TASK runTask")
    public ACT_RU_TASK getAll();

    @Query("select certificate from MetroCenCertificate certificate where certificate.processId in (select runTask from ACT_RU_TASK runTask where runTask.PROC_DEF_ID_='SampleDistr:1:8' )")
    public MetroCenCertificate getCertificateByRunProcess();

    @Modifying
    @Query (value="delete from MetroCenDistribution distribution where  distribution.sampleId=:sampleId")
    public void deleteDistribution(@Param("sampleId") long sampleId);

    //根据样品ID获取该样品信息
    @Query("select distribution from MetroCenDistribution distribution where distribution.sampleId=:sampleId")
    public MetroCenDistribution getDistributionBySampleId(@Param("sampleId") long sampleId);


}
