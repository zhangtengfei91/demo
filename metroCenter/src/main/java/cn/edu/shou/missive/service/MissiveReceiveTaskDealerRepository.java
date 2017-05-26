package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveReceiveTaskDealer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by TISSOT on 2014/9/19.
 */
public interface MissiveReceiveTaskDealerRepository extends CrudRepository<MissiveReceiveTaskDealer,Long> {
    @Query("select a from MissiveReceiveTaskDealer a where a.instanceId=?1")
    public MissiveReceiveTaskDealer getTaskDealer(Long instanceId);

}
