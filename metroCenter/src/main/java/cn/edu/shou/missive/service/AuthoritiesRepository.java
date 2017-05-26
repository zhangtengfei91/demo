package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Attachment;
import cn.edu.shou.missive.domain.Authorities;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by sqhe on 14-8-13.
 */
public interface AuthoritiesRepository extends PagingAndSortingRepository<Authorities,Long> {

    @Query("select author from Authorities author where  author.user.id=:userId")
    public Authorities getAuthoritiesByUserId(@Param("userId") long userId);

    //根据用户ID删除记录
    @Modifying
    @Transactional
    @Query(value = "delete from Authorities author where author.user.id=:userId")
    public void deleteAuthorByUserID(@Param("userId") Long userId);

}
