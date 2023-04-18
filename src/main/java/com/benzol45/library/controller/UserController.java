package com.benzol45.library.controller;

import com.benzol45.library.entity.User;
import com.benzol45.library.entity.validator.UserUniqValidator;
import com.benzol45.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserUniqValidator userUniqValidator;

    @Autowired
    public UserController(UserService userService, UserUniqValidator userUniqValidator) {
        this.userService = userService;
        this.userUniqValidator = userUniqValidator;
    }

    @GetMapping("/new")
    public String getNewUserPage(@ModelAttribute User user) {
        return "NewUser";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute @Valid User user, Errors errors) {
        userUniqValidator.validate(user, errors);
        if (errors.hasErrors()) {
            return "NewUser";
        }

        userService.saveNewUser(user);

        return "redirect:/";
    }

    @GetMapping("/admin")
    public String getAdminUserPage(Model model) {
        model.addAttribute("users_without_role", userService.getListWithoutRole());
        model.addAttribute("users_blocked", userService.getListBlocked());
        model.addAttribute("users_working", userService.getListWorking());

        return "UserAdministration";
    }

    @GetMapping("/{id}/set/{role}")
    public String setUserRole(@PathVariable("id") Long id, @PathVariable("role") String roleString) {
        User.Role role = User.Role.valueOf(roleString.toUpperCase());
        if (role!=null) {
            userService.setUserRole(id,role);
            userService.unblock(id);
        }

        return ("redirect:/user/admin");
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);

        return ("redirect:/user/admin");
    }

    @GetMapping("/{id}/unblock")
    public String unblockUser(@PathVariable("id") Long id) {
        userService.unblock(id);

        return ("redirect:/user/admin");
    }

    @GetMapping("/{id}/block")
    public String blockUser(@PathVariable("id") Long id) {
        userService.block(id);

        return ("redirect:/user/admin");
    }


}
