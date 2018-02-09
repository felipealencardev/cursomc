package com.felipealencar.cursomc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.felipealencar.cursomc.domain.Categoria;
import com.felipealencar.cursomc.repositories.CategoriaRepository;
import com.felipealencar.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo; 
	
	public Categoria find(Integer id) {
		Categoria obj = repo.findOne(id);
		if (obj == null) {
			throw new ObjectNotFoundException("Objeto não encontrado! Id: " + id
					+ ", Tipo: " + Categoria.class.getName());
		}
		return obj;
	}
	
	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return this.repo.save(obj);
	}
	
	public Categoria update(Categoria obj) {
		this.find(obj.getId());
		return this.repo.save(obj);
	}
	
}
