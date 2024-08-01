package com.example.demo.service.generic;

import java.util.List;

import com.example.demo.model.User;

public interface GenericService<Entity, Key> {
    public List<Entity> get();
    public Entity get(Key id);
    public Boolean save(Entity entity);
    public Boolean delete(Key id);
    public User authenticate(String username, String password);
}
