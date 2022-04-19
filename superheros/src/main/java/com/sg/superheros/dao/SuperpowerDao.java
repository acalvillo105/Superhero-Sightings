/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.entities.Superpower;
import java.util.List;

/**
 *
 * @author acalvillo
 */
public interface SuperpowerDao {
    Superpower getSuperPowerById(int id);
    List<Superpower> getAllSuperpowers();
    Superpower addSuperpower(Superpower supoerpower);
    
    void updateSuperpower(Superpower superpower);
    void deleteSuperpowerById(int id);
    
    
}
