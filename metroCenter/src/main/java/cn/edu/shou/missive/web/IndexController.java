package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.NotificationRepository;
import jodd.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sqhe on 14-7-7.
 */
@Controller
@RequestMapping(value="")
@SessionAttributes(value = {"userbase64","user"})
public class IndexController {

    @RequestMapping("/")
    public String index()
    {
        //return "index";
        return "redirect:/MetroCen/index/Sample/1";
    }


    @ModelAttribute(value = "userbase64")
    public String addUserBase64toPage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
        if (temp!=null && temp.getClass()== org.springframework.security.core.userdetails.User.class )
        {
            User myUser = (User) temp;
            String base64Code = "Basic " + Base64.encodeToString(myUser.getUsername() + ":" + myUser.getUsername());
            return base64Code;
        }
        else
            return "";

    }

    @ModelAttribute(value = "user")
    public User addUsertoPage()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object temp = ((auth != null) ? auth.getPrincipal() :  null);
        if (temp!=null && temp.getClass()==User.class )
        {
            return (User)temp;
        }
        else
            return null;

    }


    @RequestMapping(value="/index.html")
    public String getIndex() {
        return "index";
    }

    @RequestMapping(value="")
    public String getDefaultIndex() {
        return "index";
    }


    @Autowired
    private NotificationRepository nrDAO;

    @RequestMapping(value="/notification")
    public String getNotification() {

        return "notification";
    }

    @RequestMapping(value = "/groupTree")
    public String getGroupTree(Model model)
    { return "groupTree2";
    }


    @RequestMapping(value="/fullSearch/{searchValue}")
    public String getFullSearch(@PathVariable("searchValue")String searchValue,Model model) {

        model.addAttribute("searchValue",searchValue);
        return "FullSearch";
    }

    @RequestMapping(value="/historyProcess/{instanceId}")
    public String getHistoryProcess(@PathVariable("instanceId") Long instanceId,Model model) {

        model.addAttribute("instanceId",instanceId);

        return "HistoryProcess";
    }

    @RequestMapping(value = "/config")
    public String getMissiveType(){
        return "config";
    }

    //测试发送邮件
    @RequestMapping(value = "/mailTemplate",method = RequestMethod.GET)
    public String mail(){

        return "emailTemplate";
    }
    //测试插入签名
    @RequestMapping(value = "/insertName",method = RequestMethod.GET)
    public String insertName(){
        return "canvas_image";
    }
    //测试鼠标邮件
    @RequestMapping(value = "/rightClick",method = RequestMethod.GET)
    public String rightClick(){
        return "ContextMenu";
    }

    //局部打印测试
    @RequestMapping(value = "/areaPrint")
    public  String areaPrint(){
        return "areaPrint";
    }

}
