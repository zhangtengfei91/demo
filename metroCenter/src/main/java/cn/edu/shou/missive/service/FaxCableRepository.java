package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;


import java.util.List;

/**
 * Created by seky on 14-7-24.
 */
public interface FaxCableRepository extends PagingAndSortingRepository<FaxCablePublish, Long> {
    @Query("select field from MissiveField field where field.processType=:procType and field.taskName=:tskName")

    //根据处理类型和任务名称获取字段信息
    public List<MissiveField> findByTaskIDAndProcessTypeID(@Param("procType") ProcessType procType, @Param("tskName") TaskName tskName);

    //根据ID获取传真电报所有信息
    @Query("select faxCable from FaxCablePublish faxCable where faxCable.id=:id")
    public List<FaxCablePublish>getFaxCablePublishByID(@Param("id") Long ID);

    //根据ID获取传真电报所有信息
    @Query("select faxCable from FaxCablePublish faxCable where faxCable.id=:id")
    public FaxCablePublish getFaxCableByID(@Param("id") Long ID);

    //根据ID获取MissiveInfoID
    @Query("select faxCable.missiveInfo from FaxCablePublish faxCable where faxCable.id=:id")
    public Missive getMissiveInfoIDByID(@Param("id") Long id);
    //根据MissiveID获取版本信息
    @Query("select ver from MissiveVersion ver where ver.missive.id=:id order by ver.id desc")
    public MissiveVersion getMissiveVersionByMissiveID(@Param("id") Long missiveID);

    @Query(value = "select ver from MissiveVersion ver where ver.missive.id=:id order by ver.id desc")
    public Page<MissiveVersion> getMissiveVersionByMissiveID(@Param("id") Long missiveID, Pageable pageable);
    //获取公文最大ID
    @Query("select max(faxCable.id) from FaxCablePublish faxCable")
    public Long getMaxID();//获取最大ID

    @Query("select faxCable.missiveInfo.id from FaxCablePublish faxCable where faxCable.id=:id")
    public String getMissiveInfoIDByFaxCableID(@Param("id") Long id);

    //根据公文ID返回签发内容ID
    @Query("select faxCable.signIssueContent.id from FaxCablePublish faxCable where faxCable.id=:id")
    public String getSignCommentContentIDByFaxCableID(@Param("id") Long id);

    //根据公文ID返回处室领导内容ID
    @Query("select faxCable.leaderSignContent.id from FaxCablePublish faxCable where faxCable.id=:id")
    public String getDepCommentContentIDByFaxCableID(@Param("id") Long id);

    //根据processID返回公文ID
    @Query("select faxCable.missiveInfo.id from FaxCablePublish faxCable where faxCable.processID=:processID")
    public String getMissiveIDByProcessID(@Param("processID") Long processID);

    //根据processID返回背景图片地址
    @Query("select faxCable.bgPngPath from FaxCablePublish faxCable where faxCable.processID=:processID")
    public String getBgPngPathByProcessID(@Param("processID") Long processID);

    //根据processID返回公文
    @Query("select faxCable from FaxCablePublish faxCable where faxCable.processID=:processID")
    public FaxCablePublish getMissiveByProcessID(@Param("processID") Long processID);

    //根据MissiveID返回公文
    @Query("select faxCable.id from FaxCablePublish faxCable where faxCable.missiveInfo.id=:missiveID")
    public Long getFaxCableIDByMissiveID(@Param("missiveID") Long missiveID);

    //根据ProcessID获取拟稿人ID
    @Query("select faxCable.DrafterUser from FaxCablePublish faxCable where faxCable.processID=:processID")
    public User getDrafterUserByProcessID(@Param("processID") Long processID);

    //根据ProcessID获取FaxCable
    @Query("select faxCable from FaxCablePublish faxCable where faxCable.processID=:processID")
    public  FaxCablePublish getFaxCableByProcessID(@Param("processID")Long processID);

    //根据processID返回公文
    @Query("select faxCable.missiveInfo from FaxCablePublish faxCable where faxCable.processID=:processID")
    public Missive getMissiveInfoByProcessID(@Param("processID") Long processID);

}
