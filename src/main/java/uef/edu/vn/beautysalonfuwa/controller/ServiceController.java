package uef.edu.vn.beautysalonfuwa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uef.edu.vn.beautysalonfuwa.service.ServiceData;

@Controller
public class ServiceController {
    @Autowired
    private ServiceData serviceData;

    @GetMapping("/services")
    public String showServices(Model model) {
        model.addAttribute("services", serviceData.findAll());
        return "customer/services";
    }
}
