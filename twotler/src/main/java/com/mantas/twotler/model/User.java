package com.mantas.twotler.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "User.findByEmailId", query = "SELECT u from User u where u.email=:email")

@Data // lombok, takes care of getters/setters. default constructor
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
    // These two variables will be removed in the future if not needed
    private String role;
    private String status;


}
