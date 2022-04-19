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
import com.sg.superheros.entities.Sightings;
import java.util.ArrayList;
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
public class SightingsController {
    @Autowired
    HeroDao heroDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    OrganizationDao organizationDao;
    
    @Autowired
    SightingsDao sightingsDao;
    
    @Autowired
    SuperpowerDao supoerpowerDao;
    
    @GetMapping("sightings")
    public String displaySightings(Model model) {
        List<Sightings> sightings = sightingsDao.getAllSightings();
        List<Location> locations = locationDao.getAllLocations();
        List<Hero> heros = heroDao.getAllHeros();
        model.addAttribute("sightings", sightings);
        model.addAttribute("locations", locations);
        model.addAttribute("heros", heros);
        return "sightings";
    }
    
    @PostMapping("addSighting")
    public String addSighting(Sightings sight, HttpServletRequest request) {
        String locationId = request.getParameter("locationId");
        sight.setLocation(locationDao.getLocationById(Integer.parseInt(locationId)));
        String[] heroIds = request.getParameterValues("heroId");        
        
        if(heroIds != null) {
            List<Hero> heros = new ArrayList<>();
            for(String heroId : heroIds) {
                heros.add(heroDao.getHeroById(Integer.parseInt(heroId)));
            }
            sight.setHeros(heros);
        }
        
        sightingsDao.addSightings(sight);
        return "redirect:/sightings";
    }
    
    @GetMapping("sightingDetail")
    public String sightingDetail(Integer id, Model model) {
        Sightings sighting = sightingsDao.getSightingsById(id);
        model.addAttribute("sighting", sighting);
   
        return "sightingDetail";
    }
    
    @GetMapping("deleteSighting")
    public String deleteSighting(Integer id) {
        sightingsDao.deleteSightingsById(id);
        return "redirect:/sightings";
    }
    
    @GetMapping("editSighting")
    public String editSighting(Integer id, Model model) {
        Sightings sighting = sightingsDao.getSightingsById(id);
        List<Location> locations = locationDao.getAllLocations();
        List<Hero> heros = heroDao.getAllHeros();
        model.addAttribute("sighting", sighting);
        model.addAttribute("locations", locations);
        model.addAttribute("heros", heros);
        return "editSighting";
    }
    
    @PostMapping("editSighting")
    public String performEditSighting(Sightings sight, HttpServletRequest request) {
        String locationId = request.getParameter("locationId");
        sight.setLocation(locationDao.getLocationById(Integer.parseInt(locationId)));
        String[] heroIds = request.getParameterValues("heroId");        
        
        if(heroIds != null) {
            List<Hero> heros = new ArrayList<>();
            for(String heroId : heroIds) {
                heros.add(heroDao.getHeroById(Integer.parseInt(heroId)));
            }
            sight.setHeros(heros);
        }
        
        sightingsDao.updateSighting(sight);
        
        return "redirect:/sightings";
    }
}
