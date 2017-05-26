package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveVersion;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by seky on 14/12/26.
 */
public interface MissiveSignImgRepository extends CrudRepository<MissiveVersion.MissiveSignImg, Long> {
}
