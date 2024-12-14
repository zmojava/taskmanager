package com.zeyt.springboot.taskmanager.repository;


import com.zeyt.springboot.taskmanager.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}