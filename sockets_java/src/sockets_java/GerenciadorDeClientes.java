package sockets_java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GerenciadorDeClientes extends Thread{

	private Socket cliente;
	private String nomeCliente;
	
	public GerenciadorDeClientes(Socket cliente) {
		this.cliente = cliente;
		start();
	}
	
	@Override
	public void run() {
		try {
			BufferedReader leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			//receber bytes e transforma em strings
			PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
			//envia a mensagem escrita em forma de stream de forma automatica (true)
			escritor.println("Por favor escrea seu nome: ");
			String msg = leitor.readLine();
			this.nomeCliente = msg;
			escritor.println("Ola" + this.nomeCliente);
			
			while(true) {
				msg = leitor.readLine();
				escritor.println(this.nomeCliente + "Voce disse: " + msg);
			}
		} catch (IOException e) {
			System.err.printf("O cliente fechou o servidor");
			e.printStackTrace();
		} 
	}

}
