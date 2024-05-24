package sockets_java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorDeClientes extends Thread {

    private Socket cliente;
    private String nomeCliente;
    private BufferedReader leitor;
    private PrintWriter escritor;
    private static final Map<String, GerenciadorDeClientes> clientes = new HashMap<String,GerenciadorDeClientes>();
    

    public GerenciadorDeClientes(Socket cliente) {
        this.cliente = cliente;
        start();
    }

    @Override
    public void run() {
        try {
            leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            escritor = new PrintWriter(cliente.getOutputStream(), true);
            efetuarLogin();
            String msg;

            while (true) {
                msg = leitor.readLine();
                if (msg.equalsIgnoreCase(Comandos.SAIR)) {
                    this.cliente.close();
                    
                } else if (msg.startsWith(Comandos.MENSAGEM)) {
                    String nomeDestinatario = msg.substring(Comandos.MENSAGEM.length(), msg.length());
                    System.out.println("Enviando para " + nomeDestinatario);
                    GerenciadorDeClientes destinatario = clientes.get(nomeDestinatario);
                    if (destinatario == null) {
                        escritor.println("Cliente não existe");
                    } else {
                    	destinatario.getEscritor().println(this.nomeCliente + " disse: " + leitor.readLine());
                    	}
                //lista  todos os clientes logados    
                } else if (msg.equals(Comandos.LISTA_USUARIOS)) {
                    atualizarListaUsuarios(this);
                
                } else {
                        escritor.println(this.nomeCliente + ", você disse: " + leitor.readLine());
                }
               }
            
        } catch (IOException e) {
            System.err.println("O cliente fechou a conexão");
            clientes.remove(this.nomeCliente);
            e.printStackTrace();
        } 
    }

    private synchronized void efetuarLogin() throws IOException {
		
		while(true){
			escritor.println(Comandos.LOGIN);
			this.nomeCliente = leitor.readLine().toLowerCase().replaceAll(",", "");
			if(this.nomeCliente.equalsIgnoreCase("null") || this.nomeCliente.isEmpty()){
				escritor.println(Comandos.LOGIN_NEGADO);
			}else if(clientes.containsKey(this.nomeCliente)){
				escritor.println(Comandos.LOGIN_NEGADO);
			}else{
				escritor.println(Comandos.LOGIN_ACEITO);
				escritor.println("olá " + this.nomeCliente);
				clientes.put(this.nomeCliente, this);
				for(String cliente: clientes.keySet()){
					atualizarListaUsuarios(clientes.get(cliente));
				}
				break;
			}
		}
	}

	private void atualizarListaUsuarios(GerenciadorDeClientes cliente) {
    	StringBuffer str = new StringBuffer();
        for (String c : clientes.keySet()) {
        	if(cliente.getNomeCliente().equals(c))
				continue;
			
			str.append(c);
			str.append(",");
        }
        if(str.length() > 0)
			str.delete(str.length()-1, str.length());
		cliente.getEscritor().println(Comandos.LISTA_USUARIOS);
		cliente.getEscritor().println(str.toString());
		
	}

	public PrintWriter getEscritor() {
        return escritor;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }
    
}
