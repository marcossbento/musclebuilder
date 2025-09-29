package com.musclebuilder.service.security;

import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.model.User;
import com.musclebuilder.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {

    private final UserRepository userRepository;

    public SecurityContextService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return  userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado com o email: " + username));
    }

}
