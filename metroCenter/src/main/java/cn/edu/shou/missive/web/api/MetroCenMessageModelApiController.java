package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.service.MetroCenMessageModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 张腾飞 on 2016/1/7.
 */
@RestController
@RequestMapping(value = "MetroCen/api")
public class MetroCenMessageModelApiController {
    @Autowired
    MetroCenMessageModelRepository messageModelRepository;

    @RequestMapping(value = "/getMsgContentByStatus/{status}", method = RequestMethod.GET)
    public String getMsgContentByStatus(@PathVariable String status) {
        String msgContent=messageModelRepository.getMsgContentByStatus(status).getMsgContent();

        return msgContent;
    }
}
