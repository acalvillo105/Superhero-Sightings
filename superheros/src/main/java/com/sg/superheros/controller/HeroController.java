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
import com.sg.superheros.entities.Hero;
import com.sg.superheros.entities.Organization;
import com.sg.superheros.entities.Superpower;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author acalvillo
 */
@Controller
public class HeroController {
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
    
    @GetMapping("heros")
    public String displayHeros(Model model){
        List<Hero> heros = heroDao.getAllHeros();
        List<Superpower> superpowers = superpowerDao.getAllSuperpowers();        
        model.addAttribute("heros", heros);
        model.addAttribute("superpowers", superpowers);
        
        try{
            List<Organization> organizations = organizationDao.getAllOrganizations();
            model.addAttribute("organizations", organizations);
        }catch(DataAccessException ex){
            
        }
        return "heros";
    }
    
    @PostMapping("addHero")
    public String addHero(Hero hero, HttpServletRequest request){
        String superpowerId = request.getParameter("superpowerId");
        hero.setSuperpower(superpowerDao.getSuperPowerById(Integer.parseInt(superpowerId)));//hero needs superpower to be considered "super"
        String[] organizations = request.getParameterValues("organizationId");    
        
        if(organizations != null){//hero does not need to be part of organization
            List<Organization> orgs = new ArrayList<>();
            for(String organizationId: organizations){
                orgs.add(organizationDao.getOrganizationById(Integer.parseInt(organizationId)));
            }
            hero.setOrganizations(orgs);
        }
        heroDao.addHero(hero);
        
        return "redirect:/heros";
    }
    
    @GetMapping("heroDetail")
    public String heroDetail(Integer id, Model model){
        Hero hero = heroDao.getHeroById(id);
        model.addAttribute("hero", hero);
        return "heroDetail";
    }
    
    @GetMapping("editHero")
    public String editHero(Integer id, Model model){
        Hero hero = heroDao.getHeroById(id);
        List<Superpower> superpowers = superpowerDao.getAllSuperpowers();
        List<Organization> orgs = organizationDao.getAllOrganizations();
        
        model.addAttribute("hero", hero);
        model.addAttribute("superpowers", superpowers);
        model.addAttribute("organizations", orgs);
        return "editHero";
    }
    
    @PostMapping("editHero")
    public String performEditHero(Hero hero, HttpServletRequest request) {
        String superpowerId = request.getParameter("superpowerId");
        hero.setSuperpower(superpowerDao.getSuperPowerById(Integer.parseInt(superpowerId)));//hero needs superpower to be considered "super"
        String[] organizations = request.getParameterValues("organizationId");    
        
        if(organizations != null){//hero does not need to be part of organization
            List<Organization> orgs = new ArrayList<>();
            for(String organizationId: organizations){
                orgs.add(organizationDao.getOrganizationById(Integer.parseInt(organizationId)));
            }
            hero.setOrganizations(orgs);
        }
        heroDao.updateHero(hero);
        
        return "redirect:/heros";
    }
    
    @GetMapping("deleteHero")
    public String deleteHero(Integer id) {
        heroDao.deleteHero(id);
        return "redirect:/heros";
    }
    
}
