/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.entities.Sightings;
import java.util.List;

/**
 *
 * @author acalvillo
 */
public interface SightingsDao {
    Sightings getSightingsById(int id);
    List<Sightings> getAllSightings();
    Sightings addSightings(Sightings sighting);
    
    void updateSighting(Sightings sighting);
    void deleteSightingsById(int id);
    
}
