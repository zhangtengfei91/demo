package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenTaskName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2015/3/11 0011.
 */
public interface MetroCenTaskNameRepository extends PagingAndSortingRepository<MetroCenTaskName,Long> {

    List<MetroCenTaskName>findAll();

    MetroCenTaskName findByTaskName(String taskName);

    public List<MetroCenTaskName> findByTaskNameAndProcessType(String taskName,long processTypeId);

    @Query("select tsk from MetroCenTaskName tsk where tsk.taskName=:taskName and tsk.processTypeId=:processTypeId")
    public MetroCenTaskName findByName(@Param("taskName")String taskName,@Param("processTypeId")long processTypeId);

    //根据ID获取任务实例
    @Query("select tsk from MetroCenTaskName tsk where tsk.id=:id")
    public MetroCenTaskName getTaskNameByID(@Param("id")long id);
    //根据名称获取任务实例
    @Query("select tsk from MetroCenTaskName tsk where tsk.taskName=:tskName and tsk.processTypeId=:processTypeId")
    public MetroCenTaskName getTaskNameByName(@Param("tskName")String tskName,@Param("processTypeId")long processTypeId);
}
