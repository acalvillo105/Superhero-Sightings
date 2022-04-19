/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sg.superheros.controller;

import com.sg.superheros.dao.HeroDao;
import com.sg.superheros.dao.LocationDao;
import com.sg.superheros.dao.OrganizationDao;
import com.sg.superheros.dao.SightingsDao;
import com.sg.superheros.dao.SuperpowerDao;
import com.sg.superheros.entities.Superpower;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author acalvillo
 */
@Controller
public class SuperpowerController {
    @Autowired
    HeroDao heroDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    OrganizationDao organizationDao;
    
    @Autowired
    SightingsDao sightingsDao;
    
    @Autowired
    SuperpowerDao superpowerDao;
    
    @GetMapping("superpowers")
    public String displaySuperpowers(Model model){
        List<Superpower> superpowers = superpowerDao.getAllSuperpowers();
        model.addAttribute("superpowers", superpowers);
        return "superpowers";
    }
    
    @PostMapping("addSuperpower")
    public String addSuperpower(String name){
        Superpower superpower = new Superpower();
        superpower.setPower(name);       
        superpowerDao.addSuperpower(superpower);
        
        return "redirect:/superpowers";
    }
    @GetMapping("deleteSuperpower")
    public String deleteSuperpower(Integer id) {
        superpowerDao.deleteSuperpowerById(id);
        return "redirect:/superpowers";
    }
    
    @GetMapping("editSuperpower")
    public String editSuperpower(Integer id, Model model) {
        Superpower superpower = superpowerDao.getSuperPowerById(id);
        model.addAttribute("superpower", superpower);
        return "editSuperpower";
    }
    
    @PostMapping("editSuperpower")
    public String performEditSuperpower(Superpower superpower, HttpServletRequest request) {       
        superpowerDao.updateSuperpower(superpower);        
        return "redirect:/superpowers";
    }
   
}
