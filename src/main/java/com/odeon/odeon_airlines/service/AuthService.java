package com.odeon.odeon_airlines.service;

import com.odeon.odeon_airlines.exception.UsernameAlreadyExistsException;
import com.odeon.odeon_airlines.model.AppUser;
import com.odeon.odeon_airlines.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AppUser register(AppUser user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public AppUser login(String username, String password) {
        AppUser user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    /*
    // Admin kullanıcısı oluşturma metodu. Bu methodun bu bu şekilde oluşturulmasının sebebi başka bir localhost'da proje çalıştırılmak istediğin zaman kullanıcı admini kendi eliyle girmek isteyebilir . MySQL den. Ya da burada da yapabilir.
    public AppUser createAdminUser() {
        AppUser adminUser = new AppUser();
        adminUser.setName("Nan");
        adminUser.setSurname("Nan");
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin")); // Şifreyi hashle
        adminUser.setRole("ADMIN"); // Rolü ADMIN olarak ayarla
        return userRepository.save(adminUser); // Kullanıcıyı kaydet
    }

    // Uygulama başlatıldığında admin kullanıcıyı oluşturma
    @PostConstruct
    public void init() {
        if (userRepository.findByUsername("admin") == null) {
            createAdminUser(); // Admin kullanıcı yoksa oluştur
        }
    }*/
}
