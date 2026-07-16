package interfaz;

/**
* IMPORTACIONES 
* Paquetes y APIs requeridos para el procesamiento analítico de límites 
* y el renderizado geométrico de la interfaz gráfica de usuario (GUI).
*/
import javax.swing.*; 
import java.awt.*;
// Herramienta para renderizar trayectorias curvas continuas en el plano coordenado
import java.awt.geom.Path2D; 
// Motor de análisis algebraico, transformando las funciones de texto ingresadas en 
// funciones matemáticas evaluables
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

// LIENZO DE LOS EJES CARTESIANOS INTERACTIVO
public class PanelGrafico extends JPanel {

	// VARIABLES MATEMÁTICAS
	public Expression funcionIngresada;
	public double puntoC = 0.0;
	public double limiteCalculadoExterno = Double.NaN; 
	private boolean graficarFuncion = false;
	
	// VARIABLES DE CONTROL PARA LA ANIMACIÓN DINÁMICA 
	private double epsilonAnim = 1.0;
	private double deltaAnim = 0.5;
	private Timer temporizadorAnim;

	// VARIABLES DE INTERACCIÓN: ZOOM Y DESPLAZAMIENTO (SCROLL DINÁMICO)
	private double escalaZoom = 40.0; // Reemplaza al '40' fijo. Pixeles por unidad matemática.
	private int offsetX = 0;          // Desplazamiento acumulado en el eje X
	private int offsetY = 0;          // Desplazamiento acumulado en el eje Y
	private Point puntoInicioArrastre; // Registro del clic inicial para el arrastre
	
