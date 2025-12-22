package org.example.storeapplication.mappers;

import org.example.storeapplication.entities.User;
import org.example.storeapplication.models.UserDTO;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDTO userToUserDto(User user);

}
