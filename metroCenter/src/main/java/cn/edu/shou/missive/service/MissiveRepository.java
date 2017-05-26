package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Missive;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2014/8/2.
 */
public interface MissiveRepository extends PagingAndSortingRepository<Missive, Long>{
    public List<Missive> findAll();
    public Missive findById(Long id);
    @Query("select miss from Missive miss where miss.id=:id")
    public List<Missive>getMissiveByID(@Param("id")Long ID);
    //监测公文号是否存在
    @Query("select miss from Missive miss where miss.missiveNum=:missNum")
    public List<Missive> getMissiveByMissiveNum(@Param("missNum")String missNum);

}
