package analisis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Analisis {

	int renglon=1;
	ArrayList<String> impresion; //para la salida
	ListaDoble<Token> tokens;
	final Token vacio=new Token("", 9,0);
	boolean bandera=true,banderaclase=false, banderaErroresSemanticos = false,banderaErroresSintacticos = false;
//	public ColorCeldas color = new ColorCeldas(4);
	public String CodigoObjeto=null;
	ArrayList<TablaDeSimbolos> tablasimbolos = new ArrayList<TablaDeSimbolos>();
	ArrayList<Arbol_CodInt> arbol = new ArrayList<Arbol_CodInt>();
	ArrayList<String> expresion = new ArrayList<String>();
	private ArrayList<String> codigo_datos;


	String Anterior1Valor;
	String Anterior2Valor;
	String Anterior3Valor;
	String Anterior4Valor;
	String Anterior5Valor;
	String cadenaauxiliar ="";
	int Anterior1Tipo ;
	int Anterior2Tipo;
	int Anterior3Tipo;
	int Anterior4Tipo;
	int Anterior5Tipo;
	String Siguiente1Valor;
	String Siguiente2Valor;
	int Siguiente1Tipo;	
	String operation = "";
	int Resultadofinal;
	int RFS;
	int RFR;
	int RFD;
	int RFM;
	int contador = 1;
	
	public ArrayList<TablaDeSimbolos> getTabla() {
		return tablasimbolos ;
	}
		
	public ArrayList<Arbol_CodInt> getTabla2() {
		return arbol ;
	}
	
	public ArrayList<String> getTabla3() {
		return codigo_datos;
	}
	
	public Analisis(String ruta) {//Recibe el nombre del archivo de texto
		analisaCodigo(ruta);
		if(bandera) {
			impresion.add("No hay errores lexicos");
			
			banderaErroresSemanticos = false;
			banderaErroresSintacticos = false;
			analisisSintactio(tokens.getInicio());
			AnalizadorSemantico(tokens.getInicio());
			Semantico2(tokens.getInicio());
			VerificarClase(tokens.getInicio());
			VerificarClase(tokens.getInicio());
			GenerarCodigoIntermedio(getTabla2());

			if(!banderaclase){
				impresion.add("Falta la inicializaci�n de la clase!");
			}
			
		}
		

		if(!banderaErroresSintacticos)
			impresion.add("No hay errores sintacticos!");
		
		if(!banderaErroresSemanticos)
			impresion.add("No hay errores semanticos!");

		
		for (int i = 0; i < tablasimbolos.size(); i++) {
			System.out.println(tablasimbolos.get(i).toString());
		}
		System.out.println();
			
	}
	public void analisaCodigo(String ruta) {
		String linea="", token="";
		StringTokenizer tokenizer;
		try{
	          FileReader file = new FileReader(ruta);
	          BufferedReader archivoEntrada = new BufferedReader(file);
	          linea = archivoEntrada.readLine();
	          impresion=new ArrayList<String>();
	          tokens = new ListaDoble<Token>();
	          while (linea != null){
	        	    linea = separaDelimitadores(linea);
	                tokenizer = new StringTokenizer(linea);
	                while(tokenizer.hasMoreTokens()) {
	                	token = tokenizer.nextToken();
	                	analisisLexico(token);
	                }
	                linea=archivoEntrada.readLine();
	                renglon++;
	          }
	          archivoEntrada.close();
		}catch(IOException e) {
			JOptionPane.showMessageDialog(null,"No se encontro el archivo favor de checar la ruta","Alerta",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	
	public void analisisLexico(String token) {
		
		int tipo=0;
		//Se usan listas con los tipos de token
		// Esto se asemeja a un in en base de datos 
		//Ejemplo select * from Clientes where Edad in (18,17,21,44)
		if(Arrays.asList("public","static","private","protected").contains(token)) 
			tipo = Token.MODIFICADOR;
		else if(Arrays.asList("if","else").contains(token)) 
			tipo = Token.PALABRA_RESERVADA;
		else if(Arrays.asList("int","float","boolean", "char").contains(token))
			tipo = Token.TIPO_DATO;
		else if(Arrays.asList("(",")","{","}","=",";").contains(token))
			tipo = Token.SIMBOLO;
		else if(Arrays.asList("<","<=",">",">=","==","!=").contains(token))
			tipo = Token.OPERADOR_LOGICO; 
		else if(Arrays.asList("+","-","*","/").contains(token))
			tipo = Token.OPERADOR_ARITMETICO;
		else if(Arrays.asList("true","false").contains(token)||Pattern.matches("^[0-9]+$",token)
				||Pattern.matches("[0-9]+.[0-9]+",token)||Pattern.matches("'[a-zA-Z]'",token) ||Pattern.matches("-[0-9]+$",token)) 
			tipo = Token.CONSTANTE;
		else if(token.equals("class")) 
			tipo =Token.CLASE;
		else {
			//Cadenas validas
			Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$")  ;//Expresiones Regulares
			Matcher mat = pat.matcher(token);
				
			if(mat.find()) 
				tipo = Token.IDENTIFICADOR;
			
	
			else {
				impresion.add("Error lexico en la linea "+renglon+" token "+token);
				bandera = false;
				return;
			}
		}
		tokens.insertar(new Token(token,tipo,renglon));
		impresion.add(new Token(token,tipo,renglon).toString());
		

		
	}



	public Token analisisSintactio(NodoDoble<Token> nodo) {

		Token  to;


		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;

			
			try{

				Anterior1Valor = nodo.anterior.dato.getValor();
				Anterior1Tipo = nodo.anterior.dato.getTipo();
				Anterior2Valor = nodo.anterior.anterior.dato.getValor();
				Anterior2Tipo = nodo.anterior.anterior.dato.getTipo();
				Anterior3Valor = nodo.anterior.anterior.anterior.dato.getValor();
				Anterior3Tipo = nodo.anterior.anterior.anterior.dato.getTipo();
				Anterior4Valor = nodo.anterior.anterior.anterior.anterior.dato.getValor();
				Anterior4Tipo = nodo.anterior.anterior.anterior.anterior.dato.getTipo();
				Anterior5Valor = nodo.anterior.anterior.anterior.anterior.anterior.dato.getValor();
				Anterior5Tipo= nodo.anterior.anterior.anterior.anterior.anterior.dato.getTipo();
				
				
			}catch (Exception e){
				e.getMessage();
			}
			
			try{
				Siguiente1Valor = nodo.siguiente.dato.getValor();
				Siguiente1Tipo = nodo.siguiente.dato.getTipo();
				Siguiente2Valor = nodo.siguiente.siguiente.dato.getValor();
				
				
			}catch (Exception e){
				e.getMessage();
			}
			

			try{
				switch (to.getTipo()) // un switch para validar la estructura
				{
				case Token.MODIFICADOR:
					int sig=Siguiente1Tipo;
					// aqui se valida que sea 'public int' o 'public class' 
					if(sig!=Token.TIPO_DATO && sig!=Token.CLASE)// si lo que sigue 
					{
//						AppCompilador.enviarErrorSintactico(to.getLinea());
						banderaErroresSintacticos = true;
						impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+" se esperaba un tipo de dato");
						JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un tipo de dato",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
					}
					break;
				case Token.IDENTIFICADOR:
					// lo que puede seguir despues de un idetificador
					try{
						if((Arrays.asList("{","=",";","==",")").contains(Siguiente1Valor))) {
							if(Anterior1Valor.equals("class")) // se encontro la declaracion de la clase
							{
								tablasimbolos.add( new TablaDeSimbolos(to.getValor(), " ", "class"," ",to.getLinea()));

							}
						}

					}catch (Exception e){
						banderaErroresSintacticos = true;
						impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+" se esperaba un s�mbolo");
						JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+" se esperaba un s�mbolo",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						System.out.println(e.getMessage());
					}

					break;
					
				case Token.TIPO_DATO:
					if (nodo.anterior!=null)
						if(Anterior1Tipo==Token.MODIFICADOR) {
							if(Siguiente1Tipo!=Token.IDENTIFICADOR) {
								banderaErroresSintacticos = true;
								impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+" "
										+ "se esperaba un identificador");
								JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+"se esperaba un identificador",
										"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
							}

						}else{
							if(Siguiente1Tipo!=Token.IDENTIFICADOR) {
								banderaErroresSintacticos = true;
								impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+" "
										+ "se esperaba un identificador");
								JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+"se esperaba un identificador",
										"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
							}
						}
					break;
				case Token.CLASE:

					// si lo anterior fue modificador
					if (nodo.anterior!=null)
						if(Anterior1Tipo==Token.MODIFICADOR) {
							if(Siguiente1Tipo!=Token.IDENTIFICADOR) {
								banderaErroresSintacticos = true;
								impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+" "
										+ "se esperaba un identificador");
								JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+"se esperaba un identificador",
										"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
							}

						}else{
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un modificador");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+"se esperaba un modificador",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}
					break;
				case Token.SIMBOLO:
					// Verificar que el mismo numero de parentesis y llaves que abren sean lo mismo que los que cierran
					if(to.getValor().equals("}")) 
					{
						if(cuenta("{")!=cuenta("}")){
							banderaErroresSintacticos = true;
							impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+ " falta un {");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " falta un {",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}
					}else if(to.getValor().equals("{")) {
						if(cuenta("{")!=cuenta("}")){
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un }");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " falta un }",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}
					}

					else if(to.getValor().equals("(")) {
						if(cuenta("(")!=cuenta(")")){
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un )");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " falta un )",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}
					}else if(to.getValor().equals(")")) {
						if(cuenta("(")!=cuenta(")")){
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un (");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " falta un (",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}
					}
					// verificar la asignacion
					else if(to.getValor().equals("=")){


						if(Anterior1Tipo==Token.IDENTIFICADOR) {	
							if(Siguiente1Tipo!=Token.CONSTANTE && !Siguiente1Valor.contains("(") && Siguiente1Tipo!=Token.IDENTIFICADOR){
								banderaErroresSintacticos = true;
								impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba una constante");
								JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba una constante",
										"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);

							}

						}
					} 


					else if (to.getValor().equals(";"))
					{


						int aux=0;


						boolean banderita=false;
						try
						{
							if (Anterior3Tipo==Token.MODIFICADOR && Anterior2Tipo==Token.TIPO_DATO
									&& Anterior1Tipo==Token.IDENTIFICADOR) {
								tablasimbolos.add(new TablaDeSimbolos(Anterior1Valor,"",Anterior2Valor, Anterior3Valor,to.getLinea()));
									
								
							}
							else if (Anterior2Tipo==Token.TIPO_DATO && Anterior1Tipo==Token.IDENTIFICADOR){
								tablasimbolos.add(new TablaDeSimbolos(Anterior1Valor,"",Anterior2Valor,"Global",to.getLinea()));

							}
							
							//-------------------------------------------
							else if (Anterior4Tipo==Token.TIPO_DATO 
									&& Anterior3Tipo==Token.IDENTIFICADOR 
									&& Anterior2Tipo==Token.SIMBOLO
									&&Anterior1Tipo==Token.CONSTANTE){

								int x =0,auxRenglon=0;
								boolean bandera=false;
								for (int i = 0; i < tablasimbolos.size(); i++) {
									if (tablasimbolos.get(i).getNombre().equals(Anterior3Valor) ){
										x++;
										auxRenglon=i;
									}

								}
								if(Anterior4Tipo==Token.TIPO_DATO && x>0 && Anterior3Tipo==Token.IDENTIFICADOR){
									banderaErroresSemanticos=true;
									impresion.add("Error sem�ntico en linea "+to.getLinea()+ " la variable "+Anterior3Valor+" ya habia sido declarada en la linea "+tablasimbolos.get(auxRenglon).renglon);
									bandera=true;
									JOptionPane.showMessageDialog(null,"Error sem�ntico en linea "+to.getLinea()+ "la variable "+Anterior3Valor+" ya habia sido declarada en la linea ",
											"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
								}

								if(!bandera && Anterior5Tipo==Token.MODIFICADOR) {
									tablasimbolos.add(new TablaDeSimbolos(Anterior3Valor,Anterior1Valor,Anterior4Valor,Anterior5Valor,to.getLinea()));
								}else if(!bandera) {
									tablasimbolos.add(new TablaDeSimbolos(Anterior3Valor,Anterior1Valor,Anterior4Valor,"Global",to.getLinea()));
								}

							}
							else if ( (Anterior4Tipo==Token.CONSTANTE 
									&& Anterior3Tipo==Token.OPERADOR_ARITMETICO 
									&& Anterior2Tipo==Token.CONSTANTE
									&& Anterior1Valor.contains(")"))  
									|| 
									(Anterior4Tipo==Token.CONSTANTE
									&& Anterior3Tipo==Token.SIMBOLO 
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.CONSTANTE) 
									|| 
									( Anterior3Tipo==Token.CONSTANTE 
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.CONSTANTE)
									|| 
									( Anterior3Tipo==Token.IDENTIFICADOR 
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.IDENTIFICADOR)
									|| 
									( Anterior3Tipo==Token.IDENTIFICADOR
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.CONSTANTE)
									|| 
									( Anterior3Tipo==Token.CONSTANTE 
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.IDENTIFICADOR)){
								
								
								
								NodoDoble<Token> nodoaux = nodo;
								NodoDoble<Token> nodoaux2 = nodo;
								NodoDoble<Token> nodoaux3 = nodo;
								while(nodoaux!=null){
									String aux2 = nodoaux.anterior.dato.getValor();
									System.out.println(aux2);
									if(aux2.contains("="))
										break;
									
									nodoaux = nodoaux.anterior;
								}
						
								
								while(nodoaux!=null){
									String aux2 = nodoaux.dato.getValor();
									System.out.println(aux2);
									if(aux2.contains(";"))
										break;
									
									expresion.add(aux2);
									nodoaux = nodoaux.siguiente;
								}
								

								
								for (int i = 0; i < expresion.size(); i++) {
									for (int j = 0; j < tablasimbolos.size(); j++) {
										
										if(tablasimbolos.get(j).getNombre().equals(expresion.get(i))){
											System.out.println(tablasimbolos.get(j).getNombre());
											System.out.println(expresion.get(i));
											expresion.set(i, tablasimbolos.get(j).getValor());
									}
										
									}
									
								} 
								ArrayList<String> expresion2 = new ArrayList<String>(expresion);

								for (int i = 0; i < expresion.size(); i++) {
									
									if(expresion.get(i).contains("("))
									expresion.set(i, "ParAbierto");
									
									else if(expresion.get(i).contains(")"))
										expresion.set(i, "ParCerrado");
									
									else if(expresion.get(i).contains("/"))
										expresion.set(i, "Div");
									
									else if(expresion.get(i).contains("*"))
										expresion.set(i, "Multi");
									
									else if(expresion.get(i).contains("+"))
										expresion.set(i, "Suma");
									
									else if(expresion.get(i).equals("-"))
										expresion.set(i, "Resta");
									
								}
								Resultadofinal=0;							
								
								
								for (int i = 0; i < expresion.size(); i++) {
									
									try{
										if(Integer.parseInt(expresion.get(i)) <0){
											expresion2.set(i,"aux"+contador);
					
											arbol.add(new Arbol_CodInt("-",expresion.get(i).substring(1)," " ,expresion2.get(i)));
											contador++;

										}	
									}catch (Exception e){
										e.getMessage();
									}
									
									
										
										if(expresion.get(i).contains("ParAbierto") ){
											
											if (expresion.get(i).contains("ParAbierto")){
												
												int aux5 = i;
												int aux6 = 0 ;
												boolean banderaParentesis = false;
												
												for (int j = 0; j < expresion.size(); j++) {
													if(expresion.get(j).contains("ParCerrado")){
													aux6 = j;
													break;
													}
												}
												
												
												while(!banderaParentesis){
													
												//(3 * 6 - 2)	
													
													for (int j = aux5; j < aux6; j++) {

														if(expresion.get(j).contains("Div")){
															Resultadofinal =  dividir(expresion.get(j-1), expresion.get(j+1));
															expresion2.set(j,"aux"+contador);
															arbol.add(new Arbol_CodInt("/",expresion2.get(j-1),expresion2.get(j+1),expresion2.get(j)));
															
															expresion2.remove(j+1);
															expresion2.remove(j-1);
															
															expresion.set(j,Resultadofinal+"" );
															expresion.remove(j+1);
															expresion.remove(j-1);
															
															aux6 = aux6 - 2;
															contador++;
															setResultadofinal(Resultadofinal);
															System.out.println(Resultadofinal);
															RFD = Resultadofinal;
														}
														 if (expresion.get(j).contains("Multi")){
															Resultadofinal =  multiplicar(expresion.get(j-1), expresion.get(j+1));
															expresion2.set(j,"aux"+contador);
															arbol.add(new Arbol_CodInt("*",expresion2.get(j-1),expresion2.get(j+1),expresion2.get(j)));
															expresion2.remove(j+1);
															expresion2.remove(j-1);
															
															expresion.set(j,Resultadofinal+"" );
															expresion.remove(j+1);
															expresion.remove(j-1);
															aux6 = aux6 - 2;

															contador++;
															setResultadofinal(Resultadofinal);
															System.out.println(Resultadofinal);
															RFM = Resultadofinal;
														}
														 contador++;
													}
													
													 if (expresion.get(i+2).contains("Suma")){
														Resultadofinal =  Sumar(expresion.get(i+1), expresion.get(i+3));
														expresion2.set(i+2,"aux"+contador);
														arbol.add(new Arbol_CodInt("+",expresion2.get(i+1),expresion2.get(i+3),expresion2.get(i+2)));
														expresion2.remove(i+3);
														expresion2.remove(i+1);
														
														expresion.set(i+1,Resultadofinal+"" );
														expresion.remove(i+2);
														expresion.remove(i+2);
														contador++;
														setResultadofinal(Resultadofinal);
														System.out.println(Resultadofinal);
														RFS = Resultadofinal;
													}
													 if (expresion.get(i+2).contains("Resta")){
														Resultadofinal =  Restar(expresion.get(i+1), expresion.get(i+3));
														expresion2.set(i+2,"aux"+contador);
														arbol.add(new Arbol_CodInt("-",expresion2.get(i+1),expresion2.get(i+3),expresion2.get(i+2)));
														expresion2.remove(i+3);
														expresion2.remove(i+1);
														
														expresion.set(i+1,Resultadofinal+"" );
														expresion.remove(i+2);
														expresion.remove(i+2);
									
														contador++;
														setResultadofinal(Resultadofinal);
														System.out.println(Resultadofinal);
														RFR = Resultadofinal;
													}
													


													if(expresion.get(i+2).contains("ParCerrado"))	{
														expresion.remove(i+2);
														expresion.remove(i);
														expresion2.remove(i+2);
														expresion2.remove(i);
														banderaParentesis = true;
													}
												}							
											}
										}
								}
								
									
									for (int i = 0; i < expresion.size(); i++) {

									if(expresion.get(i).contains("Multi") || expresion.get(i).contains("Div")){
										
										
										if (expresion.get(i).contains("Multi")){
											Resultadofinal =  multiplicar(expresion.get(i-1), expresion.get(i+1));
											
											expresion2.set(i,"aux"+contador);
										

											arbol.add(new Arbol_CodInt("*",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
											expresion2.remove(i+1);
											expresion2.remove(i-1);
											
											expresion.set(i-1,Resultadofinal+"" );
											expresion.remove(i);
											expresion.remove(i);
											
											
											i--;
											contador++;
											setResultadofinal(Resultadofinal);
											System.out.println(Resultadofinal);
											RFM = Resultadofinal;
										}
										else if (expresion.get(i).contains("Div")){
											Resultadofinal =  dividir(expresion.get(i-1), expresion.get(i+1));
											
											expresion2.set(i,"aux"+contador);
										

											arbol.add(new Arbol_CodInt("/",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
											expresion2.remove(i+1);
											expresion2.remove(i-1);
											
											expresion.set(i-1,Resultadofinal+"" );
											expresion.remove(i);
											expresion.remove(i);
											
											
											i--;
											contador++;
											setResultadofinal(Resultadofinal);
											System.out.println(Resultadofinal);
											RFD = Resultadofinal;
										}
									
									}
									
									
								}
		
								
								for (int i = 0; i < expresion.size(); i++) {
									
									if(expresion.get(i).contains("Suma") || expresion.get(i).contains("Resta")){
										
										if (expresion.get(i).contains("Suma")){

											Resultadofinal =  Sumar(expresion.get(i-1), expresion.get(i+1));
										
											expresion2.set(i,"aux"+contador);
											
											
											arbol.add(new Arbol_CodInt("+",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
											expresion2.remove(i+1);
											expresion2.remove(i-1);
											
											expresion.set(i-1,Resultadofinal+"" );
											expresion.remove(i);
											expresion.remove(i);
											i--;
											contador++;
											setResultadofinal(Resultadofinal);
											System.out.println(Resultadofinal);
											RFS = Resultadofinal;
										}
										
										else if (expresion.get(i).contains("Resta")){
											if(expresion.get(i).contains("Resta")){
												Resultadofinal =  Restar(expresion.get(i-1), expresion.get(i+1));
									
												expresion2.set(i,"aux"+contador);
												
												
												arbol.add(new Arbol_CodInt("-",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
												expresion2.remove(i+1);
												expresion2.remove(i-1);
												
												expresion.set(i-1,Resultadofinal+"" );
												expresion.remove(i);
												expresion.remove(i);
												i--;
												contador++;
												setResultadofinal(Resultadofinal);
												System.out.println(Resultadofinal);
												RFR = Resultadofinal;
											}
											
										}
										
									}

										
									
								}
										int Tipo, nombre;
										String auxTipo ="", auxNombre = "";
										while(nodoaux2!=null){
											Tipo = nodoaux2.anterior.dato.getTipo();
											System.out.println(Tipo);
											if(Tipo==2 ){
												auxTipo = nodoaux2.anterior.dato.getValor();
												auxNombre = nodoaux2.dato.getValor();
												break;

											}
											
											nodoaux2 = nodoaux2.anterior;
										}
									expresion.remove(0);		
						}


							else if (Anterior3Tipo==Token.IDENTIFICADOR
									&&Anterior2Tipo==Token.SIMBOLO
									&&Anterior1Tipo==Token.CONSTANTE)
							{



								for (int i = 0; i < tablasimbolos.size(); i++) {
									if(tablasimbolos.get(i).getNombre().contains(Anterior3Valor)){
										tablasimbolos.get(i).setValor(Anterior1Valor);
										banderita=true;
									}
								}

								if(!banderita){
									banderaErroresSintacticos = true;
									impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un Tipo de Dato");
									JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un Tipo de Dato",
											"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
								}

							}

							else if (Anterior3Tipo==Token.IDENTIFICADOR
									&&Anterior2Tipo==Token.SIMBOLO
									&&Anterior1Tipo==Token.CONSTANTE)
							{



								for (int i = 0; i < tablasimbolos.size(); i++) {
									if(tablasimbolos.get(i).getNombre().contains(Anterior3Valor)){
										tablasimbolos.get(i).setValor(Anterior1Valor);
										banderita=true;
									}
								}

								if(!banderita){
									banderaErroresSintacticos = true;
									impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un Tipo de Dato");
									JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un Tipo de Dato",
											"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
								}

							}



						} catch (Exception e){
							System.out.println(e.getMessage());
						}



					}


					break;

				case Token.CONSTANTE:
					if(Anterior1Valor.equals("="))
						if(Siguiente1Tipo!=Token.OPERADOR_ARITMETICO
						&&!Siguiente1Valor.equals(";")){
							banderaErroresSintacticos = true;
							impresion.add("Error sint�ctico en l�nea "+to.getLinea()+ " la asignaci�n no es v�lida");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en l�nea "+to.getLinea()+ " la asignaci�n no es v�lida",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}


					break;
				case Token.PALABRA_RESERVADA:





					if(to.getValor().equals("if"))
					{
						if(!Siguiente1Valor.equals("(")) {
							banderaErroresSintacticos = true;
							impresion.add("Error sint�ctico en l�nea "+to.getLinea()+ " se esperaba un (");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en l�nea "+to.getLinea()+ " se esperaba un (",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}


					}
					else 
					{
						// si es un else, buscar en los anteriores y si no hay un if ocurrira un error
						NodoDoble<Token> aux = nodo.anterior;
						boolean bandera=false;
						while(aux!=null&&!bandera) {
							if(aux.dato.getValor().equals("if"))
								bandera=true;
							aux =aux.anterior;
						}
						if(!bandera){
							banderaErroresSintacticos = true;
							impresion.add("Error sint�ctico en l�nea "+to.getLinea()+ " else inv�lido");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en l�nea "+to.getLinea()+ " else inv�lido(",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}
					}
					break;
				case Token.OPERADOR_LOGICO:
					// verificar que sea  'numero' + '==' + 'numero' 
					if (to.getValor().equals("==")){
						if (Anterior3Tipo!=Token.PALABRA_RESERVADA){
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba una palabra reservada (if)");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba una palabra reservada (if)",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}

						if (Anterior2Tipo!=Token.SIMBOLO){
							banderaErroresSintacticos = true;
							impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un simbolo");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un s�mbolo",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}
						if (!Siguiente2Valor.contains(")")){
							banderaErroresSintacticos = true;
							impresion.add("Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un s�mbolo");
							JOptionPane.showMessageDialog(null,"Error sint�ctico en la l�nea "+to.getLinea()+ " se esperaba un s�mbolo",
									"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
						}
					}
					if(Anterior1Tipo!=Token.CONSTANTE && Anterior1Tipo!=Token.IDENTIFICADOR  ) {
						banderaErroresSintacticos = true;
						impresion.add("Error sint�ctico en l�nea "+to.getLinea()+ " se esperaba una constante o un identificador");
						JOptionPane.showMessageDialog(null,"Error sint�ctico en l�nea "+to.getLinea()+ " se esperaba una constante o un identificador",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
					}
					if(Siguiente1Tipo!=Token.CONSTANTE && Siguiente1Tipo!=Token.IDENTIFICADOR ){
						banderaErroresSintacticos = true;
						impresion.add("Error sint�ctico en l�nea "+to.getLinea()+ " se esperaba una una constante o un identificador");
						JOptionPane.showMessageDialog(null,"Error sem�ntico en l�nea "+to.getLinea()+ "se esperaba una constante o un identificador.",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
					}
					
					//VALIDAR OPERANDOS DE TIPOS COMPATIBLES

					String operando1,operando2;

					if (Anterior1Tipo==Token.IDENTIFICADOR)
					{
						String valor="";
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if (tablasimbolos.get(i).getNombre().equals(Anterior1Valor))
								valor = tablasimbolos.get(i).getValor();
						}
						operando1= TipoCadena(valor);

					}else
					operando1= TipoCadena(Anterior1Valor);
					
					if (Siguiente1Tipo==Token.IDENTIFICADOR)
					{
						String valor="";
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if (tablasimbolos.get(i).getNombre().equals(Siguiente1Valor))
								valor = tablasimbolos.get(i).getValor();
						}
						operando2= TipoCadena(valor);

					}else
					operando2= TipoCadena(Siguiente1Valor);
					

					if(!operando1.contains(operando2)){
						banderaErroresSemanticos=true;
						impresion.add("Error sem�ntico en l�nea "+to.getLinea()+ ", no coinciden los tipos de los operandos ("+operando1+"/"+operando2+")");
						JOptionPane.showMessageDialog(null,"Error sem�ntico en l�nea "+to.getLinea()+ ", no coinciden los tipos de los operandos ("+operando1+"/"+operando2+")",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
					}



					break;

				case Token.OPERADOR_ARITMETICO:

					String operando3,operando4;

					if (Anterior1Tipo==Token.IDENTIFICADOR)
					{
						String valor="";
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if (tablasimbolos.get(i).getNombre().equals(Anterior1Valor))
								valor = tablasimbolos.get(i).getValor();
						}
						operando3= TipoCadena(valor);

					}else
					operando3= TipoCadena(Anterior1Valor);
					if(operando3.equals(""))
						operando3= "int";
					
					if (Siguiente1Tipo==Token.IDENTIFICADOR)
					{
						String valor="";
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if (tablasimbolos.get(i).getNombre().equals(Siguiente1Valor))
								valor = tablasimbolos.get(i).getValor();
						}
						operando4= TipoCadena(valor);

					}else
					operando4= TipoCadena(Siguiente1Valor);
					

					if(!operando3.contains(operando4)){
//						AppCompilador.enviarErrorSemantico(to.getLinea());
						banderaErroresSemanticos=true;
						impresion.add("Error sem�ntico en l�nea "+to.getLinea()+ ", no coinciden los tipos de los operandos ("+operando3+"/"+operando4+")");
						JOptionPane.showMessageDialog(null,"Error sem�ntico en l�nea "+to.getLinea()+ ", no coinciden los tipos de los operandos ("+operando3+"/"+operando4+")",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
					}



					break;

				}



			}catch (Exception e)
			{
				System.out.println(e.getMessage());
			}



			analisisSintactio(nodo.siguiente);
			return to;
		}
		return  vacio;// para no regresar null y evitar null pointer
	}

	public  Token AnalizadorSemantico (NodoDoble<Token> nodo){

		//VALIDAR LA ASIGNACI�N A UNA VARIABLE
		
		Token  to;
		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;


			String aux;
			String aux2,auxiliarTipo = "";
			int aux3,renglon;


			for (int i = 0; i < tablasimbolos.size(); i++) {

				aux = tablasimbolos.get(i).tipo;
				renglon = tablasimbolos.get(i).getRenglon();

				if(aux.contains("int")){
					aux2=tablasimbolos.get(i).getValor();
					if(!aux2.isEmpty())
						auxiliarTipo =TipoCadena(aux2);

					if (EsNumeroEntero(aux2) == false && !aux2.isEmpty()) {
						banderaErroresSemanticos=true;
						impresion.add("Error sem�ntico en la l�nea "+renglon+ ", se recibi� un "+auxiliarTipo+ " y se esperaba un int");
						JOptionPane.showMessageDialog(null,"Error sem�ntico en la l�nea "+renglon+ ", se recibi� un "+auxiliarTipo+ " y se esperaba un int",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);

					} 
				}
				else if(aux.contains("float")){

					aux2=tablasimbolos.get(i).getValor();
					if(!aux2.isEmpty())
						auxiliarTipo =TipoCadena(aux2);

					if (Esfloat(aux2) == false && !aux2.isEmpty()) {
						banderaErroresSemanticos=true;
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibi� un "+auxiliarTipo+ " y se esperaba un float");
						JOptionPane.showMessageDialog(null,"Error sem�ntico en la l�nea "+renglon+ ", se recibi� un "+auxiliarTipo+ " y se esperaba un float",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
					} 


				}
				else if(aux.contains("Char")){

					aux2=tablasimbolos.get(i).getValor();
					if(!aux2.isEmpty())
						auxiliarTipo =TipoCadena(aux2);

					if (EsChar(aux2) == false && !aux2.isEmpty()) {
//						AppCompilador.enviarErrorSemantico(renglon);
						banderaErroresSemanticos=true;
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibi� un "+auxiliarTipo+ " y se esperaba un char");
						JOptionPane.showMessageDialog(null,"Error sem�ntico en la l�nea "+renglon+ ", se recibi� un "+auxiliarTipo+ " y se esperaba un char",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
					} 




				}
				else if(aux.contains("boolean")){

					aux2=tablasimbolos.get(i).getValor();
					if(!aux2.isEmpty())
						auxiliarTipo =TipoCadena(aux2);

					if (EsBoolean(aux2) == false && !aux2.isEmpty() ) {
//						AppCompilador.enviarErrorSemantico(renglon);
						banderaErroresSemanticos=true;
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibi� un "+auxiliarTipo+ " y se esperaba un boolean");
						JOptionPane.showMessageDialog(null,"Error sem�ntico en la l�nea "+renglon+ ", se recibi� un "+auxiliarTipo+ " y se esperaba un boolean",
								"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
					} 
				}
			}
		}

		return vacio;	
	}
	public void GenerarCodigoIntermedio(ArrayList<Arbol_CodInt> tabla) {
		System.out.println("CODIGO INTERMEDIO");
		codigo_datos = new ArrayList<String>();
		codigo_datos.add("                    .MODEL                   small");
		codigo_datos.add("                    .DATA ");
		//DECLARAR VARIABLES
		for (Arbol_CodInt item : getTabla2()) {
			codigo_datos.add(item.resultado+"               DW                  0");
		}
		codigo_datos.add("                    .CODE");
		codigo_datos.add("MAIN            PROC                     FAR");
		codigo_datos.add("                    .STARTUP");
		//dataCodigo.add("                    ;OPERACION = ''");
		// OPERACIONES
		for (int i=0; i < getTabla2().size(); i++) {
			Arbol_CodInt id2 = getTabla2().get(i);								
			System.out.println("Item: "+"[ "+id2.operador+", "+id2.argumento1+", "+id2.argumento2+", "+id2.resultado+" ]");
			if(id2.operador.equals("+")) {
				operation = "ADD";
				codigo_datos.add("                    ;SUMA");
			}else if (id2.operador.equals("-")) {
				operation = "SUB";
				codigo_datos.add("                    ;RESTA");
			}else if (id2.operador.equals("*")) {
				operation = "MUL";
				codigo_datos.add("                    ;MULTIPLICACION");
			}else if (id2.operador.equals("/")) {
				operation = "DIV";
				codigo_datos.add("                    ;DIVISION");
			}else if (id2.operador.equals("=")) {
				codigo_datos.add("                    ;ASIGNACION");
			}
			if(!id2.operador.equals("=") && ( operation.equals("MUL") || operation.equals("DIV") )) {
				codigo_datos.add("                    MOV	 AX,"+id2.argumento1);
				codigo_datos.add("                    MOV	 BX,"+id2.argumento2);
				codigo_datos.add("                    "+operation+" BX");
				codigo_datos.add("                    MOV	 "+id2.resultado+", AX");
			}
			else if(!id2.operador.equals("=") && ( operation.equals("ADD") || operation.equals("SUB") )) {
				codigo_datos.add("                    MOV	 AX,"+id2.argumento1);
				codigo_datos.add("                    MOV	 BX,"+id2.argumento2);
				codigo_datos.add("                    "+operation+" AX, BX");
				codigo_datos.add("                    MOV	 "+id2.resultado+", AX");
			}
			else {
				codigo_datos.add("                    MOV	 AX,"+id2.argumento1);
				codigo_datos.add("                    MOV "+id2.resultado+", AX");
			}
		}
		codigo_datos.add("MAIN            ENDP");
		// IMPRESION
		for (String item : codigo_datos) {
			System.out.println(item);
		}
		
	}
	

	public Token Semantico2(NodoDoble<Token> nodo) {
		Token  to;
		
		//VALIDAR LAS VARIABLES USADAS Y NO DECLARADAS

		
		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;


			if(to.getTipo()==Token.IDENTIFICADOR){
				String auxiliar = to.getValor();
				boolean bandera2 = false;

				for (int i = 0; i < tablasimbolos.size(); i++) {

					if(tablasimbolos.get(i).getNombre().equals(auxiliar)){
						bandera2=true;
					}
				}

				if(!bandera2){
					banderaErroresSemanticos=true;
					impresion.add("Error sem�ntico en l�nea "+to.getLinea()+ " se uso la variable "+auxiliar+" no est� declarada");
					JOptionPane.showMessageDialog(null,"Error sem�ntico en l�nea "+to.getLinea()+ " se uso la variable "+auxiliar+" no est� declarada",
							"AVISO DE APLICACI�N",	JOptionPane.ERROR_MESSAGE);
				}


			}

			Semantico2(nodo.siguiente);
			return to;
		}
		return vacio;
	}


	public Token VerificarClase(NodoDoble<Token> nodo) {
		Token  to;


		if(banderaclase){
			return vacio;
		}

		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;


			if(to.getTipo()==Token.CLASE){

				banderaclase= true;

			}

			VerificarClase(nodo.siguiente);
			return to;
		}
		return vacio;
	}

	public static boolean EsNumeroEntero(String cadena) {

		boolean resultado;

		try {
			Integer.parseInt(cadena);
			resultado = true;
		} catch (NumberFormatException excepcion) {
			resultado = false;
		}

		return resultado;
	}

	public static boolean Esfloat(String cadena) {

		boolean resultado;

		try {
			Float.parseFloat(cadena);
			resultado = true;
		} catch (NumberFormatException excepcion) {
			resultado = false;
		}

		return resultado;
	}


	public static boolean EsChar(String cadena) {

		boolean resultado;

		if(Pattern.matches("'[a-zA-Z]'",cadena))
			return true;
		return false;

	}


	public static boolean EsBoolean(String cadena) {


		if(cadena.contains("true")||cadena.contains("false"))
			return true;
		return false;

	}


	public static String TipoCadena(String cadena) {

		String resultado= "";

		if(Pattern.matches("[0-9]+",cadena)){
			resultado = "int";
			return resultado;
		}

		if(Pattern.matches("[0-9]+.[0-9]+",cadena)){
			resultado = "float";
		}


		if(Pattern.matches("'[a-zA-Z]'",cadena)){
			resultado = "char";
		}

		if(cadena.contains("true")||cadena.contains("false")){
			resultado = "boolean";
		}

		return resultado;
	}



	// por si alguien escribe todo pegado 
	public String separaDelimitadores(String linea){
		for (String string : Arrays.asList("(",")","{","}","=",";")) {
			if(string.equals("=")) {
				if(linea.indexOf(">=")>=0) {
					linea = linea.replace(">=", " >= ");
					break;
				}
				if(linea.indexOf("<=")>=0) {
					linea = linea.replace("<=", " <= ");
					break;
				}
				if(linea.indexOf("==")>=0)
				{
					linea = linea.replace("==", " == ");
					break;
				}
			}
			if(linea.contains(string)) 
				linea = linea.replace(string, " "+string+" ");
		}
		return linea;
	}
	public int cuenta (String token) {

		int conta=0;
		NodoDoble<Token> Aux=tokens.getInicio();
		while(Aux !=null){
			if(Aux.dato.getValor().equals(token))
				conta++;
			Aux=Aux.siguiente;
		}	
		return conta;
	}
	public ArrayList<String> getmistokens() {
		return impresion;
	}
	
	public int Sumar (String uno, String dos){

		int suma =0;

		suma = suma+Integer.parseInt(uno)+Integer.parseInt(dos);


		return suma;
	}
	
	public int Restar (String uno, String dos){

		int Resta =0;

		Resta = Resta+Integer.parseInt(uno)-Integer.parseInt(dos);


		return Resta;
	}
	
	public int multiplicar (String uno, String dos){

		int multi =0;

		multi = multi+Integer.parseInt(uno)*Integer.parseInt(dos);


		return multi;
	}
	
	public int dividir (String uno, String dos){

		int div =0;

		div = div+ (int)( Integer.parseInt(uno)/Integer.parseInt(dos));


		return div;
	}

	public int getResultadofinal() {
		return Resultadofinal;
	}

	public void setResultadofinal(int resultadofinal) {
		Resultadofinal = resultadofinal;
	}

	public int getRFS() {
		return RFS;
	}

	public void setRFS(int rFS) {
		RFS = rFS;
	}

	public int getRFR() {
		return RFR;
	}

	public void setRFR(int rFR) {
		RFR = rFR;
	}

	public int getRFD() {
		return RFD;
	}

	public void setRFD(int rFD) {
		RFD = rFD;
	}

	public int getRFM() {
		return RFM;
	}

	public void setRFM(int rFM) {
		RFM = rFM;
	}
	
	

}
