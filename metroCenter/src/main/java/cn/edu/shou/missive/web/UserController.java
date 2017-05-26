package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.Authorities;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by sqhe on 14-7-23.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Value("${spring.main.uploaddir}")
    String fileUploadDir;
    @Autowired
    UserRepository userDAO;
    @Autowired
    MetroCenJobPositionRepository jobPositionRepository;
    @Autowired
    MetroCenPostRepository postRepository;
    @Autowired
    GroupRepository gr;
    @Autowired
    private AuthoritiesRepository authoritiesRepository;

//    @RequestMapping(value = "/info/current/{userid}.html")
//    public String getCurrentUserInfo(Model model,@PathVariable("userid") Long userid,@AuthenticationPrincipal User currentUser)
//    {
//        User user=this.userDAO.findOne(userid);
//        model.addAttribute("user",currentUser);
//        long jobId=user.getJobPosition();//获取职称ID
//        String postId=user.getPost();//获取岗位ID
//        String[] postIds=postId.split(",");
//        String postName="";
//        String authority="否";
//        //根据职称Id获取职称名称
//        String jobName=jobPositionRepository.findOne(jobId).getJobPosition();
//
//        Authorities authorities=authoritiesRepository.getAuthoritiesByUserId(userid);
//        //查找用户是否管理员
//        if (authorities!=null){
//            authority="是";
//        }else {
//            authority="否";
//        }
//        model.addAttribute("authority",authority);
////        model.addAttribute("user",user);
//        model.addAttribute("jobName",jobName);
//        model.addAttribute("postName",postName);
//        return "userinfo";
//    }

    @RequestMapping(value = "/info/{userid}.html")
    public String getUserInfo(Model model,@PathVariable("userid") Long userid,@AuthenticationPrincipal User currentUser)
    {
        model.addAttribute("user",currentUser);
        User userEdit=this.userDAO.findOne(userid);
        long jobId=userEdit.getJobPosition();//获取职称ID
        String postId=userEdit.getPost();//获取岗位ID
        String[] postIds=postId.split(",");
        String postName="";
        String authority="否";
        //根据职称Id获取职称名称
        String jobName=jobPositionRepository.findOne(jobId).getJobPosition();
        for (int i=0;i<postIds.length;++i){
            postName+=postRepository.findOne(Long.parseLong(postIds[i])).getPostName()+",";
        }

        Authorities authorities=authoritiesRepository.getAuthoritiesByUserId(userid);
        //查找用户是否管理员
        if (authorities!=null){
            authority="是";
        }else {
            authority="否";
        }
        model.addAttribute("authority",authority);
        model.addAttribute("userEdit",userEdit);
        model.addAttribute("jobName",jobName);
        model.addAttribute("postName",postName);
        return "userinfo";
    }

    @RequestMapping(value = "/groupTree")
    public String getgroupTree(Model model,@AuthenticationPrincipal User currentUser)
    {
        model.addAttribute("user", currentUser);
        return "groupTree2";
    }

    @RequestMapping(value = "/userManage")
    public String getUserManage(Model model,@AuthenticationPrincipal User currentUser)
    {
        model.addAttribute("user", currentUser);
        return "MetroCenUserManagement";
    }

    @RequestMapping(value = "/edit/{userid}.html")
    public String getUserInfoForEdit(Model model,@PathVariable Long userid,@AuthenticationPrincipal User currentUser)
    {
        model.addAttribute("userEdit",userDAO.findOne(userid));
        model.addAttribute("user",currentUser);
        String authority="否";

        Authorities authorities=authoritiesRepository.getAuthoritiesByUserId(userid);
        //查找用户是否管理员
        if (authorities!=null){
            authority="是";
        }else {
            authority="否";
        }
        model.addAttribute("authority",authority);


        return "userinfoedit";
    }




    @RequestMapping(value = "/edit/image",method = RequestMethod.POST)
    public String setUserInfo(Model model,@RequestParam String imagePath, @RequestParam Long id)
    {
        User user = userDAO.findOne(id);

        user.setImagePath(imagePath);

        userDAO.save(user);

        model.addAttribute("user",user);

        return "userinfoedit";

    }


    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public String setUserInfo(Model model, @RequestParam String userName,@RequestParam String password,
                             @RequestParam String sex,@RequestParam String email,
                              @RequestParam String tel,@RequestParam Long id,@RequestParam String takePassword,
                              @RequestParam String backPassword)
    {
        //修改 郑小罗 20141204 用户名不能修改
        User user = userDAO.findOne(id);
        user.setPassword(password);

        user.setSex(sex);
        user.setEmail(email);
        user.setTel(tel);
        user.setBackPassword(backPassword);
        user.setTakePassword(takePassword);

        userDAO.save(user);

        model.addAttribute("user",user);


        return "redirect:/user/info/"+id+".html";
    }

    @RequestMapping("/image/{id}")
    public ResponseEntity<byte[]> downFile(@PathVariable Long id)  {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");

        //默认文件名称
        User user = userDAO.findOne(id);

        String path="";
        String downFileName="";
        String filePath="";//文件路径
        String defaultFile=fileUploadDir+"/default.jpg";

        downFileName = user.getImagePath();

        if(downFileName!=null){
            path = fileUploadDir+"/userImg/"+id;
        }else {
            path=fileUploadDir+"/";
            downFileName="default.jpg";
        }
//        try {
//            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", downFileName);
        filePath=path + "/" + downFileName;//头像文件，判断是否存在，不存在使用默认头像
        if (!FileOperate.exitFile(filePath)){
            filePath=defaultFile;//如果头像不存在，使用系统默认的头像文件
        }
        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),
                    headers,
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            //日志
            //TODO
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }
    //获取用户签名图片
    @RequestMapping("/getSignImg/{userId}")
    public ResponseEntity<byte[]>getUserSignImg(@PathVariable Long userId){
        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "");
        byte[] img=new byte[1024];//接收签名字符流
        try {
            //默认文件名称
            User user = userDAO.findOne(userId);
            if (user!=null) {
                img=user.getSignImg();

                return new ResponseEntity<byte[]>(img,
                        headers,
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //日志
            //TODO
        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }
}
