package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.MissivePublishFunction;
import cn.edu.shou.missive.domain.SignType;
import cn.edu.shou.missive.domain.missiveDataForm.SignTypeForm;
import cn.edu.shou.missive.service.SignTypeRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sqhe18 on 14-9-5.
 */
@RestController
@RequestMapping(value = "/api/missive/signType")
public class SignTypeApiControler {
    @Autowired
    private SignTypeRespository str;
    MissivePublishFunction msf=new MissivePublishFunction();
    @RequestMapping(value="/getsigntype", method= RequestMethod.GET)
    public List<SignTypeForm> getSignType(){
        List<SignTypeForm> stfs=new ArrayList<SignTypeForm>();
        List<SignType> sts=str.findAll();
        for(SignType st:sts){
            SignTypeForm stf=msf.signTypeTosignTypeForm(st);
            stfs.add(stf);
        }
        return stfs;
    }
}
