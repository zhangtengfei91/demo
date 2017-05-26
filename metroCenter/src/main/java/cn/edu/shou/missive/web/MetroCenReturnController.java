package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.MetroCenDistributionRepository;
import cn.edu.shou.missive.service.MetroCenSampleRepository;
import cn.edu.shou.missive.service.MetroCenSurveillanceProRepository;
import cn.edu.shou.missive.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MetroCenReturnController {
    @Autowired
    private MetroCenSampleRepository sampleRepository;
    @Autowired
    private MetroCenDistributionRepository distributionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MetroCenSurveillanceProRepository surveillanceProRepository;


    @RequestMapping(value = "/return")
    public String getSearch(Model model,@AuthenticationPrincipal User currentUser){
        model.addAttribute("user", currentUser);
        return "MetroCenReturnSearch";
    }

}


