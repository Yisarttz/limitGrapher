package interfaz;

//importamos (JFrame) para los botones y las cajas de texto de la ventana
import javax.swing.*;    
//importamos colores, fuentes y diseños para la pantalla
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

//importamos Expression para evaluar y resolver expresiones matemáticas
//escritas en formato de cadena de texto
import net.objecthunter.exp4j.Expression;
//importamos la clase ExpressionBiulder para evaluar las expresiones 
//matemáticas escritas como cadena de texto
import net.objecthunter.exp4j.ExpressionBuilder;

public class AnalisisLimitesApp extends JFrame {

	//COMPONENTES VISUALES
	//
	//Cuadro de texto para que el usuario ingrese la función
	private JTextField campoFuncion;
	//caja de texto para introducir el valor de C
	private JTextField campoPuntoC;
	//creamos un botón interactivo para la ventana
	private JButton botonGraficar;
	//creamos una etiqueta estática para mostrar los resultados 
	//de epsilon y delta
	private JLabel labelEstado;
	//clase PanelGrafico
	private PanelGrafico panelGrafico;
	//Componente para el desarrollo paso a paso
	private JTextArea areaProcedimiento;
	// Cuadro dinámico para la imagen
	private JLabel labelImagenTeorema;
	
	//CONSTRUCTOR
	public AnalisisLimitesApp () {
		
		//establecemos el título de la ventana 
		setTitle("Limit Grapher");
		//establecemos las dimensiones de la ventana
		setSize(1280, 720);
		//Configuramos el cierre de la ventana con el botón de
		// salir  "[x]" (CLOSE)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//usamos el metodo LocationRelativeTo(null) para centrar la 
		//ventana en el medio de la pantalla
		setLocationRelativeTo(null);
		//Configuramos un contenedor para que organice los componentes 
		//gráficos
		setLayout(new BorderLayout());
		
		//Instanciamos un objeto contenedor de la clase JPanel, en el 
		//cual instanciamos un nuevo objeto de diseño FlowLayout para 
		//la organización de los componentes de izquierda a derecha
		JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel labelLimite= new JLabel("<html><div style='text-align: center;"
				+ "'>lim<br><span style='font-size: 9px;'>x → c</span></div></html>");
		labelLimite.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		//instanciamos un objeto anónimo de la clase JLabel (etiqueta de 
		//texto estática) y lo agregamos directamente al contenedor panelControles
		panelControles.add(labelLimite);
		panelControles.add(new JLabel ("|  lim    f(x) = "));
		//instanciamos un objeto y le asignamos una variable,con texto y el ancho
		//visible de su campo
		campoFuncion= new JTextField("x^2", 12);
		//agregamos un componente visual al contenedor
		panelControles.add(campoFuncion);
		
		//instanciamos un objeto anónimo de la clase JLabel (etiqueta de 
		//texto estática) y lo agregamos directamente al contenedor panelControles
		panelControles.add(new JLabel("  donde c = "));
		//instanciamos un objeto y le asignamos una variable,con texto y el ancho
		//visible de su campo
		campoPuntoC = new JTextField("2", 5);
		//Composición de la GUI, inyectamos un componente en el contenedor secundario
		panelControles.add(campoPuntoC);
		
		//instanciamos un objeto y le asignamos una variable con texto
		botonGraficar = new JButton("Calcular y Graficar");
		//Composición de la GUI, inyectamos un componente en el contenedor secundario
		panelControles.add(botonGraficar);
		//integramos el panel de controles en el contenedor principal de la aplicación
		//asegurando que la barra de tareas y los imputs queden fijos en la parte superior.
		add(panelControles, BorderLayout.NORTH);
		
		//---GRÁFICA---
		//Instanciamos un objeto para la gráfica de  los límites
		panelGrafico = new PanelGrafico();
		//Lo centramos en la ventana
		add(panelGrafico, BorderLayout.CENTER);
		
		//Instanciamos un pobjeto para la etiqueta inferior de los resultados
		labelEstado= new JLabel(" Ingrese una función, al finalizar presione < Calcular y graficar >");
		//Instanciamos un objeto de la clase Font, con tres parámetros (tipo de letra, negrita, tamaño)
		labelEstado.setFont(new Font("DialogInput", Font.BOLD, 14));
		//lo colocamos en la parte inferior de  la ventana
		add(labelEstado, BorderLayout.SOUTH);
		
		//---Panel de Desarrollo Matemático---
		JPanel panelDere = new JPanel(new BorderLayout());
		panelDere.setPreferredSize(new Dimension(350, 0)); // Un ancho elegante de 350 píxeles
		panelDere.setBorder(BorderFactory.createTitledBorder("●ᴥ●  Desarrollo Analítico Paso a Paso ●ᴥ●"));
		
		areaProcedimiento = new JTextArea();
		areaProcedimiento.setEditable(false);
		areaProcedimiento.setFont(new Font("Monospaced", Font.PLAIN, 12));
		areaProcedimiento.setBackground(new Color(245, 245, 250)); // Fondo gris claro pastel muy limpio
		areaProcedimiento.setMargin(new Insets(10, 10, 10, 10));
		//Salto de línea automático
		areaProcedimiento.setLineWrap(true);
		areaProcedimiento.setWrapStyleWord(true);
		//Scroll para visualizar el contenido completo
		JScrollPane ScrollProcedimiento = new JScrollPane(areaProcedimiento);
		panelDere.add(ScrollProcedimiento, BorderLayout.CENTER);
		//Visor adicional para la imagen
		labelImagenTeorema = new JLabel();
		labelImagenTeorema.setHorizontalAlignment(SwingConstants.CENTER);
		labelImagenTeorema.setBorder(BorderFactory.createTitledBorder("●ᴥ●  Teorema del Sánduche  ●ᴥ●"));
		labelImagenTeorema.setVisible(false); // Oculto al inicio
		// Ajustamos el tamaño del contenedor de la imagen 
		labelImagenTeorema.setPreferredSize(new Dimension(350, 250)); 
		// Cargamos la imagen
		try {
		    ImageIcon imgIcon = new ImageIcon(getClass().getResource("/imagenes/circulo_unitario.png"));
		    // Escalamos la imagen de forma fluida para que se ajuste al ancho
		    Image imgEscalada = imgIcon.getImage().getScaledInstance(320, 210, Image.SCALE_SMOOTH);
		    labelImagenTeorema.setIcon(new ImageIcon(imgEscalada));
		} catch (Exception ex) {
		    labelImagenTeorema.setText("[Imagen del Círculo Unitario]");
		}
		// Añadimos el visor en la parte inferior del panel derecho
		panelDere.add(labelImagenTeorema, BorderLayout.SOUTH);
		//Insertamos la región
		add(panelDere, BorderLayout.EAST);
		
		//---Listener---
		//Registro de un Event Listener mediante una clase anónima interna
		//Capturamos el evento de acción (click) sobre el botón de graficación
		botonGraficar.addActionListener(new ActionListener() {
			@Override //sobreescribimos el método de la interfaz ActionListener
			public void actionPerformed(ActionEvent e) {
				//este método limpiará y actualizará la gráfica
				actualizarGrafico();
			}	
		});
		//Hacemos visible la pantalla para el usuario
		setVisible(true);
	} //Fin Constructor
	
	
	//LÓGICA DEL ÁLGEBRA DE LÍMITES Y ACTUALIZACIÓN DE LA GRÁFICA
	private void actualizarGrafico() {
	    // Usamos un try para capturar errores en la conversión de datos
	    try {
	        // Invocamos un método getter del objeto "campoFuncion" y del objeto "campoPuntoC"
	    	String textoFuncion = campoFuncion.getText().trim();
	        String textoC = campoPuntoC.getText().trim();
	        //Ocultamos el cuadro de la Imagen por defecto en cada nuevo cálculo
	        if (labelImagenTeorema != null) {
	            labelImagenTeorema.setVisible(false);
	        }
	        
	        if(textoFuncion.isEmpty() || textoC.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Complete todos los campos requeridos", "Campos vacíos", 
	                    JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        
	        // Convertimos el dato ingresado a double para el análisis matemático y gráfico
	        String textoCEval = textoC.trim().toLowerCase();
			double C;

			if (textoCEval.equals("inf") || textoCEval.equals("∞") || textoCEval.equals("infinity")) {
				C = Double.POSITIVE_INFINITY;
			} else if (textoCEval.equals("-inf") || textoCEval.equals("-∞") || textoCEval.equals("-infinity")) {
				C = Double.NEGATIVE_INFINITY;
			} else {
				try {
					C = Double.parseDouble(textoCEval);
				} catch (NumberFormatException e) {
					C = 0.0; // Valor por defecto seguro si escriben cualquier otra cosa
					System.err.println("Error: No se pudo parsear el punto C, asignando 0.0");
				}
			}

	        // Variables de control para nuestro analizador de patrones inteligente
	        boolean esPatronTrig = false;
	        boolean esPatronRaiz = false;
	        boolean esPatronFactorizable = false;
	        String desarrolloStepByStep = "";
	        String desarrolloTrigStepByStep = "";
	        
	        // NORMALIZACIÓN ABSOLUTA: Evita errores redundantes entre sqr y sqrt
	        String funcionNormalizada = textoFuncion.replace(" ", "");
	        if (funcionNormalizada.contains("sqrt")) {
	            // Si ya contiene sqrt, lo dejamos intacto
	        } else {
	            funcionNormalizada = funcionNormalizada.replace("sqr", "sqrt");
	        }
	        
	        // El texto que va al motor evaluador siempre debe llevar la sintaxis correcta 'sqrt'
	        String funcionParaEvaluar = funcionNormalizada;
	        double resultadoLimite = Double.NaN;
	        
	        
	        String regexCosSin = "1-cos\\((\\d+)\\*x\\).*sin\\((\\d+)\\*x\\)";
	        java.util.regex.Pattern patronTrig = java.util.regex.Pattern.compile(regexCosSin);
	        java.util.regex.Matcher buscadorTrig = patronTrig.matcher(funcionNormalizada);
	        
	        if (Double.isInfinite(C)) {
	            // CASO INFINITO:
	            labelEstado.setText(" lim  f(x) = L | x → " + (C > 0 ? "∞" : "-∞"));
	            labelEstado.setForeground(new Color(0, 127, 255)); 

	            StringBuilder procedimiento = new StringBuilder();
		        procedimiento.append("Dada la función:\n f(x) = ").append(textoFuncion).append("\n\n");
		        procedimiento.append("Se evalúa el límite cuando x → ∞\n");
	            procedimiento.append("=========================================\n");
	            procedimiento.append("     ♦ CÁLCULO DEL LÍMITE AL INFINITO ♦\n");
	            procedimiento.append("=========================================\n\n");
	            procedimiento.append("▶ I. ANÁLISIS DE COMPORTAMIENTO\n"
	            		           + "     ASÍNTOTICO\n\n")
	                         .append("   Se evalúa la tendencia global de la\n"
	                         	   + "   función cuando la variable x crece sin\n")
	                         .append("   límite (x → ∞).\n\n")
	                         .append(" - Análisis por Grados de Potencia:\n")
	                         .append("   Para funciones racionales del tipo\n"
	                         	   + "               P(x)\n"
	                         	   + "              ———\n"
	                         	   + "               Q(x),\n")
	                         .append("   el límite depende del grado de los\n"
	                         	   + "   polinomios:\n")
	                         .append("   • Si Gr(P) = Gr(Q)\n"
	                         		+ "    L = Coeficientes Principales.\n")
	                         .append("   • Si Gr(P) < Gr(Q)\n"
	                         		+ "    L = 0.\n")
	                         .append("   • Si Gr(P) > Gr(Q)\n"
	                         		+ "    L = ±∞.\n\n");
	            // ADAPTACIÓN DE EVALUACIÓN: Usamos el método nativo de tu app
	            double valorEvaluadoEnInfinito = panelGrafico.evaluarFuncion(10000.0); 
	            String stringResultadoL;
	            
	            if (Double.isNaN(valorEvaluadoEnInfinito) || Math.abs(valorEvaluadoEnInfinito) > 1000.0) {
	                stringResultadoL = "±∞ (No converge)";
	                labelEstado.setText(" lim  f(x) = ±∞ | x → " + (C > 0 ? "∞" : "-∞"));
	            } else {
	                stringResultadoL = String.format("%.4f", valorEvaluadoEnInfinito);
	                labelEstado.setText(" lim  f(x) = " + stringResultadoL + " | x → " + (C > 0 ? "∞" : "-∞"));
	            }

	            procedimiento.append("=========================================\n")
	                         .append(" CONCLUSIÓN:\n")
	                         .append(" El comportamiento asintótico horizontal\n"
	                         	   + " converge a:\n")
	                         .append("  L = ").append(stringResultadoL).append("\n")
	                         .append("=========================================\n");
	            
	            desarrolloStepByStep = procedimiento.toString();
	            areaProcedimiento.setText(desarrolloStepByStep);
	            
	            try {
	                // Construimos la expresión usando el builder de exp4j
	                net.objecthunter.exp4j.Expression expr = new net.objecthunter.exp4j.ExpressionBuilder(funcionParaEvaluar)
	                        .variable("x")
	                        .build();
	                panelGrafico.funcionIngresada = expr; 
	                
	            } catch (Exception e) {
	                System.err.println("Error al parsear la expresión en el bloque infinito: " + e.getMessage());
	            }
	            panelGrafico.puntoC = C;
	            panelGrafico.limiteCalculadoExterno = valorEvaluadoEnInfinito;
	            panelGrafico.revalidate(); 
	            panelGrafico.repaint();
	            return;
	         // --- MOTOR 1: ANALIZADOR DE PATRONES TRIGONOMÉTRICOS ---
	        } else if (buscadorTrig.find() && C == 0) {
	            esPatronTrig = true;
	            int kCoef = Integer.parseInt(buscadorTrig.group(1));
	            int mCoef = Integer.parseInt(buscadorTrig.group(2));
	            
	            resultadoLimite = 0.0;
	            
	            StringBuilder sbTrig = new StringBuilder();
	            sbTrig.append("=========================================\n");
	            sbTrig.append("    DESARROLLO ANALÍTICO FORMAL DE LÍMITES\n");
	            sbTrig.append("=========================================\n\n");
	            sbTrig.append("▶ PASO 1: ANÁLISIS DE DOMINIO REAL\n");
	            sbTrig.append("  • Restricción:\n"
	            		    + "    sin(").append(mCoef).append("x) ≠ 0\n");
	            sbTrig.append("  • Puntos de exclusión:\n"
	            		    + "    x ≠ k·π / ").append(mCoef).append(" (con k ∈ ℤ)\n");
	            sbTrig.append("  • Dom(f) =\n"
	            		    + "    ℝ - { k·π / ").append(mCoef).append(" }\n");
	            sbTrig.append("  Dado que c = 0 es un punto de acumulación\n"
	            		    + "  excluido, es totalmente válido evaluar el\n");
	            sbTrig.append("  comportamiento del límite.\n\n"); 
	            sbTrig.append("----------------------------------------\n");
	            sbTrig.append("▶ PASO 2: EVALUACIÓN DIRECTA E INDETERMINACIÓN\n");
	            sbTrig.append("  Sustitución directa en x = 0:\n");
	            sbTrig.append("  f(0) = (1 - cos(").append(kCoef).append("*0)) / sin(").append(mCoef).append("*0)\n");
	            sbTrig.append("  f(0) = (1 - 1) / 0 = [0 / 0]\n\n");
	            sbTrig.append("  Conclusión:\n"
	            		    + "  Se detecta una Indeterminación de la forma [0 / 0].\n\n");
	            sbTrig.append("----------------------------------------\n");
	            sbTrig.append("▶ PASO 3: REESTRUCTURACIÓN POR LÍMITES NOTABLES\n"
	            		    + "           Y AJUSTE DE COEFICIENTES Y EVALUACIÓN\\n");
	            sbTrig.append("  = ").append(kCoef).append(" * lim [ (1-cos(").append(kCoef).append("x)) / (").append(kCoef)
	            .append("x) ] * (1/").append(mCoef).append(") * lim [ (").append(mCoef).append("x) / sin(").append(mCoef).append("x) ]\n\n");
	            sbTrig.append("  Aplicando los teoremas:\n");
	            sbTrig.append("  = ").append(kCoef).append(" * (0) * (1/").append(mCoef).append(") * (1) = 0\n\n");
	            sbTrig.append("=========================================\n");
	            sbTrig.append(" CONCLUSIÓN:\n");
	            sbTrig.append(" El límite real existe mediante análisis algebraico\n"
	            		    + " riguroso.\n");
	            sbTrig.append(" Valor de convergencia analítica:\n"
	            		    + " L = 0.0\n");
	            sbTrig.append("=========================================");
	            desarrolloTrigStepByStep = sbTrig.toString();
	        } 
	        else if (funcionNormalizada.contains("1-cos(x)") && funcionNormalizada.contains("x*sin(x)") && C == 0) {
	            esPatronTrig = true;
	            resultadoLimite = 0.5;
	            
	            StringBuilder sbTrig = new StringBuilder();
	            sbTrig.append("=========================================\n");
	            sbTrig.append("    DESARROLLO ANALÍTICO FORMAL DE LÍMITES\n");
	            sbTrig.append("=========================================\n\n");
	            sbTrig.append("▶ PASO 1: ANÁLISIS DE DOMINIO REAL\n");
	            sbTrig.append("  • Restricciones en el denominador:\n"
	            		    + "    x ≠ 0  y  sin(x) ≠ 0\n");
	            sbTrig.append("  • Puntos críticos de anulación:\n"
	            		    + "    x = k·π (con k ∈ ℤ)\n");
	            sbTrig.append("  • Dom(f)=\n"
	            		    + "    ℝ - { k·π }\n");
	            sbTrig.append("  Al ser c = 0 un valor donde la función no\n"
	            		    + "  está definida, analizamos la convergencia\n");
	            sbTrig.append("  en su entorno reducido.\n\n");
	            sbTrig.append("----------------------------------------\n");
	            sbTrig.append("▶ PASO 2: EVALUACIÓN DIRECTA E\n"
	            		    + "           INDETERMINACIÓN\n");
	            sbTrig.append("  Se evalúa f(x) de forma exacta sustituyendo\n"
	            		    + "  x = 0:\n");
	            sbTrig.append("           (1 - cos(0))       (1 - 1)       0\n");
	            sbTrig.append("   f(0) = —————————————— = ———————————— = ———\n");
	            sbTrig.append("             0 * sin(0)       0 * (0)       0\n\n");
	            sbTrig.append("  Conclusión: Existe una INDETERMINACIÓN de la\n"
	            		    + "  forma [0/0].\n\n");
	            sbTrig.append("----------------------------------------\n");
	            sbTrig.append("▶ PASO 3: APLICACIÓN DE TEOREMAS TRIGONOMÉTRICOS\n");
	            sbTrig.append("  Por el Teorema del Producto de Límites, separamos la\n");
	            sbTrig.append("  expresión en dos núcleos fundamentales conocidos:\n\n");
	            sbTrig.append("  • Teorema A:  lim [ (1 - cos(x)) / x² ] = 1/2 (0.5)\n");
	            sbTrig.append("                x→0\n\n");
	            sbTrig.append("  • Teorema B:  lim [ x / sin(x) ] = 1\n");
	            sbTrig.append("                x→0\n\n");
	            sbTrig.append("----------------------------------------\n");
	            sbTrig.append("▶ PASO 4: CÁLCULO TOTAL CONVERGENTE\n");
	            sbTrig.append("  Evaluamos algebraicamente el producto de ambos\n"
	            		    + "  resultados:\n");
	            sbTrig.append("    L = (1/2) * (1) = 0.5\n\n");
	            sbTrig.append("=========================================\n");
	            sbTrig.append(" CONCLUSIÓN:\n");
	            sbTrig.append(" El límite existe por reducción notable combinada.\n");
	            sbTrig.append(" Valor de convergencia analítica:\n"
	            		    + " L = 0.5\n");
	            sbTrig.append(" Coordenada del agujero en la gráfica:\n"
	            		    + " (0 ; 0.5)\n");
	            sbTrig.append("=========================================");
	            desarrolloTrigStepByStep = sbTrig.toString();
	        }

	        // === MOTOR 2: ANALIZADOR DE INDETERMINACIONES POR RACIONALIZACIÓN (SOPORTE COMPLEJO REAL) ===
	        if (!esPatronTrig && (funcionNormalizada.contains("sqrt") || funcionNormalizada.contains("sqr"))) {
	            try {
	                // 1. FUNCIÓN PERSONALIZADA: Le enseñamos a exp4j a procesar radicales de forma nativa
	                net.objecthunter.exp4j.function.Function sqrtCustom = 
	                    new net.objecthunter.exp4j.function.Function("sqrt", 1) {
	                        @Override
	                        public double apply(double... args) {
	                            return Math.sqrt(args[0]);
	                        }
	                    };

	                // Compilamos la expresión del backend inyectando el soporte de raíces
	                net.objecthunter.exp4j.Expression expRaiz = 
	                        new net.objecthunter.exp4j.ExpressionBuilder(funcionParaEvaluar)
	                            .variable("x")
	                            .function(sqrtCustom) // <-- Registro crítico
	                            .build();
	                
	                double h = 0.00001;
	                double izq = Double.NaN;
	                double der = Double.NaN;
	                
	                try { izq = expRaiz.setVariable("x", C - h).evaluate(); } catch(Exception e){}
	                try { der = expRaiz.setVariable("x", C + h).evaluate(); } catch(Exception e){}
	                
	                // VALIDACIÓN DE LÍMITES LATERALES (Soporta dominios unilaterales)
	                boolean izqEsValido = !Double.isNaN(izq) && !Double.isInfinite(izq);
	                boolean derEsValido = !Double.isNaN(der) && !Double.isInfinite(der);

	                // Si AMBOS lados están completamente fuera del dominio real, entonces sí es inválido
	                if (!izqEsValido && !derEsValido) {
	                    resultadoLimite = Double.NaN;
	                } else {
	                    //Activamos el motor de la raíz porque al menos un lado es perfectamente válido
	                    esPatronRaiz = true;
	                    
	                    double aprox;
	                    if (!izqEsValido) {
	                        aprox = der; // Nos quedamos solo con el comportamiento por la derecha (x -> 0+)
	                    } else if (!derEsValido) {
	                        aprox = izq; // Nos quedamos solo con el comportamiento por la izquierda (x -> 0-)
	                    } else {
	                        aprox = (izq + der) / 2.0; // Bilateral estándar si ambos lados existen
	                    }
	                    
	                    resultadoLimite = (Math.abs(aprox - Math.round(aprox)) < 0.05) ? Math.round(aprox) : aprox;
	                    String factorIndeterminado = (C >= 0) ? "(x - " + (int)C + ")" : "(x + " + (int)Math.abs(C) + ")";
	                    
	                    // Numerador 
	                    String numeradorCompleto = "";
	                    if (textoFuncion.contains("/")) {
	                        numeradorCompleto = textoFuncion.split("/")[0].trim(); 
	                    } else {
	                        numeradorCompleto = textoFuncion.trim();
	                    }

	                    if (numeradorCompleto.startsWith("(") && numeradorCompleto.endsWith(")")) {
	                        numeradorCompleto = numeradorCompleto.substring(1, numeradorCompleto.length() - 1).trim();
	                    }
	                    
	                    // Extraemos la raíz interna
	                    String expresionInternaRaiz = "x"; 
	                    if (numeradorCompleto.contains("sqrt(")) {
	                        int inicioParentesis = numeradorCompleto.indexOf("sqrt(") + 5;
	                        int finParentesis = numeradorCompleto.indexOf(")", inicioParentesis);
	                        if (inicioParentesis >= 5 && finParentesis > inicioParentesis) {
	                            expresionInternaRaiz = numeradorCompleto.substring(inicioParentesis, finParentesis).trim();
	                        }
	                    }
	                    
	                    // Extraemos el término independiente
	                    String expresionRaizLimpia = ""; 
	                    String expreIndepenRaiz = "0";    
	                    String signoOperacion = "-";     

	                    if (numeradorCompleto.contains(")-")) {
	                        expresionRaizLimpia = numeradorCompleto.split("\\)-")[0] + ")";
	                        expreIndepenRaiz = numeradorCompleto.split("\\)-")[1].trim();
	                        signoOperacion = "-";
	                    } else if (numeradorCompleto.contains(")+")) {
	                        expresionRaizLimpia = numeradorCompleto.split("\\)\\+")[0] + ")";
	                        expreIndepenRaiz = numeradorCompleto.split("\\)\\+")[1].trim();
	                        signoOperacion = "+";
	                    } else {
	                        expresionRaizLimpia = "sqrt(" + expresionInternaRaiz + ")";
	                    }
	                    
	                    // Conjugado
	                    String signoConjugado = signoOperacion.equals("-") ? "+" : "-";
	                    String conjugado = expresionRaizLimpia + " " + signoConjugado + " " + expreIndepenRaiz;
	                    String numeradorConjugadoFormat = "(" + expresionRaizLimpia + " " + signoOperacion + " " 
	                    + expreIndepenRaiz + ")(" + conjugado + ")";
	                    
	                    // Denominador
	                    String denominador = "";
	                    if (textoFuncion.contains("/")) {
	                        String parteDerecha = textoFuncion.split("/")[1].trim();
	                        if (parteDerecha.startsWith("(") && parteDerecha.endsWith(")")) {
	                            parteDerecha = parteDerecha.substring(1, parteDerecha.length() - 1).trim();
	                        }
	                        denominador = parteDerecha;
	                    } else {
	                        denominador = "x - " + C; 
	                    }
	                    //Dominio de la función
	                    int origenDominio = 0;
	                    if (expresionInternaRaiz.contains("+")) {
	                        try {
	                            String[] partes = expresionInternaRaiz.split("\\+");
	                            origenDominio = -Integer.parseInt(partes[1].trim());
	                        } catch(Exception e) { origenDominio = (int)-C; }
	                    } else if (expresionInternaRaiz.contains("-")) {
	                        try {
	                            String[] partes = expresionInternaRaiz.split("-");
	                            origenDominio = Integer.parseInt(partes[1].trim());
	                        } catch(Exception e) { origenDominio = (int)C; }
	                    }
	                    String strC = Double.isInfinite(C) ? (C > 0 ? "∞" : "-∞") : 
				              (C % 1 == 0 ? String.valueOf((int)C) : String.valueOf(C));

                  StringBuilder sbRaiz = new StringBuilder();
                  sbRaiz.append("=========================================\n");
                  sbRaiz.append("          ANÁLISIS DE RADICALES\n");
                  sbRaiz.append("=========================================\n\n");
                  sbRaiz.append("▶ PASO 1: ANÁLISIS DE DOMINIO Y\n"
                  		    + "          RESTRICCIÓN\n");
                  sbRaiz.append("  • Condición del radical:\n     ").append(expresionInternaRaiz).append(" ≥ 0\n");
                  sbRaiz.append("  • Dominio real de existencia:\n     x ∈ [").append(origenDominio).append(", +∞)\n");
                  
                  if (denominador.equals("x") || denominador.contains("- 0") || String.valueOf(origenDominio).equals(strC)) {
                      sbRaiz.append("  • Exclusión por división:\n"
                      		    + "    x ≠ ").append(strC).append("\n");
                      if (String.valueOf(origenDominio).equals(strC)) {
                          sbRaiz.append("  • Dom(f):\n"
                          		    + "    (").append(strC).append(", +∞)\n\n");
                      } else {
                          sbRaiz.append("  • Dom(f):\n"
                          		    + "    [").append(origenDominio).append(", ").append(strC).append(") ∪ (")
                          .append(strC).append(", +∞)\n\n");
                      }
                  } else {
                      sbRaiz.append("  • Exclusión por división:\n"
                      		    + "    x ≠ ").append(strC).append("\n");
                      sbRaiz.append("  • Dom(f)=\n"
                      		    + "    [").append(origenDominio).append(", ")
                            .append(strC).append(") ∪ (").append(strC).append(", +∞)\n\n");
                  }
                  sbRaiz.append("  Al evaluar f(").append(strC).append(") directamente,\n"
                  		    + "  se presenta la indeterminación [0/0]\n"
                  		    + "  justo en el punto removible del\n"
                  		    + "  Dominio.\n\n");
                  sbRaiz.append("----------------------------------------\n");
                  sbRaiz.append("▶ PASO 2: RESOLUCIÓN POR RACIONALIZACIÓN\n");
                  sbRaiz.append("  Para eliminar la indeterminación,\n"
                  		    + "  multiplicamos por el CONJUGADO del\n");
                  sbRaiz.append("  término radical para forzar una\n");
                  sbRaiz.append("  Diferencia de Cuadrados:\n"
                  		    + "   (a - b)(a + b) = a² - b²\n\n");
                  sbRaiz.append("----------------------------------------\n");
                  sbRaiz.append("▶ PASO 3: PROCESO ALGEBRAICO OPERACIONAL\n");
                  sbRaiz.append("   1. Multiplicar numerador/denominador\n"
                  		    + "      por la expresión conjugada.\n\n");
                  sbRaiz.append("        ").append(numeradorConjugadoFormat).append("\n lim"); 
                  sbRaiz.append(".  ——————————————————\n");
                  sbRaiz.append("  x→").append(strC).append("   (").append(denominador).append(")(")
                                .append(conjugado).append(")\n\n");
                  sbRaiz.append("   2. Romper raíces aplicando los\n"
                  		    + "      cuadrados perfectos.\n\n");
                  sbRaiz.append("           (").append(numeradorCompleto).append(" - (").append(expreIndepenRaiz).append("))²\n lim"); 
                  sbRaiz.append(".  ——————————————————\n");
                  sbRaiz.append("  x→").append(strC).append("    (").append(denominador).append(")(")
                                .append(conjugado).append(")\n\n");
                  sbRaiz.append("              (").append(expresionInternaRaiz).append(") - (").append(expreIndepenRaiz).append(")²\n lim"); 
                  sbRaiz.append(".  ——————————————————\n");
                  sbRaiz.append("  x→").append(strC).append("   (").append(denominador).append(")(")
                                .append(conjugado).append("))\n\n");
                  sbRaiz.append("   3. Desarrollamos la expresión y\n"
                  		    + "      cancelamos el término que ocaciona\n"
                  		    + "      la indeterminación, es decir, los\n"
                  		    + "      valores comunes en c = ").append(strC).append(".\n\n");
                  sbRaiz.append("----------------------------------------\n");
                  sbRaiz.append("▶ PASO 4: EVALUACIÓN CONTINUA REDUCIDA\n");
                  sbRaiz.append("  Sustituyendo el punto libre de\n"
                  		    + "  indeterminaciones.\n\n"
                  		    + "  Reemplazamos ").append(strC).append(" en x\n\n");
                  sbRaiz.append("=========================================\n");
                  sbRaiz.append(" CONCLUSIÓN:\n");
                  sbRaiz.append(" El límite real converge por método de\n"
                  		    + " racionalización sutil.\n");
                  sbRaiz.append(" Valor del límite:\n"
                  		    + " L = ").append((resultadoLimite % 1 == 0) ? String
                  		.valueOf((int)resultadoLimite) : String.valueOf(resultadoLimite)).append("\n");
                  sbRaiz.append("=========================================");
                  desarrolloStepByStep = sbRaiz.toString();

                  panelGrafico.forzarExpresionDirecta(expRaiz, C, resultadoLimite);
	                }
	            } catch (Exception e) {
	                esPatronRaiz = false;
	            }
	        }

	        // === MOTOR 3: ANALIZADOR DE PATRONES DE FACTORIZACIÓN ALGEBRAICA ===
	        if (!esPatronTrig && !esPatronRaiz && (funcionNormalizada.contains("x^2") || funcionNormalizada.contains("x^3")) && 
	                funcionNormalizada.contains("/")) {
	            try {
	                net.objecthunter.exp4j.Expression expFactor = 
	                        new net.objecthunter.exp4j.ExpressionBuilder(funcionParaEvaluar).variable("x").build();
	                
	                // NUEVO: Validación estricta para asegurar que es una indeterminación 0/0
	                // Evaluamos el numerador por separado si es posible, o validamos que la sustitución directa falle/tienda a NaN o indeterminación real
	                double h = 0.00001;
	                double izq = Double.NaN;
	                double der = Double.NaN;
	                
	                try { izq = expFactor.setVariable("x", C - h).evaluate(); } catch(Exception e){}
	                try { der = expFactor.setVariable("x", C + h).evaluate(); } catch(Exception e){}
	                
	                // Si los límites laterales explotan a valores gigantescos en la vecindad, NO es factorización, es una asíntota
	                if (Math.abs(izq) > 5000.0 || Math.abs(der) > 5000.0) {
	                    esPatronFactorizable = false;
	                } else if (!Double.isNaN(izq) && !Double.isNaN(der) && Math.abs(izq - der) < 0.01) {
	                    
	                    esPatronFactorizable = true;
	                    double aprox = (izq + der) / 2.0;
	                    resultadoLimite = (Math.abs(aprox - Math.round(aprox)) < 0.001) ? Math.round(aprox) : aprox;
	                    
	                    // Determinamos el signo del binomio para mostrar el paso matemático real
	                    String factorIndeterminado = (C >= 0) ? "(x - " + (int)C + ")" : "(x + " + (int)Math.abs(C) + ")";
	                    String numeradorFactorizado = "";
	                    if (textoFuncion.contains("x^2") && textoFuncion.contains("-")) {
	                        int conjugadoC = (int) Math.abs(C);
	                        numeradorFactorizado = "(x - " + conjugadoC + ")(x + " + conjugadoC + ")";
	                    } else {
	                        numeradorFactorizado = factorIndeterminado + " * (Polinomio_Reducido)";
	                    }
	                    int enteroC = (int) C;
	                    StringBuilder sbFact = new StringBuilder();
	                    sbFact.append("=========================================\n");
	                    sbFact.append("    ANÁLISIS DE FACTORIZACIÓN ALGEBRAICA\n");
	                    sbFact.append("=========================================\n\n");
	                    sbFact.append("▶ PASO 1: EVALUACIÓN Y COCIENTE\n"
	                    		    + "          INDETERMINADO\n");
	                    sbFact.append("  Sustituyendo el valor crítico x = ").append(C).append(":\n\n");
	                    sbFact.append("               0\n"
	                    		    + "     f(").append(C).append(") = ——\n"
	                    		    + "               0\n"
	                    		    + "  Indeterminación evitable.\n");
	                    sbFact.append("  Dom ƒ = ℝ - {").append(C).append("}\n");
	                    sbFact.append("----------------------------------------\n");
	                    sbFact.append("▶ PASO 2: APLICACIÓN DEL TEOREMA DEL\n"
	                    		    + "          FACTOR\n");
	                    sbFact.append("  Dado que x = ").append(C).append(" anula los polinomios,\n"
	                    		    + "  ambos componentes comparten de forma\n");
	                    sbFact.append("  obligatoria el factor: ").append(factorIndeterminado).append("\n\n");
	                    sbFact.append("----------------------------------------\n");
	                    sbFact.append("▶ PASO 3: DESCOMPOSICIÓN EN FACTORES\n"
	                    		    + "          REALES\n");
	                    sbFact.append("  Reescribiendo la fracción mediante sus\n"
	                    		    + "  factores equivalentes:\n\n");
	                    sbFact.append("              ").append(numeradorFactorizado).append("\n lim"); 
	                    sbFact.append(".——————————————————\n");
	                    sbFact.append("  x→").append(C).append("           ").append(factorIndeterminado).append("\n\n");
	                    sbFact.append("----------------------------------------\n");
	                    sbFact.append("▶ PASO 4: CANCELACIÓN DE LA DIVISIÓN POR\n"
	                    		    + "           CERO\n");
	                    sbFact.append("  Eliminamos el término común superior e\n"
	                    		    + "  inferior, quedandonos así:\n\n");
	                    sbFact.append("  = lim  ").append("(x + ").append(enteroC).append(")").append("\n");
	                    sbFact.append("    x→").append(C).append("\n\n");
	                    sbFact.append("----------------------------------------\n");
	                    sbFact.append("▶ PASO 5: EVALUACIÓN DIRECTA FINAL\n");
	                    sbFact.append("  Sustituyendo libre de\n"
	                    		    + "  indeterminaciones:\n\n");
	                    sbFact.append("  Reemplazamos ").append(enteroC).append(" en x");
	                    sbFact.append("\n  L = ").append((resultadoLimite % 1 == 0) ? String.valueOf((int)resultadoLimite) :
	                    	String.valueOf(resultadoLimite)).append("\n");
	                    sbFact.append("=========================================\n");
	                    sbFact.append(" CONCLUSIÓN:\n");
	                    sbFact.append(" Removido el comportamiento\n indeterminado, el límite converge.\n");
	                    sbFact.append("\n Coordenada exacta del punto removible:\n (").append(C).append(" ; ").append(resultadoLimite).append(")\n");
	                    sbFact.append("=========================================");
	                    desarrolloStepByStep = sbFact.toString();
	                }
	            } catch(Exception e) {
	                esPatronFactorizable = false;
	            }
	        }

	        // ------- Cálculo analítico del Límite estándar ---------
	        if (!esPatronTrig && !esPatronRaiz && !esPatronFactorizable) {
	            try {
	                net.objecthunter.exp4j.Expression expresionLimite = 
	                        new net.objecthunter.exp4j.ExpressionBuilder(funcionParaEvaluar).variable("x").build(); 
	                resultadoLimite = expresionLimite.setVariable("x", C).evaluate();
	            }
	            catch (Exception e) {
	                try {
	                    net.objecthunter.exp4j.Expression expreLateralidad = 
	                            new net.objecthunter.exp4j.ExpressionBuilder(funcionParaEvaluar).variable("x").build();
	                    double h = 0.00001;
	                    double izq = expreLateralidad.setVariable("x", C - h).evaluate();
	                    double dere = expreLateralidad.setVariable("x", C + h).evaluate();
	                    
	                    if (Math.abs(izq) > 500.0 || Math.abs(dere) > 500.0) {
	                        if (izq > 0 && dere > 0) resultadoLimite = Double.POSITIVE_INFINITY;
	                        else if (izq < 0 && dere < 0) resultadoLimite = Double.NEGATIVE_INFINITY;
	                        else resultadoLimite = Double.NaN;
	                    } else if (Math.abs(izq - dere) < 0.01) {
	                        resultadoLimite = (izq + dere) / 2;
	                    } else {
	                        resultadoLimite = Double.NaN;
	                    }
	                } catch (Exception mathE) {
	                    resultadoLimite = Double.NaN;
	                }
	            }
	        }
	        
	        //---CONFIGURACIÓN Y ACTUALIZACIÓN DEL LIENZO---
	        if (!esPatronRaiz) {
	            panelGrafico.configurarFuncion(textoFuncion, C, resultadoLimite);
	        }
	        panelGrafico.revalidate();
	        panelGrafico.repaint();
	        
	        //---FORMATO MATEMÁTICO LIMPIO PARA EL RESULTADO---
	        String resultFormateado;
	        if (!Double.isNaN(resultadoLimite) && !Double.isInfinite(resultadoLimite)) {
	            if (resultadoLimite % 1 == 0) {
	                resultFormateado = String.valueOf((int) resultadoLimite);
	            } else {
	                resultFormateado = String.format("%.4f", resultadoLimite);
	            }
	        } else {
	            resultFormateado = "NaN";
	        }
	        
	        //Actualizamos los datos del letrero inferior de manera inicial
	        labelEstado.setText(" lim    f(x) = " + textoFuncion + " | x → "+ C);    
	        
	        //---DESPLIEGUE DEL RESULTADO EN LA VENTANA ---
	        StringBuilder procedimiento = new StringBuilder();
	        procedimiento.append("Dada la función:\n f(x) = ").append(textoFuncion).append("\n\n");
	        procedimiento.append("Se evalúa el límite cuando x → ").append(C).append("\n");
	     
	        String sigInfIzquierdo = "-∞"; 
	        String sigInfDerecho = "+∞";  
	        boolean coinciden = sigInfIzquierdo.equals(sigInfDerecho);
             
	        double hPrueba = 0.0001;
	        double pIzq = Double.NaN;
	        double pDer = Double.NaN;
	        try {
	            net.objecthunter.exp4j.Expression expPrueba = 
	                    new net.objecthunter.exp4j.ExpressionBuilder(funcionParaEvaluar).variable("x").build();
	            pIzq = expPrueba.setVariable("x", C - hPrueba).evaluate();
	            pDer = expPrueba.setVariable("x", C + hPrueba).evaluate();
	        } catch (Exception e) {}

	        // Detectamos si al menos uno de los lados se dispara al infinito
	        boolean tiendeAInfinito = Double.isInfinite(resultadoLimite) || Math.abs(pIzq) > 500.0 || Math.abs(pDer) > 500.0;

	        if (tiendeAInfinito) {
	            // Determinamos los signos reales de cada lado para el reporte escrito
	            String signoIzq = (pIzq > 0) ? "+∞" : "-∞";
	            String signoDer = (pDer > 0) ? "+∞" : "-∞";
	            boolean coincidenLosInfinitos = signoIzq.equals(signoDer);

	            if (coincidenLosInfinitos) {
	                labelEstado.setText(" lim f(x) = " + signoDer + " (Diverge) | x → " + C);
	                labelEstado.setForeground(Color.RED);
	            } else {
	                labelEstado.setText(" lim f(x) = No existe real | x → " + C);
	                labelEstado.setForeground(Color.RED);
	            }

	            procedimiento.append("=========================================\n");
	            procedimiento.append("         ♣ CÁLCULO DEL LÍMITE  \n");
	            procedimiento.append("=========================================\n\n");      
	            procedimiento.append("▶ I. ANÁLISIS DE DOMINIO Y ASÍNTOTA\n\n");
	            procedimiento.append("   El punto x = ").append(C).append(" no pertenece al dominio\n");
	            procedimiento.append("   de la función, ya que provoca una\n"
	                               + "   división por cero.\n\n");
	            procedimiento.append("   Esto determina la existencia de una:\n");
	            procedimiento.append("    ▲ ASÍNTOTA VERTICAL en la recta:\n"
	                               + "               x = ").append(C).append("\n\n");
	            procedimiento.append("    Dom ƒ = ℝ - {").append(C).append("}\n");
	            procedimiento.append("----------------------------------------\n");
	            procedimiento.append("▶ II. VERIFICACIÓN POR LÍMITES LATERALES\n\n");
	            procedimiento.append(" - Teorema Fundamental de los Límites\n"
	            		           + "   Laterales:\n"
	                               + "   El límite bilateral existe si y solo\n"
	                               + "   si ambos límites laterales coinciden.\n\n");
	            procedimiento.append(" Evaluación de tendencias laterales:\n");
	            procedimiento.append("   • lim (x→").append(C).append("⁻) f(x) = ").append(signoIzq).append("\n");
	            procedimiento.append("   • lim (x→").append(C).append("⁺) f(x) = ").append(signoDer).append("\n\n");
	            procedimiento.append("=========================================\n");
	            procedimiento.append(" CONCLUSIÓN:\n");

	            if (!coincidenLosInfinitos) {
	                procedimiento.append(" Los límites laterales tienden a\n"
	                		           + " INFINITOS OPUESTOS:\n");
	                procedimiento.append("      ").append(signoIzq).append("  ≠  ").append(signoDer).append("\n\n");
	                procedimiento.append(" Por el Teorema de Unicidad, el límite\n"
	                		           + " bilateral global NO EXISTE en los\n"
	                                   + " números reales.\n");
	                procedimiento.append(" L = No existe (Discontinuidad Esencial\n"
	                		           + " de Infinito)\n");
	            } else {
	                procedimiento.append(" Ambos límites laterales coinciden en su\n"
	                                   + " comportamiento infinito (Mismo Signo).\n\n");
	                procedimiento.append(" El límite diverge globalmente a:\n"
	                                   + " L = ").append(signoDer).append("\n");
	            }
	            procedimiento.append("=========================================");
	            
	        } else if (Double.isNaN(resultadoLimite)) {
	            
	               if ("1/x".equals(textoFuncion) && C == 0) {
	                    labelEstado.setText(" lim  f(x) = No existe | x → " + C + "  (-∞ ≠ +∞)"); 
	                	procedimiento.append("=========================================\n");
		                procedimiento.append("         ♠ CÁLCULO DEL LÍMITE  \n");
		                procedimiento.append("=========================================\n\n");
	                    procedimiento.append("▶ I. ANÁLISIS POR SUSTITUCIÓN DIRECTA\n");
	                    procedimiento.append("   f(0) = 1 / 0\n"
	                    		           + "   La división por cero no está definida\n"
	                    		           + "   en los ℝ.\n\n");
	                    procedimiento.append("----------------------------------------\n");
	                    procedimiento.append("▶ II. TEOREMA DE EXISTENCIA Y UNICIDAD\n");
	                    procedimiento.append("   Evaluando un entorno reducido lateral\n"
	                    		           + "   (h = 0.00001):\n");
	                    procedimiento.append("   • Lateral Izquierdo (0 - h) → -∞\n");
	                    procedimiento.append("   • Lateral Derecho   (0 + h) → +∞\n\n");
	                    procedimiento.append("=========================================\n");
	                    procedimiento.append(" CONCLUSIÓN:\n");
	                    procedimiento.append(" Límites laterales divergentes y opuestos\n"
	                    		           + " (-∞ ≠ +∞).\n");
	                    procedimiento.append(" El límite global NO EXISTE en el campo\n"
	                    		           + " de los reales.\n");
	                    procedimiento.append("=========================================");
	               } else {
	            	   labelEstado.setText(" lim  f(x) = No existe real | x → " + C);
	            	   labelEstado.setForeground(new Color(209, 0, 86));

	            	   procedimiento.append("=========================================\n");
	            	   procedimiento.append("         ♦ CÁLCULO DEL LÍMITE  \n");
	            	   procedimiento.append("=========================================\n\n");
	            	   procedimiento.append("▶ I. ANÁLISIS DE EXISTENCIA EN EL CAMPO\n"
	            	   		              + "     REAL\n\n");
	            	   procedimiento.append("   Se evalúan los entornos de\n"
	            	   		              + "   aproximación para la función en el\n"
	            	                      + "   punto crítico c = ").append(C).append(".\n\n");
	            	   procedimiento.append(" - Teorema de Existencia del Límite:\n"
	            	   		              + "   Sea ƒ: D → ℝ una función real con\n"
	            	   		              + "   dominio D ⊂ ℝ. Decimos que:\n"
	            	   		              + "            lim f(x) = L\n"
	            	   		              + "            x→ϲ\n"
	            	   		              + "   sí y solo sí ϲ es un punto de\n"
	            	   		              + "   acumulación de D.\n");
	            	   procedimiento.append("----------------------------------------\n");
	            	   procedimiento.append("▶ II. RESTRICCIÓN DEL DOMINIO REAL\n\n");
	            	   procedimiento.append("   Al intentar aproximarse al punto por\n"
	            	                      + "   uno o ambos lados, la función genera\n"
	            	                      + "   operaciones que caen FUERA del dominio\n"
	            	                      + "   de los números reales (ℝ)\n"
	            	                      + "   (ej. raíces negativas o logaritmos\n"
	            	                      + "    inapropiados).\n\n");
	            	   procedimiento.append("=========================================\n");
	            	   procedimiento.append(" CONCLUSIÓN:\n");
	            	   procedimiento.append(" Para que un límite exista en ℝ, la\n"
	            	   		              + " función debe estar definida en un\n"
	            	                      + " entorno real cercano a 'c'.\n");
	            	   procedimiento.append(" Dado que el entorno de aproximación\n"
	            	   		              + " viola el dominio real, el límite\n"
	            	                      + " bilateral global NO EXISTE en los ℝ.\n");
	            	   procedimiento.append(" L = No existe en ℝ\n");
	            	   procedimiento.append("=========================================\n");
	               }
	                        
	        } else {
	                labelEstado.setText(" lim  f(x) = " + resultFormateado + "  |  x → " + C);
	                labelEstado.setForeground(new Color(5, 153, 40));
	                        
	                if (esPatronTrig) {
	                    procedimiento.append(desarrolloTrigStepByStep);
	                } else if (esPatronRaiz || esPatronFactorizable) {
	                    procedimiento.append(desarrolloStepByStep);
	                } else if ("sin(x)/x".equals(textoFuncion) && C == 0) {
	                	labelImagenTeorema.setVisible(true);
	                	
	                	procedimiento.append("=========================================\n");
		                procedimiento.append("    LÍMITE TRIGONOMÉTRICO FUNDAMENTAL\n");
		                procedimiento.append("=========================================\n\n");
		                procedimiento.append("▶ I. ANÁLISIS DE DOMINIO REAL\n");
		                procedimiento.append("   • Restricción del denominador: x ≠ 0\n");
		                procedimiento.append("   • Dom(f) de existencia = ℝ - {0}\n");
		                procedimiento.append("   Dado que c = 0 es un punto de acumulación\n");
		                procedimiento.append("   excluido del dominio, procedemos a evaluar\n");
		                procedimiento.append("   el comportamiento en su entorno reducido.\n\n");
		                
		                procedimiento.append("----------------------------------------\n");
		                procedimiento.append("▶ II. ANÁLISIS DE INDETERMINACIÓN NOTABLE\n");
		                procedimiento.append("   Al evaluar f(0) resulta la\n"
		                    		           + "   indeterminación notable [0/0].\n\n");
		                
		                procedimiento.append("----------------------------------------\n");
		                procedimiento.append("▶ III. RESOLUCIÓN POR TEOREMA DEL EMPAREDADO\n");
		                procedimiento.append("   Evaluación por entornos numéricos\n"
		                    		           + "   simétricos:\n");
		                procedimiento.append("   • Por Izquierda (0 - h) → 0.9999\n");
		                procedimiento.append("   • Por Derecha   (0 + h) → 0.9999\n\n");
		                procedimiento.append("   Paso 1: Se dibuja una sección del\n"
		                		           + "   círculo unitario (de radio 1) y se\n"
		                		           + "   comparan las áreas de tres figuras\n"
		                		           + "   que atrapan al ángulo x, donde se\n"
		                		           + "   encuentran: un triángulo pequeño\n"
		                		           + "   inscrito, el sector circular y un\n"
		                		           + "   triángulo grande circunscrito.\n"
		                		           + "   (Visualice la imagen Inferior)\n\n");	                                    
		                procedimiento.append("   Paso 2: Al calcular sus áreas, se\n"
		                		           + "   obtiene la desigualdad:\n");
		                procedimiento.append("                sin(x)\n"
		                		           + "     cos(x) < —————— < 1\n"
		                		           + "                   x\n\n");
		                procedimiento.append("   Paso 3: Como lim  cos(x)=1 y  lim  1=1\n"
		                		           + "               x→0              x→0\n"
		                		           + "   la función del centro se comprime\n"
		                		           + "   entre ambas y su valor\n"
		                		           + "   consecuentemente es\n"
		                		           + "       lim     sin(x)\n"
		                		           + "       x→0   ———— = 1\n"
		                		           + "                x\n\n");
	                    procedimiento.append("=========================================\n");
	                    procedimiento.append(" CONCLUSIÓN:\n");
	                    procedimiento.append(" Ambos lados convergen de forma exacta.\n");
	                    procedimiento.append(" El límite existe y su valor es:\n"
	                    		           + " L = ").append(resultFormateado).append("\n");
	                    procedimiento.append("=========================================");
	                } else {
	                	procedimiento.append("=========================================\n");
	                	procedimiento.append("         ■ CÁLCULO DEL LÍMITE  \n");
	                	procedimiento.append("=========================================\n\n"); 

	                	procedimiento.append("▶ I. ANÁLISIS DE CONTINUIDAD Y DOMINIO\n\n");
	                	procedimiento.append("   Evaluación del punto directo mediante\n"
	                			           + "   sustitución:\n");
	                	procedimiento.append("   f( ").append(C).append(" ) = ").append(resultFormateado).append("\n\n");
	                	procedimiento.append("   Al obtener un valor real definido, se\n"
	                			           + "   confirma que el punto x = ").append(C)
	                                 .append("\n   PERTENECE al dominio de la función\n");
	                	procedimiento.append("   y no presenta ninguna indeterminación\n"
	                			           + "   matemática.\n\n");
	                	procedimiento.append("----------------------------------------\n");
	                	procedimiento.append("▶ II. APLICACIÓN DEL TEOREMA DE\n"
	                			           + "      CONTINUIDAD\n\n");
	                	procedimiento.append("   Por definición de continuidad:\n");
	                	procedimiento.append("   Si f(").append(C).append(") está definida y f es\n"
	                			           + "   continua en ").append(C);
	                	procedimiento.append("\n   lim f(x) = f(").append(C).append(")\n");
	                	procedimiento.append("   x→").append(C);
	                	procedimiento.append("\n\n=========================================\n");
	                	procedimiento.append(" CONCLUSIÓN:\n");
	                	procedimiento.append(" Puesto que la función es continua en\n"
	                	                   + " este punto, los límites laterales\n"
	                	                   + " coinciden plenamente con f(c).\n\n");
	                	procedimiento.append(" El límite existe y es igual a:\n"
	                	                   + " L = ").append(resultFormateado).append("\n");
	                	procedimiento.append("=========================================");
	                }
	        }
	                    
	        
	        areaProcedimiento.setText(procedimiento.toString());        
	    }
	    catch(Exception ex) {
	        JOptionPane.showMessageDialog(this,
	        "Error en los datos ingresados.\n"
	                + "Verifique que la función y el valor de c sean los correctos." 
	                + "\nEjemplos válidos:\n"
	                + "●ᴥ●  (1-cos(2*x))/sin(3*x)\n"
	                + "●ᴥ●  sin(x)/x\n"
	                + "●ᴥ●  1/x");
	    }
	}
//MÉTODO MAIN
	public static void main(String[] args) {
		
		//DSincronización con el Event Dispatch Threads (EDT) para garantizar el thread-safety
		//asegurando que cualquier modificación en el estado o creación de la GUI se encole y ejecute directamente
		//en el EDT garantizando al estabilidad del renderizado de la interfaz.
		SwingUtilities.invokeLater(new Runnable() {
			@Override //garantiza la validez de código en tiempo de compilación
			public void run() {
				//Instanciamos la clase principal AnalisisLimitesApp, para dispara de manera segura la interfaz de
				//usuario dentro del EDT
				new AnalisisLimitesApp();
			}
		});
	} //Fin Main
}