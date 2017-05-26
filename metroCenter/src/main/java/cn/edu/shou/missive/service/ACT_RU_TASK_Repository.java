package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.ACT_RU_TASK;
import cn.edu.shou.missive.domain.FaxCablePublish;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by seky on 14-7-28.
 */
public interface ACT_RU_TASK_Repository extends PagingAndSortingRepository<ACT_RU_TASK, Long> {
    @Query("select act from ACT_RU_TASK act where act.ID_=:id and act.PROC_INST_ID_=:instID")
    public ACT_RU_TASK getACTTaskNameByIDAndInstID(@Param("id") String id, @Param("instID") String instID);

    @Query("select act from ACT_RU_TASK act where act.PROC_INST_ID_=:instID")
    public ACT_RU_TASK getACTTaskNameByInstID(@Param("instID") String instID);
}
