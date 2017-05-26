package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissivePublish;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface MissivePublishRepository extends CrudRepository<MissivePublish, Long> {
    public MissivePublish findByProcessID(long processID);
    public List<MissivePublish> findAll();

}