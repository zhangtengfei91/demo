package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenComments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/3/19.
 */
public interface MetroCenCommentRepository extends PagingAndSortingRepository<MetroCenComments,Long> {

    //根据processId获取领导意见
    @Query("select comments from MetroCenComments comments where comments.processId=:processId")
    public List<MetroCenComments>getCommentsByProcessId(@Param("processId")String processId);
}
