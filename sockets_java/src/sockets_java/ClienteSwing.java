package sockets_java;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClienteSwing {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter escritor;
    private BufferedReader leitor;

    public ClienteSwing() {
        frame = new JFrame("Chat App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Área de texto para exibir mensagens
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        
        // Campo de texto para digitar novas mensagens
        inputField = new JTextField(25);

        // Botão de enviar
        sendButton = new JButton("Enviar");

        // Painel inferior contendo o campo de texto e o botão
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Adiciona o painel de rolagem e o painel de entrada ao frame
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        // Adiciona um listener ao botão de enviar
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Permite o envio de mensagens pressionando Enter
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            messageArea.append("Você: " + message + "\n");
            inputField.setText("");
            if (escritor != null) {
                escritor.println(message);
            }
        }
    }

    private void connectToServer(String serverAddress, int port) {
        try {
            Socket cliente = new Socket(serverAddress, port);
            leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            escritor = new PrintWriter(cliente.getOutputStream(), true);

            // Thread para ler mensagens do servidor
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String mensagem;
                        while ((mensagem = leitor.readLine()) != null) {
                            final String msg = mensagem;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    messageArea.append("Servidor: " + msg + "\n");
                                }
                            });
                        }
                    } catch (IOException e) {
                        System.out.println("Impossível ler a mensagem do servidor");
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (UnknownHostException e) {
            System.out.println("O endereço de IP passado é inválido");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("O servidor está fora do ar");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClienteSwing client = new ClienteSwing();
                client.connectToServer("192.168.1.9", 12345); // Altere para o endereço e porta do servidor
            }
        });
    }
}
