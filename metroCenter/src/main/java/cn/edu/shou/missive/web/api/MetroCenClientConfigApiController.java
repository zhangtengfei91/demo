package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MetroCenClientForm;
import cn.edu.shou.missive.service.MetroCenCertificateRepository;
import cn.edu.shou.missive.service.MetroCenClientRepository;
import cn.edu.shou.missive.service.MetroCenSampleRepository;
import cn.edu.shou.missive.service.MetroCenUpUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/21 0021.
 */
@RestController
@RequestMapping(value = "/MetroCen/api/clientConfig")
public class MetroCenClientConfigApiController {
    @Autowired
    public MetroCenClientRepository clientRepository;
    @Autowired
    public MetroCenUpUnitRepository upUnitRepository;
    @Autowired
    public MetroCenSampleRepository sampleRepository;
    @Autowired
    public MetroCenCertificateRepository cenCertificateRepository;
    @Autowired
    cn.edu.shou.missive.web.CommonFunction commonFunction;

    //read获取客户
    @RequestMapping(value = "/client",method = RequestMethod.GET)
    public List<MetroCenClient> getClient(){
        List<MetroCenClient> clientList =(List<MetroCenClient>) clientRepository.findAll();
        return clientList;
    }

    //创建客户
    @RequestMapping(value = "/createClient",method =RequestMethod.POST)
    public boolean createClient(MetroCenClientForm clientForm,@AuthenticationPrincipal User currentUser){
        MetroCenClient client=null;
        long clientId=clientForm.getId();
        /*更新客户*/
        if(clientId>0){
            client=clientRepository.findOne(clientId);
        }
        /*创建客户*/
        if (client==null){
            client=new MetroCenClient();
            client.setCreatedDate(commonFunction.getCurrentDateTime());
            client.setCreatedBy(currentUser);
        }
        client.setEnterpriseNature(clientForm.getEnterpriseNature());
        client.setAccount(clientForm.getAccount());
        client.setPostCode(clientForm.getPostCode());
        client.setContactSecond(clientForm.getContactSecond());
        client.setUpUnit(clientForm.getUpUnit());
        client.setCertiCode(clientForm.getCertiCode());
        client.setCertiName(clientForm.getCertiName());
        client.setContacts(clientForm.getContacts());
        client.setEmail(clientForm.getEmail());
        client.setContractNo(clientForm.getContractNo());
        client.setPhone(clientForm.getPhone());
        client.setTelephone(clientForm.getTelephone());
        client.setUnitAddress(clientForm.getUnitAddress());
        client.setUnitName(clientForm.getUnitName());
        client.setLastModifiedBy(currentUser);
        client.setLastModifiedDate(commonFunction.getCurrentDateTime());
        clientRepository.save(client);
        return true;
    }

    //删除客户
    @RequestMapping(value = "/deleteClient/{clientId}")
    public List<MetroCenClient> deleteClient(@PathVariable long clientId){
        MetroCenClient pp=clientRepository.findOne(clientId);
        clientRepository.delete(pp);
        List<MetroCenClient> List=new ArrayList<MetroCenClient>();
        List.add(pp);
        return List;
    }

    //获取关联客户
    @RequestMapping(value = "/relationClient/{clientId}",method =RequestMethod.GET)
    public boolean relationClient(@PathVariable long clientId){
        List<MetroCenSample> samples=null;//样品信息
        List<MetroCenCertificate> certificates=null;//证书
        MetroCenClient client=clientRepository.findOne(clientId);
        certificates=cenCertificateRepository.getCertificateByClientId(clientId);
        samples=sampleRepository.getSampleByClientId(clientId);
        Boolean relation=true;
        if(samples.size()==0 && certificates.size()==0){
            relation=false;
        }
        else{
            relation=true;
        }
        return relation;
    }

    //read获取
    @RequestMapping(value = "/upUnit",method = RequestMethod.GET)
    public List<MetroCenUpUnit> getUpUnit(){
        List<MetroCenUpUnit> upUnitList =(List<MetroCenUpUnit>) upUnitRepository.findAll();
        return upUnitList;
    }
    //创建上级主管单位
    @RequestMapping(value="/createUpUnit")
    public List<MetroCenUpUnit> createUpUnit(@RequestParam String upUnit){
        MetroCenUpUnit pp=new MetroCenUpUnit();
        pp.setUpUnit(upUnit);
        upUnitRepository.save(pp);
        List<MetroCenUpUnit> list=new ArrayList<MetroCenUpUnit>();
        list.add(pp);
        return list;
    }
    @RequestMapping(value = "/updateUpUnit")
    public List<MetroCenUpUnit> updateUpUnit(@RequestParam long id,@RequestParam String upUnit){
        MetroCenUpUnit pp=upUnitRepository.findOne(id);
        pp.setUpUnit(upUnit);
        upUnitRepository.save(pp);
        List<MetroCenUpUnit> list=new ArrayList<MetroCenUpUnit>();
        list.add(pp);
        return list;

    }


    @RequestMapping(value ="/deleteUpUnit")
    public List<MetroCenUpUnit> deleteUpUnit(@RequestParam long id){
        MetroCenUpUnit pp=upUnitRepository.findOne(id);
        upUnitRepository.delete(pp);
        List<MetroCenUpUnit> list=new ArrayList<MetroCenUpUnit>();
        list.add(pp);
        return list;

    }
}
