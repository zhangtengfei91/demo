package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.GroupFrom;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by XQQ on 2014/8/5.
 */



@RestController
@RequestMapping(value = "/api/group")
    public class GroupApiController{
    @Autowired
    private GroupRepository groupDAO;

    @Autowired
    UserRepository userr;

    @Autowired
    GroupRepository gr;

    @Autowired
    MetroCenJobPositionRepository jobPositionRepository;
    @Autowired
    MetroCenPostRepository postRepository;
    @Autowired
    MetroCenLabNameRepository labNameRepository;
    @Autowired
    MetroCenSurveillanceProRepository surveillanceProRepository;
    @Autowired
    AuthoritiesRepository authoritiesRepository;
    @Autowired
    UserAuthorityRepository userAuthorityRepository;





    MissivePublishFunction msf=new MissivePublishFunction();
    @Autowired
    private GroupRepository gpDAO;
    @RequestMapping(value="/getallgroup", method= RequestMethod.GET)
    public List<GroupFrom> getAllgroup(){
        List<GroupFrom> allGroup= new ArrayList<GroupFrom>();
        List<Group> groups=gpDAO.findAll();
        for(Group g:groups){
            GroupFrom gf=msf.groupToGroupFrom(g);
            allGroup.add(gf);
        }
        return allGroup;
    }

    //所有节点
    @RequestMapping(value = "nodeGroup",method = RequestMethod.GET)
    @ResponseBody
    public List<Object> getAllNodeGroup(){

        List<Object> nodeValue=new ArrayList<Object>();

        //Group parentGroup=groupDAO.findOne((long) 1);  //父节点，东海分局
        List<Group>twoGroupList=groupDAO.getGroupListByGroup("计量中心"); //第二级节点

        //List<Group>parentGroup=this.groupDAO.findAll();
        List<Group>leafGroupList = groupDAO.getAllLeafGroup();   //所有叶子节点

        List<Group>allNodeGroup=new ArrayList<Group>();

        allNodeGroup.addAll(twoGroupList);

        List<Group> middle = new ArrayList<Group>(leafGroupList);
        middle.removeAll(allNodeGroup);
        allNodeGroup.addAll(middle);



        for(Group tempgroup:allNodeGroup)
        {
            Map<String,Object> result = new HashMap<String, Object>();
            result.put("id",tempgroup.getId());
            result.put("groupname",tempgroup.getGroup().getGroupName()+"---"+tempgroup.getGroupName());
            nodeValue.add(result);
        }

        return nodeValue;
    }

    //二级和三级归并
    @RequestMapping(value = "/leafGroup",method = RequestMethod.GET)
    @ResponseBody
    public List<Object> getTwoAndThreeGroup(){

        List<Object> leafValue=new ArrayList<Object>();

        List<Group>twoGroupList=groupDAO.getGroupListByGroup("计量中心");
        List<Group>leafGroupList = groupDAO.getAllLeafGroup();

        for(Group tempgroup:leafGroupList)
        {
            Map<String,Object> result = new HashMap<String, Object>();
            result.put("id",tempgroup.getId());
            result.put("groupname",tempgroup.getGroup().getGroupName()+"---"+tempgroup.getGroupName());
            leafValue.add(result);
        }

        return leafValue;
    }


    //treeView获取所有组
    @RequestMapping(value = "/treeview",method = RequestMethod.GET)
    public List<Group> getGroupByParentId(@RequestParam(value = "id",required = false)Long id){
        List<Group> gplist=null;
        if (id==null){
            Group gp=groupDAO.findByGroupName("计量中心");
            gplist=new ArrayList<Group>();
            gplist.add(gp);
        }else {
            gplist=groupDAO.findOne(id).getGroupList();
        }
        return gplist;
    }

    //grid获取组里所有成员信息
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public List<Object> getUser(@RequestParam(value = "groupId")Long id){
        Group group= gr.findOne(id);
        List<Object> userList = new ArrayList<Object>();
        List<User> users = new ArrayList<User>();
        this.getUsersByGroup(group,users);
        for(User uu:users){
            Map<String,Object>result=new HashMap<String, Object>();
            result.put("groupId",uu.getGroup().getId());
            result.put("id",uu.getId());
            result.put("name",uu.getName());
            result.put("userName",uu.getUserName());
            result.put("sex",uu.getSex());
            result.put("tel",uu.getTel());
            result.put("email",uu.getEmail());
            result.put("password",uu.getPassword());
            result.put("takePassword",uu.getTakePassword());

            result.put("backPassword",uu.getBackPassword());
            result.put("jobPosition",uu.getJobPosition());
            result.put("post",uu.getPost());
            result.put("surveillancePro",uu.getSurveillancePro());
            result.put("lab",uu.getLab());
            result.put("labMan",uu.getLabMan());

            result.put("delayWarm",uu.getDelayWarm());
            userList.add(result);
        }

        return userList;
    }
    //通过组id获取用户方法
    private void getUsersByGroup(Group group,List<User> userlist)
    {
        if (group!=null)
        {
            userlist.addAll(group.getUsers());
            if (group.getGroupList()!=null)
            {
                for (Group childGroup :group.getGroupList())
                {
                    getUsersByGroup(childGroup,userlist);
                }
            }
        }
    }

    //grid获取所有组
    @RequestMapping(value = "/gridGroup",method = RequestMethod.GET)
    public List<Group> getGridGroup(@RequestParam(value = "parentGroup")Long id){
        List<Group> groups = new ArrayList<Group>();
        if (id==1){
            groups=gr.findAllGroups();
        }else {
            Group gn = gr.findOne(id);
            this.getGroupByGroup(gn, groups);
        }
        return groups;
    }
    private void getGroupByGroup(Group group,List<Group> grouplist){

        if (group!=null  )
        {
            grouplist.addAll(group.getGroupList());
            Long groupID=null;
            if (group.getGroupList()!=null){
                for(Group gg:group.getGroupList())
                {   groupID=gg.getId();
                    if (!Long.toString(groupID).equals("2")){
                        getGroupByGroup(gg, grouplist);
                    }
                }
            }
        }
    }
    //删除组
    @RequestMapping(value = "/deleteGroup")
    public List<Group> deleteGroup(@RequestParam long id)
    {
        Group group=gr.findOne(id);
        Group unGroup=gr.findOne(Long.parseLong("0"));   //把字符串转换成long, 找到id为0的未分组
        if(group!=null && unGroup!=null && group.getHasGroups()){

            for(Group tempGroup : group.getGroupList())
            {
                for(User tempUser:tempGroup.getUsers()){
                    tempUser.setGroup(unGroup);
                    unGroup.getUsers().add(tempUser);
                }

                unGroup.getUsers().addAll(tempGroup.getUsers());
                tempGroup.setGroupList(null);
                tempGroup.setUsers(null);

            }
        }
        gr.save(group);
        group.group = null;
        group.setGroupList(null);
        group.setUsers(null);
        gr.save(unGroup);
        gr.delete(group);
        List<Group> list=new ArrayList<Group>();
        list.add(group);
        return list;
    }

    //删除用户
    @RequestMapping(value = "/deleteUser",method = RequestMethod.GET)
    public List<User> deleteUser(@RequestParam long id)
    {
        try{

            userr.deleteUserByID(id);


        }
        catch (Exception ex){

        }

        return null;
    }
    //更新组
    @RequestMapping(value="/updateGroup")
    public List<Group> updateGroup(@RequestParam long id,@RequestParam String groupName,@RequestParam String description)
    {
        Group groups=gr.findOne(id);
        groups.setGroupName(groupName);
        groups.setDescription(description);
        Group editedGroup = gr.save(groups);
        List<Group> grouplist = new ArrayList<Group>();
        grouplist.add(editedGroup);
        return grouplist;
    }
    //更新用户
    @RequestMapping(value="/updateUser")
    public List<User> updateUser(@RequestParam Long groupId,@RequestParam long id,@RequestParam String name,
                                 @RequestParam String userName,@RequestParam String password,
                                 @RequestParam String takePassword,@RequestParam String backPassword,
                                 @RequestParam long jobPosition,@RequestParam String postId,
                                 @RequestParam String labId, @RequestParam String labManId,@RequestParam String surveillanceProId,
                                 @RequestParam String tel,@RequestParam String sex,@RequestParam String email,
                                 @RequestParam String delayWarm)
    {
        User user=userr.findOne(id);
        Group upGroup=gr.findOne(groupId);
        user.setGroup(upGroup);
        user.setName(name);
        user.setUserName(userName);
        user.setPassword(password);
        user.setTakePassword(takePassword);
        user.setBackPassword(backPassword);
        user.setJobPosition(jobPosition);
        user.setPost(postId);
        user.setDelayWarm(delayWarm);
        user.setLab(labId);
        user.setLabMan(labManId);
        user.setSurveillancePro(surveillanceProId);
        user.setTel(tel);
        user.setSex(sex);
        user.setEmail(email);
        User updateUser=userr.save(user);
        List<User> userList=new ArrayList<User>();
        userList.add(updateUser);
        //对权限数据表进行操作
        setAuthority(id,delayWarm);
        return  userList;
    }
    //添加组
    @RequestMapping(value = "/createGroup")
    public List<Group> createGroup(@RequestParam(value = "parentGroup")Long groupId,@RequestParam String groupName,@RequestParam String description) {
        Group upGroup=gr.findOne(groupId);
        Group gg = new Group();

        gg.setGroupName(groupName);
        gg.setDescription(description);
        gg.setIsDel(false);
        gg.setGroup(upGroup);


        gr.save(gg);
        List<Group> list = new ArrayList<Group>();
        list.add(gg);
        return list;

    }
    //添加用户, 用户管理
    @RequestMapping(value = "/createUser")
    public List<User> createUser(@RequestParam Long groupId,@RequestParam String name,@RequestParam String userName,
                                 @RequestParam String password,
                                 @RequestParam String takePassword,@RequestParam String backPassword,
                                 @RequestParam long jobPosition,@RequestParam String postId,
                                 @RequestParam String labId, @RequestParam String labManId,@RequestParam String surveillanceProId,
                                 @RequestParam String sex,@RequestParam String tel,@RequestParam String email,
                                 @RequestParam String delayWarm) {
        Group upGroup=gr.findOne(groupId);
        User userN=userr.findByUserName(userName);//判断用户是否存在
        User user=new User();

        user.setName(name);
        user.setUserName(userName);
        user.setPassword(password);
        user.setTakePassword(takePassword);
        user.setBackPassword(backPassword);
        user.setSex(sex);
        user.setTel(tel);
        user.setEmail(email);
        user.setGroup(upGroup);
        user.setJobPosition(jobPosition);
        user.setDelayWarm(delayWarm);
        user.setPost(postId);
        user.setLab(labId);
        user.setLabMan(labManId);
        user.setSurveillancePro(surveillanceProId);
        user.setEnabled(true);
        userr.save(user);
        //对权限数据表进行操作
        setAuthority(user.id, delayWarm);
        List<User> list = new ArrayList<User>();
        list.add(user);
        return list;


    }
//职称
    @RequestMapping(value = "/jobPosition",method = RequestMethod.GET)
    public List<MetroCenJobPosition> getJobPosition (){
        List<MetroCenJobPosition> jobPositions=(List<MetroCenJobPosition>) jobPositionRepository.findAll();
        return jobPositions;
    }



    //用户更换科室
    @RequestMapping(value = "/getGroupByUser",method = RequestMethod.GET)
    public List<Group> getGroup (){
        List<Group> Groups= gr.findAllGroups();
        return Groups;
    }


 //岗位
     @RequestMapping(value = "/post",method = RequestMethod.GET)
     public List<MetroCenPost> getPost (){
         List<MetroCenPost> postList=postRepository.findAll();
         return postList;
     }

    //实验室
    @RequestMapping(value = "/lab",method = RequestMethod.GET)
    public List<MetroCenLabName> getLab (){
        List<MetroCenLabName> labList=labNameRepository.findAll();
        return labList;
    }

    // 检测项目
    @RequestMapping(value = "/surveillancePro",method = RequestMethod.GET)
    public List<MetroCenSurveillancePro> getSurveillancePro(){
        List<MetroCenSurveillancePro> surveillanceProList=surveillanceProRepository.findAll();
        return surveillanceProList;
    }


    //用户权限

    @RequestMapping(value = "/userAuthority",method = RequestMethod.GET)
    public List<userAuthority> getUserAuthority(){
        List<userAuthority> list=(List<userAuthority>) userAuthorityRepository.findAll();
        return list;
    }

    //管理员

    @RequestMapping(value = "/authority",method = RequestMethod.GET)
    public List<Authorities> getAuthority(){
        List<Authorities> list=(List<Authorities>) authoritiesRepository.findAll();
        return list;
    }

    @RequestMapping(value="")
    public Group getDefaultGroup()
    {
        Group gn=gr.findByGroupName("计量中心");
        return gn;
    }

    //对Authority表进行添加修改
    public void setAuthority(long userId,String authorities){

        Authorities authorities1 =authoritiesRepository.getAuthoritiesByUserId(userId);
        User user=userr.findOne(userId);
        if (authorities1==null){
            authorities1=new Authorities();
            authorities1.setUser(user);
            authorities1.setAuthority(authorities);
            authoritiesRepository.save(authorities1);
        }else{
            authorities1.setAuthority(authorities);
            authoritiesRepository.save(authorities1);

        }

    }



    }

