package com.springboot.blog;

import com.springboot.blog.model.Role;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.utils.constants.UserRoles;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Blog Api by Ahsan Azeem",
				description = "Spring Boot Blog Api documentation",
				version = "1.0",
				contact = @Contact(
						name = "Ahsan Azeem",
						email = "ahsan.btph123@gmail.com"
				)
		)
)
public class SpringbootBlogRestApiApplication implements CommandLineRunner {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootBlogRestApiApplication.class, args);
	}

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
		addRole(UserRoles.ROLE_INDEX + UserRoles.ADMIN);
		addRole(UserRoles.ROLE_INDEX + UserRoles.USER);
	}

	private void addRole(String roleName) {
		Role role = new Role();
		role.setName(roleName);

		roleRepository.save(role);
	}
}
