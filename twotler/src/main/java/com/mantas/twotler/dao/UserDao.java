package com.mantas.twotler.dao;

import com.mantas.twotler.model.User;
import com.mantas.twotler.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);
    List<UserWrapper> getAllUsers();
    List<String> getAllAdmin();
    @Transactional
    @Modifying
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

    // Behind the scenes, Data JPA will create SQL queries based on the finder method and execute the query for us thus it's not needed to be manually implemented
    User findByEmail(String email);
}
