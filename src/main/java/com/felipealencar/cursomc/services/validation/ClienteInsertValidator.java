package com.felipealencar.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.felipealencar.cursomc.domain.Cliente;
import com.felipealencar.cursomc.domain.enums.TipoCliente;
import com.felipealencar.cursomc.dto.ClienteNewDTO;
import com.felipealencar.cursomc.repositories.ClienteRepository;
import com.felipealencar.cursomc.resources.exception.FieldMessage;
import com.felipealencar.cursomc.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO> {
	
	@Autowired
	private ClienteRepository repo;
	
	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(ClienteNewDTO objDto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		if (objDto.getTipo().equals(TipoCliente.PESSOAFISICA.getCod()) && !BR.isValidSsn(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "Cpf Inválido"));
		}
		
		if (objDto.getTipo().equals(TipoCliente.PESSOAJURIDICA.getCod()) && !BR.isValidTin(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "Cnpj Inválido"));
		}
		
		Cliente aux = repo.findByEmail(objDto.getEmail());
		
		if (aux != null) {
			list.add(new FieldMessage("email", "Email já existente"));
		}
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		
		return list.isEmpty();
	}
}
