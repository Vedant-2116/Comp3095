package ca.gbc.userservice.repository;

import ca.gbc.userservice.model.User;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String>{
    @DeleteQuery
    void deleteById(String userId);
}
