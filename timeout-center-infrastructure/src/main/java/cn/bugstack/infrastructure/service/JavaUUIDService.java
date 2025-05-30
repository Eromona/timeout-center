package cn.bugstack.infrastructure.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JavaUUIDService implements IUUIDService {

    @Override
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
