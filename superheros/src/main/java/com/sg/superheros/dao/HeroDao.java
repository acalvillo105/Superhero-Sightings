/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.entities.Hero;
import java.util.List;

/**
 *
 * @author acalvillo
 */
public interface HeroDao {
    //CRUD methods here
    Hero getHeroById(int id);
    List<Hero> getAllHeros();
    Hero addHero(Hero hero);
    
    void updateHero(Hero hero);
    void deleteHero(int id);
    
    //
    
}
