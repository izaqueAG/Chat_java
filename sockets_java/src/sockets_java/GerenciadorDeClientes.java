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
    public static final Map<String, GerenciadorDeClientes> clientes = new HashMap<String, GerenciadorDeClientes>();
    private GerenciadorDeClientes destinatario = null;

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
            this.nomeCliente = msg.toLowerCase().replace(",", "");
            escritor.println("Olá, " + this.nomeCliente);
            clientes.put(this.nomeCliente, this);

            while (true) {
                msg = leitor.readLine();
                if (msg.equalsIgnoreCase("::SAIR")) {
                    this.cliente.close();
                    break;
                } else if (msg.toLowerCase().startsWith("::msg")) {
                    String nomeDestinatario = msg.substring(5).trim();
                    System.out.println("Enviando para " + nomeDestinatario);
                    destinatario = clientes.get(nomeDestinatario);
                    if (destinatario == null) {
                        escritor.println("Cliente não existe");
                    } else {
                        escritor.println("Aguardando  " + destinatario.getNomeCliente() + " aceitar sua solicitação" );
                        destinatario.getEscritor().println(this.nomeCliente + " quer se conectar a você. Aceitar conexão? (s/n): ");
                        
                     // Usando uma nova thread para aguardar a resposta do destinatário
                        new Thread(() -> {
                            try {
                                String resposta = leitor.readLine();
                                if (resposta.equalsIgnoreCase("s")) {
                                    escritor.println(destinatario.getNomeCliente() + " aceitou sua solicitação.");
                                    destinatario.getEscritor().println("Conexão com " + this.nomeCliente + " estabelecida.");
                                    ConexaoDireta(destinatario);
                                } else {
                                    escritor.println(destinatario.getNomeCliente() + " recusou sua solicitação.");
                                }
                            } catch (IOException e) {
                                escritor.println("Erro ao aguardar resposta do destinatário.");
                                e.printStackTrace();
                            }
                        }).start();
                    }
                } else if (msg.equals("::listar-clientes")) {
                    StringBuffer str = new StringBuffer();
                    for (String c : clientes.keySet()) {
                        str.append(c);
                        str.append(",");
                    }
                    if (str.length() > 0) {
                        str.deleteCharAt(str.length() - 1);
                    }
                    escritor.println(str.toString());
                } else {
                    if (destinatario != null) {
                        destinatario.getEscritor().println(this.nomeCliente + " disse: " + msg);
                    } else {
                        escritor.println(this.nomeCliente + ", você disse: " + msg);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("O cliente fechou a conexão");
            e.printStackTrace();
        } finally {
            if (nomeCliente != null) {
                clientes.remove(nomeCliente);
                if (destinatario != null) {
                    destinatario.getEscritor().println(this.nomeCliente + " se desconectou.");
                }
                System.out.println(nomeCliente + " se desconectou.");
            }
        }
    }

    public PrintWriter getEscritor() {
        return escritor;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }
    private void ConexaoDireta(GerenciadorDeClientes destinatario) throws IOException {
        while (true) {
        	String msg = leitor.readLine();
            try {
                msg = leitor.readLine();
                if (msg.equalsIgnoreCase("::SAIR")) {
                    this.cliente.close();
                    break;
                }
                destinatario.getEscritor().println(this.nomeCliente + " disse: " + msg);
            } catch (IOException e) {
                System.err.println("Erro na comunicação com o destinatário.");
                e.printStackTrace();
                break;
            }
        }
    }
}
