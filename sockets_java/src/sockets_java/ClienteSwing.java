package sockets_java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;

public class ClienteSwing extends JFrame{
    
	
	
    private static final long serialVersionUID = -8529839079872877437L;
    private JFrame frame;
    private JTextArea messageArea;
    private JTextArea inputField = new JTextArea("Digite aqui sua mensagem: ");
    private JButton sendButton;
    private JList LiUsuarios = new JList();
    private PrintWriter escritor;
    private BufferedReader leitor;
    private JComboBox<String> comboBox;

    public ClienteSwing() {
    	Janela();
    }
    	public void Janela() { 
    	frame = new JFrame("Chat Java");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        
        //area que contem a troca de mensagens 
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setVisible(true);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        inputField = new JTextArea(1, 25);
        
        sendButton = new JButton("Enviar");
        sendButton.setUI(new Botao());
        
        JPanel inputPanel = new JPanel();
        inputPanel.setVisible(true);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        iniciarEscritor();
        
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);
        
        JPanel cima = new JPanel();
        cima.setVisible(true);
        cima.setLayout(new BorderLayout());
        String[] cores = {"Branco", "Preto", "Rosa", "Verde"};
        JComboBox<String> cor = new JComboBox<>(cores);
        cor.setBackground(Color.WHITE); 
        cor.setForeground(Color.BLACK);
        cor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE); 
                } else {
                    c.setBackground(Color.WHITE); 
                    c.setForeground(Color.BLACK); 
                }
                return c;
            }
        });
        
        
        
        String[] usuarios = new String[] {};
        preencherListaUsuarios(usuarios);
        
//        adiciona usuarios onlines contidos na jlist ao combobox
        
        comboBox = new JComboBox<>(usuarios);
        comboBox.setBounds(50, 50, 50, 30);
        comboBox.setBackground(Color.WHITE); 
        comboBox.setForeground(Color.BLACK);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE); 
                } else {
                    c.setBackground(Color.WHITE); 
                    c.setForeground(Color.BLACK); 
                }
                return c;
            }
        });
        cor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verifica qual cor foi selecionada e define a cor de fundo correspondente na área de texto
                String selectedColor = (String) cor.getSelectedItem();
                switch (selectedColor) {
                    case "Branco":
                        scrollPane.getViewport().setBackground(Color.WHITE);
                        messageArea.setForeground(Color.BLACK);
                        break;
                    case "Preto":
                        scrollPane.getViewport().setBackground(Color.BLACK);
                        messageArea.setForeground(Color.WHITE);
                        break;
                    case "Rosa":
                        scrollPane.getViewport().setBackground(Color.getHSBColor(217, 203, 39));
                        messageArea.setForeground(Color.BLACK);
                        break;
                    case "Verde":
                        scrollPane.getViewport().setBackground(Color.getHSBColor(111, 217, 124));
                        messageArea.setForeground(Color.BLACK);
                        break;
                }
            }
        });
        cima.add(comboBox, BorderLayout.CENTER);
        cima.add(cor, BorderLayout.EAST);
        frame.getContentPane().add(cima, BorderLayout.NORTH);
        comboBox.setSelectedIndex(-1);


        
    }

    	private void preencherListaUsuarios(String[] usuarios) {
            DefaultListModel<String> modelo = new DefaultListModel<>();
            LiUsuarios.setModel(modelo);

            

            for (String usuario : usuarios) {
                modelo.addElement(usuario);
                comboBox.addItem(usuario);  // Adicionar cada item ao JComboBox
            }
        }


    

    private void iniciarEscritor() {
		inputField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					
					//escrevendo para o servidor
					
					if(messageArea.getText().isEmpty()) {
							return;
					}
					
					
					Object usuario = (String) comboBox.getSelectedItem();
//					Object usuario = LiUsuarios.getSelectedValue();
					if(usuario != null) {
						// mostrando no visor
						messageArea.append("Você: ");
						messageArea.append(inputField.getText());
						messageArea.append("\n"); 
						
						escritor.println(Comandos.MENSAGEM + usuario);
						escritor.println(inputField.getText());
						
						//limpando o editor	
						inputField.setText("");	
						e.consume();
						
					}else {
						if(messageArea.getText().equalsIgnoreCase(Comandos.SAIR)) {
							System.exit(0);
						}					
						JOptionPane.showMessageDialog(ClienteSwing.this, "Selecione um usuario");
						return;
					}
					
				}

			}				
			
			
			

			private DefaultListModel getListaUsuarios() {				
				return (DefaultListModel) LiUsuarios.getModel();
			}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
		
	}
    
    
    public void iniciarChat()  {
    	try {
            final Socket cliente = new Socket("127.0.0.1", 12345);            
            escritor = new PrintWriter(cliente.getOutputStream(), true);
            leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
        }catch(UnknownHostException e) {
            System.out.println("O endereço de IP passado é inválido");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("O servidor está fora do ar");
            e.printStackTrace();
        }
    
    } 
    
    private void iniciarLeitor() {
		// lendo mensagens do servidor
		try {
			while(true){
				String mensagem = leitor.readLine();
				if(mensagem == null || mensagem.isEmpty())
					continue;
				
				// recebe o texto
				if(mensagem.equals(Comandos.LISTA_USUARIOS)){
					String[] usuarios = leitor.readLine().split(",");
					preencherListaUsuarios(usuarios);
				}else if(mensagem.equals(Comandos.LOGIN)){
					String login = JOptionPane.showInputDialog("Qual o seu login?");
					escritor.println(login);
				}else if(mensagem.equals(Comandos.LOGIN_NEGADO)){
					JOptionPane.showMessageDialog(ClienteSwing.this, "o login é inválido");
				}else if(mensagem.equals(Comandos.LOGIN_ACEITO)){
					atualizarListaUsuarios();	
				}else{
					messageArea.append(mensagem);
					messageArea.append("\n");
					messageArea.setCaretPosition(messageArea.getDocument().getLength());
				}
			}
			
		} catch (IOException e) {
			System.out.println("impossivel ler a mensagem do servidor");
			e.printStackTrace();
		}		
	}
    
    private void atualizarListaUsuarios() {
    	escritor.println(Comandos.LISTA_USUARIOS);
    	
    }

    public static void main(String[] args) {
	ClienteSwing cliente = new ClienteSwing();
	cliente.iniciarChat();
	cliente.iniciarEscritor();
	cliente.iniciarLeitor();
}
    
}
