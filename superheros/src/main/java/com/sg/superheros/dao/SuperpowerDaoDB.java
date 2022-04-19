/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.entities.Superpower;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author acalvillo
 */
@Repository
public class SuperpowerDaoDB implements SuperpowerDao{

    @Autowired
    JdbcTemplate jdbc;
    
    @Override
    public Superpower getSuperPowerById(int id) {
        try {
            final String GET_SUPERPOWERS_BY_ID = "SELECT * FROM superpower WHERE id = ?";
            return jdbc.queryForObject(GET_SUPERPOWERS_BY_ID, new SuperpowerMapper(), id);
        }catch(DataAccessException ex){
            return null; //id does not exist
        }
    }

    @Override
    public List<Superpower> getAllSuperpowers() {
        final String GET_ALL_SUPERPOWERS = "SELECT * FROM superpower";
        return jdbc.query(GET_ALL_SUPERPOWERS, new SuperpowerMapper());
    }

    @Override
    @Transactional
    public Superpower addSuperpower(Superpower superpower) {
        final String ADD_SUPERPOWER = "INSERT INTO superpower(name) VALUES (?)";
        jdbc.update(ADD_SUPERPOWER, superpower.getPower());
        
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        superpower.setId(newId);
        return superpower;        
    }

    @Override
    public void updateSuperpower(Superpower superpower) {
        final String UPDATE_SUPERPOWER = "UPDATE superpower SET name = ? WHERE id = ?";
        jdbc.update(UPDATE_SUPERPOWER, 
                superpower.getPower(), 
                superpower.getId());
    }

    @Override
    @Transactional
    public void deleteSuperpowerById(int id) { 
        //delete membership that the hero belongs to 
        final String DELETE_MEMBERSHIP = "DELETE m.* FROM membership m "
                + "JOIN hero h ON m.heroId WHERE h.superpowerId = ?";
        jdbc.update(DELETE_MEMBERSHIP, id);
        
        //delete sightings
        final String DELETE_HERO_SIGHTINGS = "DELETE hs.* FROM hero_sightings hs "
                + "JOIN hero h ON hs.heroId WHERE h.superpowerId = ?";
        jdbc.update(DELETE_HERO_SIGHTINGS, id);
        
        //delete hero
        final String DELETE_HERO = "DELETE FROM hero WHERE superpowerId = ?"; //If superpower is gone, then the villan/hero is no longer considered "SUPER"
        jdbc.update(DELETE_HERO, id);
        
        //delete power
        final String DELETE_SUPERPOWER = "DELETE FROM superpower WHERE id  = ?";
        jdbc.update(DELETE_SUPERPOWER, id);        
    }
    
    //mapper here to convert data into objects (:
    
    public static final class SuperpowerMapper implements RowMapper<Superpower> {
        @Override
        public Superpower mapRow(ResultSet rs, int index) throws SQLException {
            Superpower superpower = new Superpower();
            superpower.setId(rs.getInt("id"));
            superpower.setPower(rs.getString("name")); //name of superpower
            
            return superpower; //returns an object from database based on id
        }
        
    }
}
