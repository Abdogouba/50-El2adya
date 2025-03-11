package com.example.service;


import com.example.repository.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public abstract class MainService<T> {

    protected final MainRepository<T> mainRepository;

    @Autowired
    public MainService(MainRepository<T> mainRepository) {
        this.mainRepository = mainRepository;
    }
}