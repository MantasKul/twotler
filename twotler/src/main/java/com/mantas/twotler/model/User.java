package com.mantas.twotler.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

// These querys are loaded with the app instead of everytime they're used
@NamedQuery(name = "User.findByEmailId", query = "SELECT u from User u where u.email=:email")
@NamedQuery(name = "User.getAllUsers", query = "SELECT new com.mantas.twotler.wrapper.UserWrapper(u.id, u.name, u.email, u.role, u.status) from User u WHERE u.role='user'")
@NamedQuery(name = "User.updateStatus", query = "UPDATE User u SET u.status=:status WHERE u.id=:id")
@NamedQuery(name = "User.getAllAdmin", query = "SELECT u.email from User u WHERE u.role='admin'")

@Data // lombok, takes care of getters/setters, default constructor
@Entity
@DynamicInsert
@DynamicUpdate
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String password;
    // private String displayName; // might implement in the future
    // One could be used as verified/non-verified acc, atm they're used for learning purposes
    private String role;    // User/admin
    private String status;  // Active/not active (true/false)

}
