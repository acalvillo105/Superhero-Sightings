/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.dao.OrganizationDaoDB.OrganizationMapper;
import com.sg.superheros.dao.SuperpowerDaoDB.SuperpowerMapper;
import com.sg.superheros.entities.Hero;
import com.sg.superheros.entities.Organization;
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
public class HeroDaoDB implements HeroDao{

    @Autowired
    JdbcTemplate jdbc;
    
    @Override
    public Hero getHeroById(int id) {
        try{
            final String GET_HERO_BY_ID = "SELECT * FROM hero WHERE id = ?";
            Hero hero = jdbc.queryForObject(GET_HERO_BY_ID, new HeroMapper(), id); //id, name, description, isHero
            hero.setSuperpower(getSuperpower(id));
            hero.setOrganizations(getOrganizations(id));    
            return hero;
        }catch(DataAccessException ex){
            return null;
        }
    }
    
    private Superpower getSuperpower(int id){
        final String GET_SUPERPOWER = "SELECT s.* FROM superpower s "
                + "JOIN hero h ON h.superpowerId = s.id WHERE h.id = ?";
        return jdbc.queryForObject(GET_SUPERPOWER, new SuperpowerMapper(), id);
    }

    private List<Organization> getOrganizations(int id){
        final String GET_ORGANIZATIONS = "SELECT o.* FROM organization o "
                + "JOIN membership m ON m.organizationId = o.id WHERE m.heroId = ?";
        return jdbc.query(GET_ORGANIZATIONS, new OrganizationMapper(), id);
    }
    
    @Override
    public List<Hero> getAllHeros() {
        final String GET_ALL_HEROS = "SELECT * FROM hero";
        List<Hero> heros = jdbc.query(GET_ALL_HEROS, new HeroMapper());
        associateSuperpowerAndOrganizations(heros);
        return heros;
    }
    
    private void associateSuperpowerAndOrganizations(List<Hero> heros) {//adding the Organization and Superpower
        for(Hero hero: heros){
            hero.setSuperpower(getSuperpower(hero.getId()));
            hero.setOrganizations(getOrganizations(hero.getId()));
        }
    }

    @Override
    @Transactional
    public Hero addHero(Hero hero) {//when adding a course, we need to add superpower and add membership (if any) 
        final String INSERT_HERO = "INSERT INTO hero(name, description, isHero, superpowerId) "
                + "VALUES(?,?,?,?)";
        jdbc.update(INSERT_HERO, 
                hero.getName(),
                hero.getDescription(),
                hero.isIsHero(),
                hero.getSuperpower().getId());
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        hero.setId(newId);
        insertHeroOrganization(hero);  
        return hero; 
    }
    
    private void insertHeroOrganization(Hero hero) {
        if (hero.getOrganizations() != null){
            final String INSERT_HERO_ORGANIZATIONS = "INSERT INTO "
                    + "membership(heroId, organizationId) VALUES(?,?)";
            for(Organization organization: hero.getOrganizations()){
                jdbc.update(INSERT_HERO_ORGANIZATIONS, 
                        hero.getId(), 
                        organization.getId());
            }
        }
        
    }

    @Override
    @Transactional 
    public void updateHero(Hero hero) {
        final String UPDATE_HERO = "UPDATE hero SET name = ?, description = ?, "
                + "isHero = ?, superpowerId = ? WHERE id = ?";
        jdbc.update(UPDATE_HERO, 
                hero.getName(),
                hero.getDescription(),
                hero.isIsHero(),
                hero.getSuperpower().getId(),
                hero.getId());
        final String DELETE_HERO_ORGANIZATION = "DELETE FROM membership WHERE heroId = ?";
        jdbc.update(DELETE_HERO_ORGANIZATION, hero.getId()); //delete old organizations
        insertHeroOrganization(hero);//add organizations with updated versions
         
    }

    @Override
    @Transactional
    public void deleteHero(int id) {
        // should I leave the sightings? Do we assume he was forgotten by everyone?
        
        // delete hero_sightings
        final String DELETE_HERO_SIGHTINGS = "DELETE FROM hero_sightings WHERE heroId = ?";
        jdbc.update(DELETE_HERO_SIGHTINGS,id);
        
        //delete membership the hero belongs to
        final String DELETE_HERO_ORGANIZATION = "DELETE FROM membership WHERE heroId = ?"; //delete all memberships first
        jdbc.update(DELETE_HERO_ORGANIZATION, id);
        
        final String DELETE_HERO = "DELETE FROM hero WHERE id = ?";
        jdbc.update(DELETE_HERO, id);
    }


    //mapper here to convert data into objects (:
    
    public static final class HeroMapper implements RowMapper<Hero> {

        @Override
        public Hero mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hero hero = new Hero();
            hero.setId(rs.getInt("id"));
            hero.setName(rs.getString("name"));
            hero.setDescription(rs.getString("description"));
            hero.setIsHero(rs.getBoolean("isHero"));            
            
            //need superpower + organizations
            return hero;
        }
        
    }
}
