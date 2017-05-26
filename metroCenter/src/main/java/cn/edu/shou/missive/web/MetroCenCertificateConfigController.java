package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created by Administrator on 2015/3/27 0027.
 */
@RequestMapping(value = "MetroCen")
@SessionAttributes(value ={"userbase64","user"} )
@Controller
public class MetroCenCertificateConfigController {

    //证书配置
    @RequestMapping(value = "/CertificateConfig")
    public String getCertificateConfig(Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("user", currentUser);
        model.addAttribute("currentEditableField",null);//将需要加载的字段加载岛模板
        return "MetroCenCertificateConfig";
    }
    //系统配置
    @RequestMapping(value = "/systemConfig")
    public String getTask(Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("user", currentUser);
        model.addAttribute("currentEditableField",null);//将需要加载的字段加载岛模板
        return "MetroCenSystemConfig";
    }

    //客户管理
    @RequestMapping(value = "/ClientConfig")
    public String getClient(Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("user", currentUser);
        model.addAttribute("currentEditableField",null);//将需要加载的字段加载岛模板
        return "MetroCenClientConfig";
    }

    //客户管理
    @RequestMapping(value = "/sampleConfig")
    public String getSample(Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("user", currentUser);
        model.addAttribute("currentEditableField",null);//将需要加载的字段加载岛模板
        return "MetroCenSampleConfig";
    }

    //权限配置
    @RequestMapping(value = "/systemAuthority")
    public String getSystemAuthority(Model model,@AuthenticationPrincipal User currentUser){

        model.addAttribute("user", currentUser);
        model.addAttribute("currentEditableField",null);//将需要加载的字段加载岛模板
        return "MetroCenSystemAuthority";
    }

}
