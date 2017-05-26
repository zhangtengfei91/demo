package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by sqhe on 14-7-20.
 */
//@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    public List<User> findAll();

    @Query(value = "select t.userName,t.tel,t.description as name,t.id from User t where t.description IS NOT null and t.description <> ''")
    public List<User> getUsernames();

    public User findByUserName(String username);

//    public User findByName(String name);

    @Query(value = "select t.userName from User t where t.id=?1")
    public String findUserNameById(Long id);

    @Query(value = "select t.Name from User t where t.id=?1")
    public String findUserCNameById(Long id);

    //根据ID 返回List<User对象>
    @Query("select  u from User u where u.id=:id")
    public List<User>getUserInfoByID(@Param("id")Long ID);

    @Query(value = "select t from User t where t.group.id in ?1")
    public List<User> getUserListByGroupList(List<Long> groupIdList);

    @Query(value = "select t from User t where t.id in ?1")
    public List<User> getUserListByIdList(List<Long> userIdList);

    //根据用户名返回用户编号
    @Query(value = "select u.id from User u where u.userName=:name")
    public String getUserIDByUserName(@Param("name")String name);


        //根据用户ID删除记录
    @Modifying
    @Transactional
    @Query(value = "delete from User u where u.id=:id")
    public void deleteUserByID(@Param("id")Long id);

    //根据groupID获取用户列表
    @Query(value = "select u from User u where u.group.id=:groupID")
    public List<User>getUserListByGroupID(@Param("groupID")Long groupID);

    //根据post获取用户列表
    @Query(value = "select u from User u where u.Post like:post")
    public List<User>getUserListByPostId(@Param("post")String post);

    //根据post获取用户列表
    @Query(value = "select u from User u where u.Post like:post and u.labMan like:lab")
    public List<User>getUserListByLabId(@Param("post")String post,@Param("lab")String lab);


    public User findByUserNameAndPassword(String username,String passwork);

    //根据用户名获取用户
    @Query(value = "select u from User u where u.userName=:userName")
    public User getUserInfoByUserName(@Param("userName")String userName);//获取中文名部分不能显示为啥

    @Query(value = "select u.Name from User u where u.userName=:userName")
    public String  getCNameByEName(@Param("userName")String userName);//上面方法的补充


    @Query(value = "select u.id from User u where u.Name like:name")
    public long getUserIdByCName(@Param("name")String name);

    //根据用户名取手机号码

    @Query(value = "select u.tel from User u where u.id=:id")
    public String getUserTelByUserId(@Param("id") long id);


}
