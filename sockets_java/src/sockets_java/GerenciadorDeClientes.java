package sockets_java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorDeClientes extends Thread{

	private Socket cliente;
	private String nomeCliente;
	private BufferedReader leitor;
	private PrintWriter escritor;
	public static final Map<String, GerenciadorDeClientes>  clientes = new HashMap<String, GerenciadorDeClientes>();
	//tipo chave - valor que rodará em todo o programa, por isso constante
	
	public GerenciadorDeClientes(Socket cliente) {
		this.cliente = cliente;
		start();
	}
	
	@Override
	public void run() {
		try {
			leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			escritor = new PrintWriter(cliente.getOutputStream(), true);
			escritor.println("Por favor escreva seu nome: ");
			String msg = leitor.readLine();
			this.nomeCliente = msg;
			escritor.println("Olá, " + this.nomeCliente);
			clientes.put(this.nomeCliente, this);
			
			while(true) {
				msg = leitor.readLine();
				if(msg.equalsIgnoreCase("::SAIR")) {
					this.cliente.close();
					
				}else if(msg.toLowerCase().startsWith("::msg")) {
						String nomeDestinatario = msg.substring(5, msg.length());
						System.out.println("Enviando para " + nomeDestinatario);
						GerenciadorDeClientes destinatario =  clientes.get(nomeDestinatario);
					if(destinatario == null) {
							System.out.println("Cliente nao exixte");
					}else {
							escritor.println("Digite uma mensagem para " + destinatario.getNomeCliente());
							destinatario.getEscritor().println(this.nomeCliente + " disse: " + leitor.readLine());
						}
					
				}else{
					escritor.println(this.nomeCliente + ", você disse: " + msg);
					
				}
			}
		} catch (IOException e) {
			System.err.println("O cliente fechou a conexão");
			e.printStackTrace();
		}
	}
	
	public PrintWriter getEscritor() {
		return escritor;
	}
	public String getNomeCliente() {
		return nomeCliente;
	}
//	public BufferedReader getLeitor() {
//		return leitor;
//	}
	
}
