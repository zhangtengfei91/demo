package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenClient;
import cn.edu.shou.missive.domain.MetroCenMissiveField;
import cn.edu.shou.missive.domain.MetroCenSample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by seky on 15/1/12.
 */
public interface MetroCenSampleRepository extends PagingAndSortingRepository<MetroCenSample, Long> {

    @Query("select sample from MetroCenSample sample ")
    public MetroCenSample findAllSample();

    @Query("select sample from MetroCenSample sample where sample.processId in (select runTask.PROC_INST_ID_ from ACT_RU_TASK runTask )")
    public List<MetroCenSample> findSampleByRunProcess();

    //根据流程类型ID和任务ID获取字段加载信息
    @Query("select field from MetroCenMissiveField field where field.processTypeId=:typeId and field.taskId=:tskId")
    public List<MetroCenMissiveField> getFieldInfo(@Param("typeId")long typeId,@Param("tskId")long tskId);

    //根据processId获取sample信息
    @Query("select sample from MetroCenSample sample where sample.processId=:processId")
    public MetroCenSample getSampleInfoByProcessId(@Param("processId")String processId);

    //根据样品Id，查找客户Id
    @Query("select sample.client from MetroCenSample sample where sample.id=:sampleId")
    public MetroCenClient getClientIdBySampleId(@Param("sampleId")long sampleId);

    //根据样品Id，查找客户receptId
    @Query("select sample.receptId from MetroCenSample sample where sample.id=:sampleId")
    public long getReceptIdBySampleId(@Param("sampleId")long sampleId);





    //根据客户名称模糊查询相关信息
    @Query("select sample from MetroCenSample sample where sample.sampleName like:look")
    public List<MetroCenSample>getSampleByLook(@Param("look")String look);

    @Transactional
    @Modifying
    @Query(value="delete from MetroCenSample sample where sample.id =:sampleId")
//    @Query(value = "delete from User u where u.id=:id")
    public void deleteSample (@Param("sampleId")long sampleId);

    //根据日期获取相应sampleCode
    @Query("select max(sample.sampleCode) from MetroCenSample sample where sample.sampleCode like:look")
    public String getSampleCode(@Param("look")String look);

    //获取待处理的样品
    //根据用户ID获取该用户待处理的样品
    @Query("select sample from MetroCenSample sample where sample.receptId=:receptId and sample.distributionId=0 and sample.processId is NULL")
    public Page<MetroCenSample> getWaitSampleByUserId(@Param("receptId") long receptId,Pageable pageable);

    //根据客户Id，，查找样品
    @Query("select sample from MetroCenSample sample where sample.client.id=:clientId")
    public List<MetroCenSample> getSampleByClientId(@Param("clientId")long clientId);

}
