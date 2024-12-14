package com.zeyt.springboot.taskmanager.repository;


import com.zeyt.springboot.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
