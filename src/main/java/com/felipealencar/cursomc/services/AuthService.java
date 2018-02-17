package com.felipealencar.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.felipealencar.cursomc.domain.Cliente;
import com.felipealencar.cursomc.repositories.ClienteRepository;
import com.felipealencar.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailService emailService;
	
	private Random random = new Random();
	
	public void sendNewPassword(String email) {
		
		Cliente cliente = this.clienteRepository.findByEmail(email);
		if (cliente == null) {
			throw new ObjectNotFoundException("Email não encontrado");
		}
		
		String newPass = newPassword();
		cliente.setSenha(this.passwordEncoder.encode(newPass));
		
		this.clienteRepository.save(cliente);
		this.emailService.sendNewPasswordEmail(cliente, newPass);
	}

	private String newPassword() {
		char[] vet = new char[10];
		for(int i = 0; i < 10; i++) {
			vet[i] = randomChar();
		}
		return new String(vet);
	}

	private char randomChar() {
		int opt = random.nextInt(3);
		if (opt == 0) { // gera um numero
			return (char) (random.nextInt(10) + 48);
		} 
		else if (opt == 1) { // gera letra maiuscula
			return (char) (random.nextInt(26) + 65);
		}
		else { // gera letra minuscula
			return (char) (random.nextInt(26) + 97);
		}
	}
	
}
