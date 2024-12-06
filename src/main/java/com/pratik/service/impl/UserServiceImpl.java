package com.pratik.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pratik.dto.EmailRequest;
import com.pratik.dto.UserDto;
import com.pratik.entity.Role;
import com.pratik.entity.User;
import com.pratik.repository.RoleRepository;
import com.pratik.repository.UserRepository;
import com.pratik.service.UserService;
import com.pratik.util.Validation;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private Validation validation;
	
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private EmailService emailService;
		
	@Override
	public Boolean register(UserDto userDto) throws Exception {
		
		validation.userValidation(userDto);
		User user= mapper.map(userDto, User.class);
		setRole(userDto,user);
		
		User saveUser= userRepo.save(user);
		if(!ObjectUtils.isEmpty(saveUser)) {
			
			//Send  Email
			emailSend(saveUser);
			return true;
		}
		 	
		return false;
	}

	private void emailSend(User saveUser) throws Exception {
		
		String message = "Hi, <b>"+saveUser.getFirstName()+"</b>"
		+"<br>Your account registration successfully.<br>"
		+"<br>Click the below link to verify & active your account<br>"
		+"<a href='#'>Click Here</a> <br><br>"
		+"Thanks,<br> <b>Enotes.com</b>";
		
		EmailRequest emailRequest=EmailRequest.builder()
				.to(saveUser.getEmail())
				.title("Account Creation Confirmation")
				.subject("Account Created Success")
				.message(message)
				.build();
		
		emailService.sendEmail(emailRequest);
		
	}

	private void setRole(UserDto userDto, User user) {
		List<Integer> reqRoleId = userDto.getRoles().stream().map(r->r.getId()).toList();
		List<Role> roles = roleRepo.findAllById(reqRoleId);
		user.setRoles(roles);
	}

	
}
