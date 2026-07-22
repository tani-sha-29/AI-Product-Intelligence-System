package ecommerceai.service;

import ecommerceai.entity.User;

public interface IUserService {
    String addUser(User user);
    String login(User user);
    String logout(User user);
    String setProfile(User user);
    String updateProfile(User user);

}
