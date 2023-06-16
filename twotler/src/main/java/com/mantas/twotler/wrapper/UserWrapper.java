package com.mantas.twotler.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Takes care of getters/setters
@NoArgsConstructor // Takes cares of empty constructor
public class UserWrapper {

    private Integer id;
    private String name;
    private String email;
    private String role;
    private String status;

    public UserWrapper(Integer id, String name, String email, String role, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
    }
}
