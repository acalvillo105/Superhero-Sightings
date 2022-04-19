/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.entities.Hero;
import com.sg.superheros.entities.Location;
import com.sg.superheros.entities.Sightings;
import com.sg.superheros.dao.LocationDaoDB.LocationMapper;
import com.sg.superheros.dao.HeroDaoDB.HeroMapper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class SightingsDaoDB implements SightingsDao{
    
    @Autowired
    JdbcTemplate jdbc;

    @Override
    public Sightings getSightingsById(int id) {
        try{
            final String GET_SIGHTINGS_BY_ID = "SELECT * FROM sightings WHERE id = ?";
            Sightings sight =  jdbc.queryForObject(GET_SIGHTINGS_BY_ID, new SightingsMapper(), id);
            sight.setLocation(getLocationForSightings(id));
            sight.setHeros(getHeros(id));
            return sight;
            
        }catch(DataAccessException ex){
            return null; //id does not exist
        }
    }
    
    private Location getLocationForSightings(int id) {
        final String GET_LOCATION_FOR_SIGHTING = "SELECT l.* FROM location l "
                + "JOIN sightings s ON s.locationId = l.id WHERE s.id = ?";
        return jdbc.queryForObject(GET_LOCATION_FOR_SIGHTING, new LocationMapper(), id);
    }

    private List<Hero> getHeros(int id) {
        final String GET_HEROS_FOR_SIGHTINGS = "SELECT h.* FROM hero h "
                + "JOIN hero_sightings hs ON hs.heroId = h.id WHERE hs.sightingId = ?" ;
        return jdbc.query(GET_HEROS_FOR_SIGHTINGS, new HeroMapper(), id);
    }

    @Override
    public List<Sightings> getAllSightings() {
        final String GET_ALL_SIGHTINGS = "SELECT * FROM sightings";
        List<Sightings> sightings = jdbc.query(GET_ALL_SIGHTINGS, new SightingsMapper());
        associateLocationAndHeros(sightings); //will set Heros and Locations
        return sightings;
    }
    
    private void associateLocationAndHeros(List<Sightings> sightings) {
        for(Sightings sight : sightings){
            sight.setLocation(getLocationForSightings(sight.getId()));
            sight.setHeros(getHeros(sight.getId()));
        }
    }

    @Override
    @Transactional
    public Sightings addSightings(Sightings sighting) { //CHECK THIS AGAIN  
        final String INSERT_SIGHTING = "INSERT INTO sightings(locationId, date, description) "
                + "VALUES(?,?,?)";
        jdbc.update(INSERT_SIGHTING, 
                sighting.getLocation().getId(), 
                sighting.getTime(),
                sighting.getDescription());
        
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        sighting.setId(newId);
        insertHeros(sighting);  
        return sighting;
    }
    private void insertHeros(Sightings sighting) {
        //When we create a new sighting, we need to add a list of the heros in that sighting. 
        final String INSERT_HEROS_SIGHTINGS = "INSERT INTO hero_sightings(heroId, sightingId) "
                + "VALUES(?,?)";
        for(Hero hero: sighting.getHeros()){
            jdbc.update(INSERT_HEROS_SIGHTINGS, 
                    hero.getId(), 
                    sighting.getId());
        }
    }

    @Override
    @Transactional
    public void updateSighting(Sightings sighting) {
        final String UPDATE_SIGHTING = "UPDATE sightings SET locationId = ?, "
                + "date = ?, description = ? WHERE id = ?";
        jdbc.update(UPDATE_SIGHTING, 
                sighting.getLocation().getId(),
                sighting.getTime(),
                sighting.getDescription(),
                sighting.getId());
        
        final String DELETE_HERO_SIGHTINGS = "DELETE FROM hero_sightings WHERE sightingId = ?";
        jdbc.update(DELETE_HERO_SIGHTINGS, sighting.getId());
        insertHeros(sighting);        
    }

    @Override
    @Transactional
    public void deleteSightingsById(int id) {
        final String DELETE_HERO_SIGHTINGS = "DELETE FROM hero_sightings WHERE sightingId = ?";
        jdbc.update(DELETE_HERO_SIGHTINGS, id);
        
        final String DELETE_SIGHTING = "DELETE FROM sightings WHERE id = ?";
        jdbc.update(DELETE_SIGHTING, id);
    }
    
    //mapper here to convert data into objects (:
    public static final class SightingsMapper implements RowMapper<Sightings> {

        @Override
        public Sightings mapRow(ResultSet rs, int rowNum) throws SQLException {
            Sightings sight = new Sightings();
            sight.setId(rs.getInt("id"));
            sight.setTime(rs.getDate("date"));
            sight.setDescription((rs.getString("description")));
            
            return sight;
        }
        
    }
}
