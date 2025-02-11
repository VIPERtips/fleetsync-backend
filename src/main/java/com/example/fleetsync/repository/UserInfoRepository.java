package com.example.fleetsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleetsync.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

}
