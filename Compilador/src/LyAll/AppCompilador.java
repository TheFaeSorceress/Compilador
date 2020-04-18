package LyAll;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;


public class AppCompilador extends JFrame implements ActionListener{
	// Componentes o Atributos
		private JMenuBar barraMenu;
		private JMenu menuArchivo;
		// Menu Archivo
		private JMenuItem itemNuevo,itemAbrir,itemGuardar,itemSalir,itemAnalisLexico;
		private JFileChooser ventanaArchivos;
		private File archivo;
		private JTextArea areaTexto;
		private JScrollPane barrita; 
		private JList<String> lista_tokens;
		private JTabbedPane documentos,consola,tabla_simbolos;
		private String [] titulos ={"Tipo","Nombre","Valor","Alcance","Renglon"};
		DefaultTableModel modelo = new DefaultTableModel(new Object[0][0],titulos);
		private JTable mitabla = new JTable(modelo);
		private JButton btnAnalizar;


	public static void main(String[] args) {
		
		new AppCompilador();

	}
	
	public AppCompilador() {
		super("Analizador Lexico y Sintáctico");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setLayout(new GridLayout(2,2));
		//mitabla.setBackground(new Color(255,45,0));
		setSize(1000,550);
		setLocationRelativeTo(null);
		InitComponents();
		setVisible(true);
	}
	
	public void InitComponents() {
		barraMenu = new JMenuBar();
		setJMenuBar(barraMenu);
		menuArchivo = new JMenu("Archivo");
		menuArchivo.setIcon(new ImageIcon("archivo.png"));
		//MenuAnalisis =  new JMenu("Analisis");
		//MenuAnalisis.setIcon(new ImageIcon("analisis.png"));
		ventanaArchivos = new JFileChooser();
		itemNuevo = new JMenuItem("Nuevo");
		itemAbrir = new JMenuItem("Abrir...");
		itemGuardar = new JMenuItem("Guardar...");
		itemSalir = new JMenuItem("Salir");
		itemSalir.addActionListener(this);
		itemGuardar.addActionListener(this);
		itemAbrir.addActionListener(this);
		itemNuevo.addActionListener(this);
		itemAnalisLexico  = new JMenuItem("Analizar codigo");
		itemAnalisLexico.addActionListener(this);
		btnAnalizar = new JButton("ANALIZAR");
		btnAnalizar.setFont(new Font("Dialog",Font.PLAIN,40));
		btnAnalizar.addActionListener(this);
		
		
		ventanaArchivos = new JFileChooser();
		menuArchivo.add(itemNuevo);
		menuArchivo.add(itemAbrir);
		menuArchivo.add(itemGuardar);
		menuArchivo.addSeparator();
		menuArchivo.add(itemSalir);
		//MenuAnalisis.add(itemAnalisLexico);
		barraMenu.add(menuArchivo);
		//barraMenu.add(MenuAnalisis);
		areaTexto = new JTextArea();
		
	
		
		ventanaArchivos= new JFileChooser("Guardar");
		areaTexto.setFont(new Font("Consolas", Font.PLAIN, 12));		

		NumeroLinea lineNumber = new NumeroLinea(areaTexto);
		
		barrita = new JScrollPane(areaTexto);
		barrita.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		barrita.setPreferredSize(new Dimension(870, 65));
		barrita.setRowHeaderView(lineNumber);
		
		documentos = new JTabbedPane();
		documentos.addTab("Nuevo",barrita);
		consola = new JTabbedPane();
		tabla_simbolos = new JTabbedPane();

		documentos.setToolTipText("Aqui se muestra el codigo");
		add(documentos);
		lista_tokens=new JList<String>();
		consola.addTab("Consola",new JScrollPane(lista_tokens));
		//consola.addTab("Tabla",new JScrollPane(mitabla));
		tabla_simbolos.addTab("Tabla de simbolos",new JScrollPane(mitabla) );
		add(consola);
		consola.setToolTipText("Aqui se muestra el resultado del analisis");
		add(tabla_simbolos);
		add(btnAnalizar);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnAnalizar) {
			if(guardar()){
				Analisis analisador = new Analisis(archivo.getAbsolutePath());
				lista_tokens.setListData(analisador.getmistokens().toArray( new String [0]));
//				codigo.setListData(analisador.getTabla3().toArray( new String [0] ));
				modelo = new DefaultTableModel(new Object[0][0],titulos);
//				modelo2 = new DefaultTableModel(new Object[0][0],titulos2);

//			    mitabla.setDefaultRenderer(Object.class, color);
			  
				for (int i=0; i < analisador.getTabla().size(); i++) {
					TablaDeSimbolos id = analisador.getTabla().get(i);						
					mitabla.setModel(modelo);
					if(!id.tipo.equals("")) {
						Object datostabla[]= {id.tipo,id.nombre,id.valor,id.alcance,id.renglon};

						modelo.addRow(datostabla);
					}
				}

			}
		
			return;
		}
		if (e.getSource()==itemSalir) {
			System.exit(0);
			return;
		}
		if(e.getSource()==itemNuevo) {
			documentos.setTitleAt(0, "Nuevo");
			areaTexto.setText("");
			archivo=null;
			lista_tokens.setListData(new String[0]);
			return;
		}
		if(e.getSource()==itemAbrir) {
			ventanaArchivos.setDialogTitle("Abrir..");
			ventanaArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(ventanaArchivos.showOpenDialog(this)==JFileChooser.CANCEL_OPTION) 
				return;
			archivo=ventanaArchivos.getSelectedFile();
			documentos.setTitleAt(0, archivo.getName());
			abrir();
		}
		if(e.getSource()==itemGuardar) {
			guardar();
		}
	}
	public boolean guardar() {
		try {
			if(archivo==null) {
				ventanaArchivos.setDialogTitle("Guardando..");
				ventanaArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(ventanaArchivos.showSaveDialog(this)==JFileChooser.CANCEL_OPTION) 
					return false;
				archivo=ventanaArchivos.getSelectedFile();
				documentos.setTitleAt(0, archivo.getName());
			}
			FileWriter fw = new FileWriter(archivo);
			BufferedWriter bf = new BufferedWriter(fw);
			bf.write(areaTexto.getText());
			bf.close();
			fw.close();
		}catch (Exception e) {
			System.out.println("Houston tenemos un problema?");
			return false;
		}
		return true;
	}
	public boolean abrir() {
		String texto="",linea;
		try {
			FileReader fr = new FileReader(archivo) ; 
			BufferedReader br= new BufferedReader(fr);
			while((linea=br.readLine())!=null) 
				texto+=linea+"\n";
			areaTexto.setText(texto);
			return true;
		}catch (Exception e) {
			archivo=null;
			JOptionPane.showMessageDialog(null, "Tipo de archivo incompatible", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}

}
