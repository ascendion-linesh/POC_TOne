package com.bookstore;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bookstore.domain.User;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.UserService;

@SpringBootApplication
public class BookstoreApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Adams");
        user1.setUsername("j");
        user1.setPassword(passwordEncoder.encode("p"));
        user1.setEmail("jadams@gmail.com"); // lowercase for consistency

        Set<UserRole> userRoles = new HashSet<>();
        Role role1 = new Role();
        role1.setRoleId(1);
        role1.setName("ROLE_USER");
        userRoles.add(new UserRole(user1, role1));

        userService.createUser(user1, userRoles);


        User user2 = new User();
        user2.setFirstName("Aman");
        user2.setLastName("Jhon");
        user2.setUsername("AJ");
        user2.setPassword(passwordEncoder.encode("JA"));
        user2.setEmail("amanjhon@gmail.com"); // lowercase for consistency

        Set<UserRole> userRoles2 = new HashSet<>();
        Role role2 = new Role();
        role1.setRoleId(2);
        role1.setName("ROLE_USER");
        userRoles.add(new UserRole(user2, role2));

        userService.createUser(user2, userRoles2);

        User user3 = new User();
        user3.setFirstName("ADI");
        user3.setLastName("MANAB");
        user3.setUsername("AM");
        user3.setPassword(passwordEncoder.encode("MA"));
        user3.setEmail("adimanab@gmail.com"); // lowercase for consistency

        Set<UserRole> userRoles3 = new HashSet<>();
        Role role3 = new Role();
        role1.setRoleId(3);
        role1.setName("ROLE_USER");
        userRoles.add(new UserRole(user3, role3));

        userService.createUser(user3, userRoles3);

    }
}
