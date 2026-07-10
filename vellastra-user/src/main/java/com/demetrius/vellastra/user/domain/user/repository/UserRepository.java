package com.demetrius.vellastra.user.domain.user.repository;

import com.demetrius.vellastra.user.domain.user.entity.User;

public interface UserRepository {

    User findById(Long id);

    void save(User user);
}
