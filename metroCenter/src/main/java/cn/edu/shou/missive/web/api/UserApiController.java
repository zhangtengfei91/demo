package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.UserFrom;
import cn.edu.shou.missive.service.GroupRepository;
import cn.edu.shou.missive.service.MetroCenJobPositionRepository;
import cn.edu.shou.missive.service.MetroCenPostRepository;
import cn.edu.shou.missive.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
/**
 * Created by Administrator on 2014/7/31.
 */
@RestController
@RequestMapping(value = "/api/user")
public class UserApiController {
    MissivePublishFunction mpf=new MissivePublishFunction();
    @Autowired
    private UserRepository usDAO;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired

    private MetroCenSampleApiController sampleApiController;

    @Autowired
    private MetroCenJobPositionRepository positionRepository;
    @Autowired
    private MetroCenPostRepository postRepository;
    @Autowired GroupRepository groupRepository;
@Autowired
    //获取所有用户数据
    @RequestMapping(value="/getalluser", method= RequestMethod.GET)
    public List<UserFrom> getAlluser(){
        List<UserFrom> userlist =  new ArrayList<UserFrom>();
        List<User> users=usDAO.findAll();
        for (User user:users){
            UserFrom userFrom=mpf.userToUserForm(user);
            userlist.add(userFrom);
        }
        return  userlist;
    }
    //通过用户名获取用户信息
    @RequestMapping(value="/getuserbyusername", method= RequestMethod.GET)
    public User getuserbyusername(String userName){

        return usDAO.findByUserName(userName);
    }
    //通过用户Id获取用户信息
//    @RequestMapping(value="/getuserbyid", method= RequestMethod.GET)
//    public User getuserbyid(Long id){
//
//        return usDAO.findOne(id);
//    }

 //通过用户id获取用户jobPosition名称
   @RequestMapping(value="/getUserById/{userId}", method= RequestMethod.GET)
    public String getuserbyid(@PathVariable long userId){
        User user=usDAO.findOne(userId);
        MetroCenJobPosition position= positionRepository.findOne(user.getJobPosition());
        String positionName=position.getJobPosition();
        return positionName;
    }
    //根据用户Id 获取用户信息 郑小罗 20151122
    @RequestMapping(value = "/getUserByUserId/{userId}",method = RequestMethod.GET)
    public User getUserByUserId(@PathVariable long userId){
        User user=usDAO.findOne(userId);
        return user;
    }

    //电话号码

    //通过用户id获取用户post名称
    @RequestMapping(value="/getUserPostById/{userId}", method= RequestMethod.GET)
    public String getUserPostById(@PathVariable long userId){
        User user=usDAO.findOne(userId);
        String postId=user.getPost();//获取postID
        String [] postIds=postId.split(",");
        MetroCenPost post=null;
        String postName="";
        for (int i=0;i<postIds.length;++i){
            post= postRepository.findOne(Long.parseLong(postIds[i]));
            postName+=post.getPostName()+",";
        }
        return postName;
    }
    //通过用户id获取用户group名称
    @RequestMapping(value="/getUserGroupById/{userId}", method= RequestMethod.GET)
    public String getUserGroupById(@PathVariable long userId){
        User user=usDAO.findOne(userId);
        Group group=groupRepository.findOne(user.getGroup().getId());
        String groupName=group.getGroupName();
        return groupName;
    }
//根据用户Id获取用户手机号码
    @RequestMapping(value="/getUserTelByUserId/{nextUserId}",method = RequestMethod.GET)
    public String  getUserTelByUserId(@PathVariable long nextUserId ){
        User user=usDAO.findOne(nextUserId);
        if(!user.equals(null)){
        String data=user.getTel();
           return data;
        }
        else
            return "0";
    }



    //通过组名获取用户组信息
    @RequestMapping(value="/getuserbygroupname/{groupName}", method= RequestMethod.GET)
    public List<UserFrom> getuserbygroupname(@PathVariable String groupName){
        List<UserFrom> userlist =  new ArrayList<UserFrom>();
        List<User> users=usDAO.findAll();
        for (User user:users){
            UserFrom userFrom=mpf.userToUserForm(user);
            if(userFrom.group!=null&&userFrom.group.groupName.equals(groupName)){
                userlist.add(userFrom);
            }
        }
        return  userlist;
    }
    //通过用户ID验证用户取样/归还样品
    @RequestMapping(value = "/validate/{distributionIds}/{type}/{userId}/{password}")
    @Transactional(rollbackOn=Exception.class)
    public boolean validatePassword(@AuthenticationPrincipal User currentUser,@PathVariable String distributionIds,@PathVariable String type,@PathVariable long userId,@PathVariable String password){
        User user=usDAO.findOne(userId);//查找用户
        String[] distributions=distributionIds.split(",");//拆分分发样品
       // String distributionIds=accreditedId.distributionIds();//获取postID
        if (user==null){
            return false;
        }else {
            if (type.equals("take")){
                if(user.getTakePassword().equals(password)){
                    //更新取样状态
                    for (String distributionId:distributions){//分批对分发样品状态进行更新
                        boolean flag=sampleApiController.updateTakeAndBack(type,Long.parseLong(distributionId));
                        if (!flag){
                            return flag;
                        }
                    }
                    return true;
                }else {
                    return false;
                }

            }else if (type.equals("back")){
                //还样品的时候，是样品分发人员输入密码，也就是当前登入用户输入密码
                if(currentUser.getBackPassword().equals(password)){
                    //更新归还样品状态
                    for (String distributionId:distributions){//分批对分发样品状态进行更新
                        boolean flag=sampleApiController.updateTakeAndBack(type,Long.parseLong(distributionId));
                        if (!flag){
                            return flag;
                        }
                    }
                    return true;

                }else {
                    return false;
                }

            }else {
                return false;
            }
        }
    }
}
