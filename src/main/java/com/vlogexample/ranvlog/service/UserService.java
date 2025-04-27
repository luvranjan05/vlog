
package com.vlogexample.ranvlog.service;

import com.vlogexample.ranvlog.entity.Users;
import com.vlogexample.ranvlog.repository.UsersRepo;
import com.vlogexample.ranvlog.requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UsersRepo usersRepo;

    // Method for adding a new user
    public Users addUser(Users user) {
        // Check if the email already exists in the database
        if (usersRepo.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Save the user to the repository with the plain text password
        return usersRepo.save(user);
    }

    // Method for logging in the user
    public Boolean loginUser(LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String password = loginRequest.getPassword();

        // Log the incoming request data
        System.out.println("UserId: " + userId);
        System.out.println("Password: " + password);

        // Check if the provided userId exists in the database
        Optional<Users> user = usersRepo.findById(userId);

        // If user is not found, return false
        if (!user.isPresent()) {
            System.out.println("User not found in the database");
            return false;
        }


        
        Users user1 = user.get();

        // Check if the password matches
        if (!user1.getPassword().equals(password)) {
            System.out.println("Incorrect password");
            return false;
        }

        // If both userId and password are correct, return true
        System.out.println("Login successful");
        return true;
    }

}
