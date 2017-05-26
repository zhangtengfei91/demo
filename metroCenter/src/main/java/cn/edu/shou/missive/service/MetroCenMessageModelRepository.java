package cn.edu.shou.missive.service;
import cn.edu.shou.missive.domain.MetroCenMessageModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * Created by shou on 2015/12/21.
 */
public interface MetroCenMessageModelRepository extends PagingAndSortingRepository<MetroCenMessageModel,Long> {

    public List<MetroCenMessageModel> findAll();

    //根据taskName查找短信模板
    @Query("select msgContent from MetroCenMessageModel msgContent where msgContent.taskName=:taskName")
    public MetroCenMessageModel getMsgContentByStatus(@Param("taskName") String taskName);
}
//张腾飞 20151221