package com.felipealencar.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.felipealencar.cursomc.domain.Cliente;
import com.felipealencar.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);

	void sendNewPasswordEmail(Cliente cliente, String newPass);
	
}
