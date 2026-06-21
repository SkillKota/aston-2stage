package homework2.assembler;

import homework2.controller.UserController;
import homework2.dto.UserResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler {
    public EntityModel<UserResponseDto> toModel(UserResponseDto user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).findUserById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).findAllUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).updateUser(user.id(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(user.id())).withRel("delete"));
    }
}
