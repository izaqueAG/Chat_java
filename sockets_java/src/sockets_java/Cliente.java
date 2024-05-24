package sockets_java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {

	public static void main(String[] args) {
		try {
			Socket cliente = new Socket("192.168.1.9", 12345);
			
			// lendo a mensagem do servidor 
			new Thread() {
				@Override
				public void run() {
					try {
						BufferedReader leitor = new  BufferedReader(new InputStreamReader(cliente.getInputStream()));
						
						
						while(true) {
							String mensagem = leitor.readLine();
							if(mensagem == null || mensagem.isEmpty())
								continue;
							System.out.println("O servidor disse: " + mensagem);
						}
					} catch (IOException e) {
						System.out.println("Impossivel ler a mensagem do servidor");
						e.printStackTrace();
					}
				}
				
			}.start();
			
			
			
		} catch (UnknownHostException e) {
			System.out.println("O endereco de ip passado e invalido");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("O servidor esta fora do ar");
			e.printStackTrace();
		}

	}

}
