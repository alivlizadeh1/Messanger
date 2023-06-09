package com.av.user.service;

import com.av.user.entity.User;
import com.av.user.exception.User.UserNotFoundException;
import com.av.user.exception.User.UserPhoneNumberDuplicatedException;
import com.av.user.repository.UserRepository;
import com.av.user.request.UserInsertRequest;
import com.av.user.request.UserUpdateRequest;
import com.av.user.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    //private final RestTemplate restTemplate;

    @Autowired
    // DI
    public UserServiceImpl(UserRepository userRepository/*, RestTemplate restTemplate*/) {
        /* ioc */ this.userRepository = userRepository;
        //this.restTemplate = restTemplate;
    }

    @Override
    public UserResponse insert(UserInsertRequest request) throws UserPhoneNumberDuplicatedException {
        try {
            findByPhoneNumber(request.phoneNumber());
            throw new UserPhoneNumberDuplicatedException("User with phone number '" + request.phoneNumber() + "' founded.");
        } catch (UserNotFoundException ignored){}
        User user = User.builder().
                firstname(request.firstName()).
                lastname(request.lastName()).
                phoneNumber(request.phoneNumber()).
                build();
        userRepository.save(user);
        return new UserResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getBio(),
                user.getPhoneNumber(),
                user.getProfilePicture()
        );
    }

    @Override
    public UserResponse findById(Long id) throws UserNotFoundException{
        User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException("User not found with id : '" + id + "'"));
        return new UserResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getBio(),
                user.getPhoneNumber(),
                user.getProfilePicture());
    }

    @Override
    public UserResponse findByPhoneNumber(String phoneNumber) throws UserNotFoundException{
        User user = userRepository.findByPhoneNumber(phoneNumber).
                orElseThrow(() -> new UserNotFoundException("User not found with phone number : '" + phoneNumber + "'"));
        return new UserResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getBio(),
                user.getPhoneNumber(),
                user.getProfilePicture()
        );
    }

    @Override
    public UserResponse findByUserName(String username) throws UserNotFoundException{
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new UserNotFoundException("User not found with username : '" + username + "'"));
        return new UserResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getBio(),
                user.getPhoneNumber(),
                user.getProfilePicture()
        );
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    @Override
    public List<UserResponse> findAllBySorting() {
        List<User> users = userRepository.findAll(Sort.by(/* messageRepository.save(message); */"id"));
        return users.stream().map(this::convertToResponse).toList();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteByPhoneNumber(String phoneNumber) {
        userRepository.deleteByPhoneNumber(phoneNumber);
    }

    @Override
    public Long updateById(UserUpdateRequest request) {
        return null;
    }

    @Override
    public Long updateByPhoneNumber(UserUpdateRequest request) {
        return null;
    }

    private UserResponse convertToResponse(User user){
        return new UserResponse(user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getBio(),
                user.getPhoneNumber(),
                user.getProfilePicture());
    }
}
