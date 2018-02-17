package com.felipealencar.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.felipealencar.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);
	
}
