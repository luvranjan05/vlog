
package com.vlogexample.ranvlog.repository;

import com.vlogexample.ranvlog.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<Users,String> {


    Users findByEmail(String email);
}



