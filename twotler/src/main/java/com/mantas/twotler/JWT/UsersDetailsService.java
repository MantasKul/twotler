package com.mantas.twotler.JWT;

import com.mantas.twotler.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class UsersDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private com.mantas.twotler.model.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        userDetail = userDao.findByEmailId(email);
        if(!Objects.isNull(userDetail)) {
            return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        }
        else throw new UsernameNotFoundException("User not found");
    }

    public com.mantas.twotler.model.User getUserDetail() {
        return userDetail;
    }
}
