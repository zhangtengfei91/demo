package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenTaskName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by jiliwei on 2014/7/18.
 */
public interface TaskNameRepository extends PagingAndSortingRepository<MetroCenTaskName, Long> {

    List<MetroCenTaskName>findAll();

    MetroCenTaskName findByTaskName(String taskName);

    public List<MetroCenTaskName> findByTaskNameAndProcessType(String taskName,long processTypeId);

    @Query("select a from MetroCenTaskName a where a.taskName=:taskName and a.processTypeId=:processTypeId")
    public MetroCenTaskName findByName(@Param("taskName")String taskName,@Param("processTypeId")long processTypeId );

    //根据ID获取任务实例
    @Query("select tsk from MetroCenTaskName tsk where tsk.id=:id")
    public MetroCenTaskName getTaskNameByID(@Param("id")long id);
    //根据名称获取任务实例
    @Query("select tsk from MetroCenTaskName tsk where tsk.taskName=:tskName and tsk.processTypeId=:processTypeId")
    public MetroCenTaskName getTaskNameByName(@Param("tskName")String tskName,@Param("processTypeId")long processTypeId);
}
