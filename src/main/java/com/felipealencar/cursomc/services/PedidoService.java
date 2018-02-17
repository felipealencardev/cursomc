package com.felipealencar.cursomc.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.felipealencar.cursomc.domain.Cliente;
import com.felipealencar.cursomc.domain.ItemPedido;
import com.felipealencar.cursomc.domain.PagamentoComBoleto;
import com.felipealencar.cursomc.domain.Pedido;
import com.felipealencar.cursomc.domain.enums.EstadoPagamento;
import com.felipealencar.cursomc.repositories.ClienteRepository;
import com.felipealencar.cursomc.repositories.ItemPedidoRepository;
import com.felipealencar.cursomc.repositories.PagamentoRepository;
import com.felipealencar.cursomc.repositories.PedidoRepository;
import com.felipealencar.cursomc.repositories.ProdutoRepository;
import com.felipealencar.cursomc.security.UserSS;
import com.felipealencar.cursomc.services.exceptions.AuthorizationException;
import com.felipealencar.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo; 
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EmailService emailService;
	
	public Pedido find(Integer id) {
		Pedido obj = repo.findOne(id);
		if (obj == null) {
			throw new ObjectNotFoundException("Objeto não encontrado! Id: " + id
					+ ", Tipo: " + Pedido.class.getName());
		}
		return obj;
	}
	
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteRepository.findOne(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if(obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			this.boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = this.repo.save(obj);
		this.pagamentoRepository.save(obj.getPagamento());
		for(ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(this.produtoRepository.findOne(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		this.itemPedidoRepository.save(obj.getItens());
		emailService.sendOrderConfirmationEmail(obj);
		return obj;
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderby, String direction) {
		UserSS user = UserService.authenticaded();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderby);
		Cliente cliente = this.clienteRepository.findOne(user.getId());
		return this.repo.findByCliente(cliente, pageRequest);
	}
}
