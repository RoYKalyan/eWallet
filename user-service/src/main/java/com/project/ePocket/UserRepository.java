package com.project.ePocket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> { //Datatype of the primary key is Integer

    User findByUserId(String userId);

}
