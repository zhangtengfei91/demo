package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.userAuthority;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhengxl on 15/6/25.
 */
public interface UserAuthorityRepository extends PagingAndSortingRepository<userAuthority,Long> {
    List<userAuthority> findAll();
}

