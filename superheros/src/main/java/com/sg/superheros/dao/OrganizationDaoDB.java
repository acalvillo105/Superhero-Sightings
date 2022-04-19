/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.dao.HeroDaoDB.HeroMapper;
import com.sg.superheros.dao.LocationDaoDB.LocationMapper;
import com.sg.superheros.entities.Hero;
import com.sg.superheros.entities.Location;
import com.sg.superheros.entities.Organization;
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
public class OrganizationDaoDB implements OrganizationDao{
    
    @Autowired 
    JdbcTemplate jdbc;
    

    @Override
    public Organization getOrganizationById(int id) {
        try{
            final String GET_ORGANIZATION_BY_ID = "SELECT * FROM organization WHERE id = ?";
            Organization org = jdbc.queryForObject(GET_ORGANIZATION_BY_ID, new OrganizationMapper(), id);
            org.setLocation(getLocation(id));
            org.setHeros(getHeros(id));
            return org;            
        }catch(DataAccessException ex){
            return null;
        }
    }
    
    private Location getLocation(int id) {
        final String GET_LOCATION_FOR_ORGANIZATION = "SELECT l.* FROM location l "
                + "JOIN organization o ON o.locationId = l.id WHERE o.id = ?";
        return jdbc.queryForObject(GET_LOCATION_FOR_ORGANIZATION, new LocationMapper(), id);      
    }
    
    private List<Hero> getHeros(int id) {
        final String GET_HEROS_FOR_ORGANIZATION = "SELECT h.* FROM hero h "
                + "JOIN membership m ON m.heroId = h.id WHERE m.organizationId = ?";
        return jdbc.query(GET_HEROS_FOR_ORGANIZATION,new HeroMapper(), id);
                
    }

    @Override
    public List<Organization> getAllOrganizations() {
        final String GET_ALL_ORGANIZATIONS = "SELECT * FROM organization";
        List<Organization> orgs = jdbc.query(GET_ALL_ORGANIZATIONS, new OrganizationMapper());
        associateLocationAndHeros(orgs);
        return orgs;
    }
    
    private void associateLocationAndHeros(List<Organization> orgs) {
        for(Organization org: orgs){
            org.setLocation(getLocation(org.getId()));
            org.setHeros(getHeros(org.getId()));
        }
    }

    @Override
    @Transactional
    public Organization addOrganization(Organization organization) {
        final String INSERT_ORGANIZATION = "INSERT INTO organization(name, locationId, description) "
                + "VALUES(?,?,?)";
        jdbc.update(INSERT_ORGANIZATION,
                organization.getName(),
                organization.getLocation().getId(),
                organization.getDescription());
        int newInt = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        organization.setId(newInt);
        insertMembership(organization);
        return organization;
    }
    
    private void insertMembership(Organization organization) {
        final String INSERT_MEMBERSHIP = "INSERT INTO membership(heroId, organizationId) "
                + "VALUES(?,?)";
        for(Hero hero: organization.getHeros()){
            jdbc.update(INSERT_MEMBERSHIP,
            hero.getId(),
            organization.getId());
        }
    }

    @Override
    @Transactional
    public void updateOrganization(Organization organization) {
        final String UPDATE_ORGANIZATION = "UPDATE organization SET name = ?, "
                + "locationId = ?, description = ? WHERE id = ?";
        jdbc.update(UPDATE_ORGANIZATION,
                organization.getName(),
                organization.getLocation().getId(),
                organization.getDescription(),
                organization.getId());
        final String DELETE_MEMBERSHIP = "DELETE FROM membership WHERE organizationId = ?";
        jdbc.update(DELETE_MEMBERSHIP,organization.getId());
        insertMembership(organization);
    }

    @Override
    @Transactional
    public void deleteOrganizationById(int id) {
            final String DELETE_MEMBERSHIP = "DELETE FROM membership WHERE organizationId = ?";
            jdbc.update(DELETE_MEMBERSHIP, id);
            
            final String DELETE_ORGANIZATION = "DELETE FROM organization WHERE id = ?";
            jdbc.update(DELETE_ORGANIZATION, id);            
    }
    
    //mapper here to convert data into objects (:
    public static final class OrganizationMapper implements RowMapper<Organization> {

        @Override
        public Organization mapRow(ResultSet rs, int rowNum) throws SQLException {
            Organization org = new Organization();
            org.setId(rs.getInt("id"));
            org.setName(rs.getString("name"));
            org.setDescription(rs.getString("description"));
            
            return org;
        }
        
    }
}
