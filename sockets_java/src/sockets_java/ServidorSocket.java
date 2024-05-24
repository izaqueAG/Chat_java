package sockets_java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {
	public static void main(String[] args) {
		ServerSocket servidor = null;
		try {
			System.out.println("Iniciando o Servidor");
			servidor = new ServerSocket(12345);
			System.out.println("Servidor iniciado");
			
			while(true) {
				Socket cliente = servidor.accept(); //espera os clientes se conectarem
				new GerenciadorDeClientes(cliente);
			}
			
		} catch (IOException e) {
			System.err.println("A porta esta ocupada ou o servidor nao esta disponivel");
			try {
				if(servidor != null) {
					servidor.close(); 
				}				
			} catch (IOException e1) {}
			System.err.println("A porta esta ocupada ou o servidor foi fechado");
			e.printStackTrace();
		}
	}
}
