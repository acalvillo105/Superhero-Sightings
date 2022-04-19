/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.entities.Location;
import java.util.List;

/**
 *
 * @author acalvillo
 */
public interface LocationDao {
    //CRUD methods here
    Location getLocationById(int id);
    List<Location> getAllLocations();
    Location addLocation(Location location);
    
    void updateLocation(Location location);
    void deleteLocationById(int id);
}
