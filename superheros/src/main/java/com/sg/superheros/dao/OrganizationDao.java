/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sg.superheros.dao;

import com.sg.superheros.entities.Organization;
import java.util.List;

/**
 *
 * @author acalvillo
 */
public interface OrganizationDao {
    //CRUD methods 
    Organization getOrganizationById(int id);
    List<Organization> getAllOrganizations();
    Organization addOrganization(Organization organization);
    
    void updateOrganization(Organization organization);
    void deleteOrganizationById(int id);
}