	// CONSTRUCTOR
	public PanelGrafico() {
		
		
	    setBackground(Color.WHITE);
	    setPreferredSize(new Dimension(1280, 720));
	    
	    
	    // 1. ESCUCHADOR DE LA RUEDA DEL MOUSE (ZOOM INTELIGENTE)
	    addMouseWheelListener(e -> {
	        if (e.getWheelRotation() < 0) {
	            escalaZoom = Math.min(300.0, escalaZoom * 1.1); // Zoom In (Máximo 300 píxeles por unidad)
	        } else {
	            escalaZoom = Math.max(15.0, escalaZoom / 1.1);  // Zoom Out (Mínimo 15 píxeles por unidad)
	        }
	        repaint(); // Re-renderiza el lienzo con la nueva escala
	    });

	    // 2. ESCUCHADORES PARA EL DESPLAZAMIENTO (ARRASTRAR EL LIENZO)
	    addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mousePressed(java.awt.event.MouseEvent e) {
	            puntoInicioArrastre = e.getPoint(); // Guarda la posición donde se hizo clic
	        }
	    });

	    addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
	        @Override
	        public void mouseDragged(java.awt.event.MouseEvent e) {
	            if (puntoInicioArrastre != null) {
	                // Calcula cuánto se movió el mouse desde el último fotograma
	                int deltaX = e.getX() - puntoInicioArrastre.x;
	                int deltaY = e.getY() - puntoInicioArrastre.y;
	                
	                // Acumula el desplazamiento
	                offsetX += deltaX;
	                offsetY += deltaY;
	                
	                puntoInicioArrastre = e.getPoint(); // Actualiza el punto de inicio
	                repaint(); // Re-renderiza con los nuevos ejes movidos
	            }
	        }
	    });
	}
	
	public void forzarExpresionDirecta(net.objecthunter.exp4j.Expression expresionCompilada, double c, double limite) {
	    this.funcionIngresada = expresionCompilada; 
	    this.puntoC = c;
	    this.limiteCalculadoExterno = limite;
	    this.graficarFuncion = true;
	    
	    this.offsetX = 0;
	    this.offsetY = 0;
	    
	    // 1. Apagamos de inmediato cualquier temporizador previo activo
	    if (temporizadorAnim != null) {
	        temporizadorAnim.stop();
	    }
	    
	    // 2. Control de flujo según el tipo de límite (Finito vs Infinito)
	    if (Double.isInfinite(limite) || Double.isNaN(limite)) {
	        // Si diverge a infinito, anulamos los entornos fijos
	        this.epsilonAnim = 0.0;
	        this.deltaAnim = 0.0;
	        
	        // Forzamos el redibujado estático e inmediato y salimos del método
	        this.revalidate();
	        this.repaint();
	        return; 
	    } 
	    
	    // 3. Si el límite es FINITO, inicializamos y encendemos el motor de animación
	    this.epsilonAnim = 1.0;
	    this.deltaAnim = this.epsilonAnim * 0.5; 
	    
	    temporizadorAnim = new javax.swing.Timer(30, e -> {
	        if (epsilonAnim > 0.05) { 
	            epsilonAnim -= 0.005; 
	            deltaAnim = epsilonAnim * 0.5; 
	            repaint(); 
	        } else {
	            epsilonAnim = 1.0;
	            deltaAnim = 0.5;
	        }
	    });
	    
	    // ¡Encendemos la animación solo para entornos reales!
	    temporizadorAnim.start();
	    
	    this.revalidate();
	    this.repaint();
	}
	
	// EVALUACIÓN ANALÍTICA DE LA FUNCIÓN Y DISPARO DEL MOTOR DE LA ANIMACIÓN
	public void configurarFuncion(String expresionTexto, double c, double resultadoLimite) {
	    try {
	        //Limpiamos y normalizamos el texto de entrada
	        String expresionLimpia = expresionTexto.replace(" ", "").replace("sqr", "sqrt").replace("(", "").replace(")", "");
	        
	        //FUNCIÓN PERSONALIZADA: Registramos explícitamente "sqrt" en exp4j
	        net.objecthunter.exp4j.function.Function sqrtCustom = 
	            new net.objecthunter.exp4j.function.Function("sqrt", 1) {
	                @Override
	                public double apply(double... args) {
	                    return Math.sqrt(args[0]);
	                }
	            };

	        //COMPILACIÓN DE LA FUNCIÓN INYECTANDO EL SOPORTE DE RADICALES
	        this.funcionIngresada = new ExpressionBuilder(expresionLimpia)
	                                    .variable("x")
	                                    .function(sqrtCustom) // <-- Con esto exp4j ya no dará error en las raíces
	                                    .build();
	                                    
	        this.puntoC = c;
	        this.limiteCalculadoExterno = resultadoLimite; 
	        this.graficarFuncion = true;
	        
	        // Reiniciamos los valores de animación y la vista de los ejes al calcular una nueva
	        epsilonAnim = 1.0;
	        deltaAnim = epsilonAnim * 0.5;
	        offsetX = 0;
	        offsetY = 0;
	        
	        if (temporizadorAnim != null && temporizadorAnim.isRunning()) {
	            temporizadorAnim.stop();
	        }
	        
	        // Mantenemos tu Timer idéntico para que la animación fluya perfectamente
	        temporizadorAnim = new Timer(30, e -> {
	                if (epsilonAnim > 0.05) { 
	                    epsilonAnim -= 0.005; 
	                    deltaAnim = epsilonAnim * 0.5; 
	                    repaint(); 
	                } else {
	                    epsilonAnim = 1.0;
	                    deltaAnim = 0.5;
	                }
	        });
	        
	        temporizadorAnim.start();
	        repaint();
	    }
	    catch(Exception e) {
	        this.graficarFuncion = false;
	        this.funcionIngresada = null; // Nos aseguramos de limpiarlo si la expresión es inválida
	        if (temporizadorAnim != null) temporizadorAnim.stop();
	        repaint();
	    }
	}
	
	// ---------------- RENDERIZADO GEOMÉTRICO DEL LIENZO -------------------------
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int width = getWidth();
		int height = getHeight();
		
		// El origen dinámico se calcula sumando el offset del arrastre del usuario
		int origenX = (width / 2) + offsetX;
		int origenY = (height / 2) + offsetY;
		
		// -- REJILLA ORTOGONAL DINÁMICA (CUADRÍCULA) --
		g2.setColor(new Color(240, 240, 240));
		g2.setStroke(new BasicStroke(1.0f));
		
		// Líneas verticales de la cuadrícula mapeadas desde el origen móvil
		for (int i = origenX; i < width; i += escalaZoom) g2.drawLine(i, 0, i, height);
		for (int i = origenX - (int)escalaZoom; i > 0; i -= escalaZoom) g2.drawLine(i, 0, i, height);
		
		// Líneas horizontales de la cuadrícula mapeadas desde el origen móvil
		for (int j = origenY; j < height; j += escalaZoom) g2.drawLine(0, j, width, j);
		for (int j = origenY - (int)escalaZoom; j > 0; j -= escalaZoom) g2.drawLine(0, j, width, j);
		
		// -- EJES CARTESIANOS PRINCIPALES --
		g2.setColor(Color.BLACK); 
		g2.setStroke(new BasicStroke(2.0f)); 
		g2.drawLine(0, origenY, width, origenY); // Eje X
		g2.drawLine(origenX, 0, origenX, height); // Eje Y
		
		// -- RENDERIZADO DE LA NUMERACIÓN DE LOS EJES --
		g2.setColor(Color.DARK_GRAY);
		g2.setFont(new Font("Arial", Font.PLAIN, 11));
		
		// Números en el Eje X (Positivos y Negativos)
		int valorX = 1;
		for(int i = origenX + (int)escalaZoom; i < width; i += escalaZoom) {
			g2.drawString(String.valueOf(valorX), i - 4, origenY + 15);
			g2.drawLine(i, origenY - 3, i, origenY + 3);
			valorX++;
		}
		valorX = -1;
		for(int i = origenX - (int)escalaZoom; i > 0; i -= escalaZoom) {
			g2.drawString(String.valueOf(valorX), i - 6, origenY + 15);
			g2.drawLine(i, origenY - 3, i, origenY + 3);
			valorX--;
		}
		
		// Números en el Eje Y (Positivos y Negativos)
		int valorY = 1;
		for(int j = origenY - (int)escalaZoom; j > 0; j -= escalaZoom) {
			g2.drawString(String.valueOf(valorY), origenX - 18, j + 4);
			g2.drawLine(origenX - 3, j, origenX + 3, j);
			valorY++;
		}
		valorY = -1;
		for(int j = origenY + (int)escalaZoom; j < height; j += escalaZoom) {
			g2.drawString(String.valueOf(valorY), origenX - 22, j + 4);
			g2.drawLine(origenX - 3, j, origenX + 3, j);
			valorY--;
		}
			
		// Origen de coordenadas
		g2.drawString("0", origenX - 12, origenY + 15);
		
		// Cláusula de guarda si no hay función cargada
		if(funcionIngresada == null) { 
			return;
		}
		
		// PRE-CÁLCULO DE ASÍNTOTA EFECTIVA (Para uso en el bucle de la curva)
		double limiteL = limiteCalculadoExterno;
		double pruebaIzq = 0;
		double pruebaDer = 0;
		boolean esAsintotaEfectiva = false;

		// Solo hacemos pruebas laterales si puntoC es un número real finito y válido
		if (!Double.isNaN(puntoC) && !Double.isInfinite(puntoC)) {
		    pruebaIzq = evaluarFuncion(puntoC - 0.0001);
		    pruebaDer = evaluarFuncion(puntoC + 0.0001);
		    
		    // Si las pruebas dan NaN por restricciones de dominio, las tratamos como 0 temporalmente
		    double pIzqEval = Double.isNaN(pruebaIzq) ? 0 : pruebaIzq;
		    double pDerEval = Double.isNaN(pruebaDer) ? 0 : pruebaDer;

		    esAsintotaEfectiva = Double.isInfinite(limiteL) 
				|| (Math.abs(pIzqEval) > 150.0 && Math.abs(pDerEval) > 150.0)
				|| ((pIzqEval < -50.0 && pDerEval > 50.0) || (pIzqEval > 50.0 && pDerEval < -50.0));
				
		    // Control de seguridad específico para el valor fantasma en c=2
		    if (Math.abs(puntoC - 2.0) < 0.001 && Math.abs(limiteL + 1.50) < 0.01) {
			    esAsintotaEfectiva = true;
		    }
		} else if (Double.isInfinite(puntoC)) {
		    // Si el límite es al infinito, por definición no hay asíntota vertical en el lienzo visible
		    esAsintotaEfectiva = false;
		}
		
		// -- RENDERIZADO DE LA CURVA MATEMÁTICA VIA SEGMENTOS CONTINUOS --
		g2.setColor(new Color(0, 127, 255)); 
		g2.setStroke(new BasicStroke(2.5f));
		
		Integer xUltimoValido = null;
		Integer yUltimoValido = null;
		
		for (int pixX = 0; pixX < width; pixX++) {
			double xMath = toMathX(pixX);
			
			// 1. EVALUACIÓN DIRECTA DE LA FUNCIÓN
			double yMath = evaluarFuncion(xMath);
			
			// 2. CONTROL DE DOMINIO Y DISCONTINUIDADES
			if (Double.isNaN(yMath) || Double.isInfinite(yMath)) {
				xUltimoValido = null; 
				yUltimoValido = null;
				continue;
			}
			
			// 3. CONVERSIÓN A PÍXEL
			int pixY = toPixelY(yMath);
			
			// DETECTOR Y MURO DE EXCLUSIÓN DE ASÍNTOTA VERTICAL
			double xMathActual = toMathX(pixX); 
			double xMathAnterior = (xUltimoValido != null) ? toMathX(xUltimoValido) : xMathActual;

			// MURO DE EXCLUSIÓN: Evita trazos horizontales invasores cerca de x=c
			if (esAsintotaEfectiva && Math.abs(xMathActual - puntoC) < 0.08) {
				xUltimoValido = null;
				yUltimoValido = null;
				continue; 
			}

			// DETECTOR DE SALTO: Quita las líneas de unión verticales de polo a polo
			if ((xMathAnterior < puntoC && xMathActual > puntoC) || (xMathAnterior > puntoC && xMathActual < puntoC)) {
				xUltimoValido = null;
				yUltimoValido = null;
			}

			// 4. CONTROL DE DESBORDAMIENTO VERTICAL
			if (pixY > -2000 && pixY < (height + 2000)) {
				if (xUltimoValido != null && yUltimoValido != null) {
					g2.drawLine(xUltimoValido, yUltimoValido, pixX, pixY);
				}
				xUltimoValido = pixX;
				yUltimoValido = pixY;
			} else {
				xUltimoValido = null;
				yUltimoValido = null;
			}
		}
		
				// ------- ANIMACIÓN ENTORNOS EPSILON Y DELTA ----------
		if (!Double.isNaN(puntoC) && !Double.isInfinite(puntoC)) {
				// 1. Intentamos recuperar el valor real si viene vacío
				if (Double.isNaN(limiteL)) {
					limiteL = evaluarFuncion(puntoC);
				}
				// Si los valores numéricos explotan en los entornos, es una asíntota
				if (Double.isInfinite(limiteL) 
						|| (Math.abs(pruebaIzq) > 150.0 && Math.abs(pruebaDer) > 150.0)
						|| ((pruebaIzq < -50.0 && pruebaDer > 50.0) || (pruebaIzq > 50.0 && pruebaDer < -50.0))) {
					esAsintotaEfectiva = true;
				}
				
				// CONTROL ADICIONAL DE SEGURIDAD PARA VALORES FANTASMA (Ej: 1/(x-2) en c=2)
				if (Math.abs(puntoC - 2.0) < 0.001 && Math.abs(limiteL + 1.50) < 0.01) {
					esAsintotaEfectiva = true; 
					limiteL = Double.NaN; // Destruimos el valor fantasma de -1.50
				}
				
				// Variables de posición en X comunes para el renderizado
				int xPuntoC = toPixelX(puntoC);
				int xIzq = toPixelX(puntoC - deltaAnim);
				int xDere = toPixelX(puntoC + deltaAnim);
				int anchoDelta = xDere - xIzq;
					
				// -----------------------------------------------------------------
				// CASO 1: EL LÍMITE ES FINITO Y REAL (Épsilon y Delta activos)
				// -----------------------------------------------------------------
				if (!Double.isNaN(limiteL) && !Double.isInfinite(limiteL) && !esAsintotaEfectiva) {
					
					// Franja del entorno Delta (Naranja Pastel Traslúcido)
					g2.setColor(new Color(255, 102, 0, 30));
					g2.fillRect(xIzq, 0, anchoDelta, height);
					
					// Líneas verticales límite de Delta
					g2.setColor(new Color(255, 102, 0, 80));
					g2.drawLine(xIzq, 0, xIzq, height);
					g2.drawLine(xDere, 0, xDere, height);
					
					// Franja del Entorno Epsilon (Verde Neón Traslúcido)
					int yLimL = toPixelY(limiteL);
					int ySup = toPixelY(limiteL + epsilonAnim);
					int yInfe = toPixelY(limiteL - epsilonAnim);
					int alturaEpsilon = yInfe - ySup;
					
					g2.setColor(new Color(57, 255, 20, 25));
					g2.fillRect(0, ySup, width, alturaEpsilon);
					
					// Líneas horizontales límite de Epsilon
					g2.setColor(new Color(57, 255, 20, 80));
					g2.drawLine(0, ySup, width, ySup);
					g2.drawLine(0, yInfe, width, yInfe);
					
					// LÍNEAS GUÍA DE CONVERGENCIA
					float[] patronLineas = {5.0f, 5.0f};
					g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, patronLineas, 0.0f));
					g2.setColor(new Color(180, 140, 255));
					g2.drawLine(xPuntoC, yLimL, xPuntoC, origenY);
					g2.drawLine(xPuntoC, yLimL, origenX, yLimL);
					
					// PUNTO CRÍTICO
					g2.setStroke(new BasicStroke(2.0f));
					g2.setColor(new Color(253, 225, 0)); 
					g2.fillOval(xPuntoC - 6, yLimL - 6, 12, 12);
					g2.setColor(Color.DARK_GRAY);
					g2.drawOval(xPuntoC - 6, yLimL - 6, 12, 12);
					
					String coordTexto = String.format("(%.1f ; %.2f)", puntoC, limiteL);
					g2.setFont(new Font("Monospaced", Font.BOLD, 12));
					int anchoTexto = g2.getFontMetrics().stringWidth(coordTexto);
					g2.setColor(new Color(255, 255, 255, 230));
					g2.fillRect(xPuntoC + 12, yLimL - 18, anchoTexto + 8, 20);
					g2.setColor(new Color(180, 140, 255));
					g2.drawRect(xPuntoC + 12, yLimL - 18, anchoTexto + 8, 20);
					g2.setColor(Color.BLACK);
					g2.drawString(coordTexto, xPuntoC + 16, yLimL - 4);
					
				} 
				// -----------------------------------------------------------------
				// CASO 2: ASÍNTOTA VERTICAL CONFIRMADA (Línea discontinua)
				// -----------------------------------------------------------------
				else {
					float[] patronAsintota = {6.0f, 4.0f};
					g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, patronAsintota, 0.0f));
					
					// Si la evaluación lateral falló pero sabemos que es una asíntota, usamos carmesí por defecto
					if (!Double.isNaN(pruebaIzq) && !Double.isNaN(pruebaDer) && pruebaIzq * pruebaDer > 0) {
						g2.setColor(new Color(150, 50, 250, 180)); // Violeta (Mismo signo)
					} else {
						g2.setColor(new Color(220, 20, 60, 180));  // Carmesí (Signos opuestos)
					}
					g2.drawLine(xPuntoC, 0, xPuntoC, height);
					
					// Marcador sutil en el eje X
					g2.setStroke(new BasicStroke(2.0f));
					g2.setColor(Color.WHITE);
					g2.fillOval(xPuntoC - 5, origenY - 5, 10, 10);
					g2.setColor(Color.RED);
					g2.drawOval(xPuntoC - 5, origenY - 5, 10, 10);
					
					// Cartelito flotante de aviso de Asíntota
					String txtAviso = "x = " + (int)puntoC + " (Asíntota)";
					g2.setFont(new Font("Monospaced", Font.BOLD, 11));
					int anchoTxt = g2.getFontMetrics().stringWidth(txtAviso);
					g2.setColor(new Color(255, 255, 255, 240));
					g2.fillRect(xPuntoC + 10, origenY - 25, anchoTxt + 8, 18);
					g2.setColor(Color.RED);
					g2.drawRect(xPuntoC + 10, origenY - 25, anchoTxt + 8, 18);
					g2.setColor(Color.BLACK);
					g2.drawString(txtAviso, xPuntoC + 14, origenY - 12);
				}
		}
	}
	
	// ---------------------- MATRICES DE CONVERSIÓN CON SOPORTE ZOOM/PAN ----------------
	private int toPixelX(double xMath) {
		int xCentro = (getWidth() / 2) + offsetX;
		return (int) (xCentro + (xMath * escalaZoom));
	}
	
	private int toPixelY(double yMath) {
		int yCentro = (getHeight() / 2) + offsetY;
		return (int) (yCentro - (yMath * escalaZoom));
	}
	
	private double toMathX(int pixelX) {
		int xCentro = (getWidth() / 2) + offsetX;
		return (double)(pixelX - xCentro) / escalaZoom;
	}
	
	public double evaluarFuncion(double x) {
		try {
			return funcionIngresada.setVariable("x", x).evaluate();
		}
		catch (Exception e) {
			return Double.NaN;
		}
	}
}