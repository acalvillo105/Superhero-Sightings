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
import com.sg.superheros.entities.Location;
import com.sg.superheros.entities.Organization;
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
public class OrganizationController {
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
    
    @GetMapping("organizations")
    public String displayOrganizations(Model model) {
        List<Organization> orgs = organizationDao.getAllOrganizations();
        List<Location> locations = locationDao.getAllLocations();
        model.addAttribute("organizations", orgs);
        model.addAttribute("locations", locations);
        
        try{
            List<Hero> heros = heroDao.getAllHeros();
            model.addAttribute("heros", heros);
        }catch(DataAccessException ex){
            
        }
        return "organizations";
    }
    
    @PostMapping("addOrganization")
    public String addOrganization(Organization organization, HttpServletRequest request) {
        String locationId = request.getParameter("locationId");
        organization.setLocation(locationDao.getLocationById(Integer.parseInt(locationId)));
        String[] heroIds = request.getParameterValues("heroId");       
             
        if(heroIds != null) {
            List<Hero> heros = new ArrayList<>();
            for(String heroId : heroIds) {
                heros.add(heroDao.getHeroById(Integer.parseInt(heroId)));
            }
            organization.setHeros(heros);
        }
        organizationDao.addOrganization(organization);
        return "redirect:/organizations";
    }
    
    @GetMapping("organizationDetail")
    public String organizationDetail(Integer id, Model model) {
        Organization organization = organizationDao.getOrganizationById(id);
        model.addAttribute("organization", organization);
        return "organizationDetail";
    }
    
    @GetMapping("deleteOrganization")
    public String deleteCourse(Integer id) {
        organizationDao.deleteOrganizationById(id);
        return "redirect:/organizations";
    }
    
    @GetMapping("editOrganization")
    public String editOrganization(Integer id, Model model) {
        Organization organization = organizationDao.getOrganizationById(id);
        List<Location> locations = locationDao.getAllLocations();
        List<Hero> heros = heroDao.getAllHeros();
        model.addAttribute("organization", organization);
        model.addAttribute("locations", locations);
        model.addAttribute("heros", heros);
        return "editOrganization";
    }
    
    @PostMapping("editOrganization")
    public String performEditOrganization(Organization organization, HttpServletRequest request) {
        String locationId = request.getParameter("locationId");
        organization.setLocation(locationDao.getLocationById(Integer.parseInt(locationId)));
        String[] heroIds = request.getParameterValues("heroId");       
             
        if(heroIds != null) {
            List<Hero> heros = new ArrayList<>();
            for(String heroId : heroIds) {
                heros.add(heroDao.getHeroById(Integer.parseInt(heroId)));
            }
            organization.setHeros(heros);
        }
        organizationDao.updateOrganization(organization);
        return "redirect:/organizations";
    }
    
}
