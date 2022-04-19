/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.entities.Location;
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
public class LocationDaoDB implements LocationDao{
    
    @Autowired 
    JdbcTemplate jdbc;

    @Override
    public Location getLocationById(int id) {
        try{
            final String GET_LOCATION_BY_ID = "SELECT * FROM location WHERE id = ?";
            return jdbc.queryForObject(GET_LOCATION_BY_ID, new LocationMapper(), id);
        }catch(DataAccessException ex){
            return null;
        }
    }

    @Override
    public List<Location> getAllLocations() {
        final String GET_ALL_LOCATIONS = "SELECT * FROM location";
        return jdbc.query(GET_ALL_LOCATIONS, new LocationMapper());
    }

    @Override
    @Transactional
    public Location addLocation(Location location) {
        final String INSERT_LOCATION = "INSERT INTO location(name, description, address,"
                + "longitude, latitude) VALUES(?,?,?,?,?)";
        jdbc.update(INSERT_LOCATION, 
                location.getName(),
                location.getDescription(),
                location.getAddress(),
                location.getLongitude(),
                location.getLatitude());
        
        int newInt = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        location.setId(newInt);
        
        return location;        
    }

    @Override
    public void updateLocation(Location location) {
        final String UPDATE_LOCATION = "UPDATE location SET name = ?, "
                + "description = ?, address = ?, longitude = ?, latitude = ? WHERE id = ?";
        jdbc.update(UPDATE_LOCATION,
                    location.getName(),
                    location.getDescription(),
                    location.getAddress(),
                    location.getLongitude(),
                    location.getLatitude(),
                    location.getId());
    }

    @Override
    @Transactional
    public void deleteLocationById(int id) { 
        // should I leave the sightings (assuming location was destroyed)?
        // should I leave the organizations? (they just don't have anywhere to meet?
        
        //if location is deleted, then the hero_sightings to that location are deleted as well
        final String DELETE_HERO_SIGHTINGS = "DELETE hs.* FROM hero_sightings hs "
                + "JOIN sightings s ON hs.sightingId WHERE s.locationId = ?";
        jdbc.update(DELETE_HERO_SIGHTINGS, id);        
        
        //sightings to location are deleted
        final String DELETE_SIGHTINGS = "DELETE FROM sightings WHERE locationId = ?";
        jdbc.update(DELETE_SIGHTINGS,id);
        
        //if the location belongs to an organization, then delete the membership first
        final String DELETE_MEMBERSHIP = "DELETE m.* FROM membership m "
                + "JOIN organization o ON m.organizationId WHERE o.locationId = ?";
        jdbc.update(DELETE_MEMBERSHIP, id);
        
        // then the organization
        final String DELETE_ORGANIZATION = "DELETE FROM organization WHERE locationId = ?";
        jdbc.update(DELETE_ORGANIZATION, id);
        
        final String DELETE_LOCATION_BY_ID = "DELETE FROM location WHERE id = ?";
        jdbc.update(DELETE_LOCATION_BY_ID, id);
    }

    //mapper here to convert data into objects (:   

    public static final class LocationMapper implements RowMapper<Location> {

        @Override
        public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
            Location location = new Location();
            location.setId(rs.getInt("id"));
            location.setName(rs.getString("name"));
            location.setDescription(rs.getString("description"));
            location.setAddress(rs.getString("address"));
            location.setLongitude(rs.getString("longitude"));
            location.setLatitude(rs.getString("latitude"));            
            
            return location;
        }
        
    }
    
}
