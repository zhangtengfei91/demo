package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.User;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created by seky on 15/1/8.
 */
@RequestMapping(value="MetroCen")
@SessionAttributes(value = {"userbase64","user"})
@Controller
public class NewProcessController {

    @RequestMapping(value = "/NewProcess")
    public String getNewProcess(Model model,@AuthenticationPrincipal User currentUser){
        model.addAttribute("user", currentUser);
        return "NewProcess";
    }
}
