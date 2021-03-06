/**
 *************************************************************************************************
 **             Francisco Rodríguez García / Proyecto 2º DAM - Hibernate y Java FX              **
 *************************************************************************************************
 **  _                    _ __  __            _      ____        _        _                     **
 ** | |    ___   ___ __ _| |  \/  | _____   _(_) ___|  _ \  __ _| |_ __ _| |__   __ _ ___  ___  **
 ** | |   / _ \ / __/ _` | | |\/| |/ _ \ \ / | |/ _ | | | |/ _` | __/ _` | '_ \ / _` / __|/ _ \ **
 ** | |__| (_) | (_| (_| | | |  | | (_) \ V /| |  __| |_| | (_| | || (_| | |_) | (_| \__ |  __/ **
 ** |_____\___/ \___\__,_|_|_|  |_|\___/ \_/ |_|\___|____/ \__,_|\__\__,_|_.__/ \__,_|___/\___| **
 **                                                                                             **
 *************************************************************************************************
 */
package com.monroy.lmdb;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import com.monroy.lmdb.dao.ActorDAO;
import com.monroy.lmdb.dao.DirectorDAO;
import com.monroy.lmdb.dao.GenericDAO;
import com.monroy.lmdb.dao.PeliculaDAO;
import com.monroy.lmdb.persistencia.Actor;
import com.monroy.lmdb.persistencia.Director;
import com.monroy.lmdb.persistencia.Genero;
import com.monroy.lmdb.persistencia.HibernateUtil;
import com.monroy.lmdb.persistencia.Pais;
import com.monroy.lmdb.persistencia.Pelicula;
import com.monroy.lmdb.persistencia.PeliculaActor;
import com.monroy.lmdb.vista_controlador.ModificarPeliculaControlador;
import com.monroy.lmdb.vista_controlador.PeliculaControlador;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class Principal extends Application {

	//========================================================================================//
	// CONSTANTES
	//========================================================================================//
	private static final int OPCION_MINIMA = 1;
	private static final int OPCION_MAXIMA_PRINCIPAL = 4;
	private static final int OPCION_MAXIMA_PELICULA = 9;
	private static final int OPCION_MAXIMA_DIRECTOR = 5;
	private static final int OPCION_MAXIMA_DIRECTOR_PELICULA = 2;
	private static final int OPCION_MAXIMA_ACTOR = 6;
	private static final int MINIMO_INDICE_PAIS = 1;
	private static final int MINIMO_INDICE_GENERO = 1;
	private static final int ANCHO_MINIMO_CELDA_ID = 4;
	private static final int ANCHO_MINIMO_CELDA_TITULO_ESPANHA = 19;
	private static final int ANCHO_MINIMO_CELDA_TITULO_ORIGINAL = 18;
	private static final int ANCHO_MINIMO_CELDA_ANHO = 7;
	private static final int ANCHO_MINIMO_CELDA_DURACION = 11;
	private static final int ANCHO_MINIMO_CELDA_PAIS = 7;
	private static final int ANCHO_MINIMO_CELDA_DIRECTOR = 14;
	private static final int ANCHO_MINIMO_CELDA_GENERO = 9;
	private static final int ANCHO_MINIMO_CELDA_NOMBRE = 9;
	private static final int ANCHO_MINIMO_CELDA_REPARTO = 14;
	
	//========================================================================================//
	// VRIABLES ESTÁTICAS
	//========================================================================================//
	private static Scanner teclado = new Scanner(System.in);
	public static Session sesion;
	public static GenericDAO<PeliculaActor> genericDao = new GenericDAO<>();
	public static PeliculaDAO peliculaDao = new PeliculaDAO();
	public static ActorDAO actorDao = new ActorDAO();
	public static DirectorDAO directorDao = new DirectorDAO();
    public static ObservableList<Pelicula> datosPeliculas = FXCollections.observableArrayList();
	
	//========================================================================================//
	// VARIABLES
	//========================================================================================//
	private Stage escenarioPrincipal, escenarioDialogo;
	private Scene escenaRaiz, escenaModificarPelicula;
	private BorderPane panelRaiz;
	private AnchorPane panelPelicula, panelModificarPelicula;
    private FXMLLoader cargadorFxml;
    private PeliculaControlador controladorPelicula;
    private ModificarPeliculaControlador controladorModificarPelicula;
    private Alert alerta;
	
	//========================================================================================//
	// MÉTODO PRINCIPAL
	//========================================================================================//
    /**
     * Principal.
     * @param args Argumentos.
     */
	public static void main(String[] args) {
		// INTERFAZ GRÁFICA
		launch(args); // Se llama al método start(), que lanza la interfaz gráfica.
		
		// CONSOLA
		// Comentar el launch, y descomentar las siguientes líneas para usar por consola.
		//Principal.configurarSesion();
		//Principal.mostrarMenuPrincipal();
		//Principal.cerrarSesion();
	}

	//========================================================================================//
	// MÉTODOS DE LA SESIÓN
	//========================================================================================//
	/**
	 * Metodo que crea y configura la sesion en Hibernate.
	 */
	public static void configurarSesion() {
		HibernateUtil.buildSessionFactory();
		HibernateUtil.openSessionAndBindToThread();
		sesion = HibernateUtil.getSessionFactory().getCurrentSession();
	}
	
	/**
	 * Metodo que cierra la sesion en Hibernate.
	 */
	public static void cerrarSesion() {
		HibernateUtil.closeSessionFactory();
	}
	
	//========================================================================================//
  	// LANZAMIENTO JAVA FX
  	//========================================================================================//
	/**
	 * Lanza la aplicación gráfica.
	 */
	@Override
	public void start(Stage escenarioPrincipal) {
		try {
			Principal.configurarSesion();
			
            Principal.cargarDatosPeliculas(); // Se cargan los datos de las películas.
			
			this.escenarioPrincipal = escenarioPrincipal;
	        this.escenarioPrincipal.setTitle("LocalMovieDatabase");
	        this.escenarioPrincipal.getIcons().add(new Image("file:resources/img/claqueta.png"));
	        
	        iniciarEscenaRaiz();
	        
	        iniciarEscenaPelicula();
	        
	        Principal.cerrarSesion();
			
		} catch(Exception e) {
			alerta = new Alert(AlertType.ERROR);
	    	alerta.setTitle("LocalMovieDatabase");
	    	alerta.setHeaderText("Error");
	    	alerta.setContentText(e.getMessage());
	    	alerta.showAndWait();
	    	
	    	Principal.cerrarSesion();
	    	System.exit(0);
		}
	}

	//========================================================================================//
  	// MÉTODOS PARA JAVAFX
  	//========================================================================================//
	/**
	 * Inicia la escena Raíz.
	 * 
	 * @throws LmdbException Puede devolver una excepción Lmdb.
	 */
	private void iniciarEscenaRaiz() throws LmdbException {
		try {
            // Se inicia el cargardor de FXML.
            cargadorFxml = new FXMLLoader();

            // Se define el FXML a cargar.
            cargadorFxml.setLocation(Principal.class.getResource("vista_controlador/Raiz.fxml"));

            // Se carga el FXML en un BorderPane.
            panelRaiz = (BorderPane) cargadorFxml.load();

            // Se crea una escena con el panel cargado.
            escenaRaiz = new Scene(panelRaiz);
            escenaRaiz.getStylesheets().add(Principal.class.getResource("vista_controlador/bootstrap3.css").toExternalForm());

            // Se pone la escena en el escenario.
            escenarioPrincipal.setScene(escenaRaiz);
            escenarioPrincipal.setResizable(false);

            // Se muestra el escenario.
            escenarioPrincipal.show();

        } catch (IOException e) {
	    	throw new LmdbException("Error al cargar el archivo FXML.");

        } catch (IllegalStateException ex) {
        	throw new LmdbException("No se encuentra el archivo FXML.");
        }
	}
	
	/**
	 * Inicia la escena Pelicula.
	 * @throws LmdbException Puede devolver una excepción Lmdb.
	 */
	private void iniciarEscenaPelicula() throws LmdbException {
		try {
			// Se inicia el cargador de FXML.
            cargadorFxml = new FXMLLoader();

            // Se define el FXML a cargar.
            cargadorFxml.setLocation(Principal.class.getResource("vista_controlador/Pelicula.fxml"));

            // Se carga el FXML en un AnchorPane.
            panelPelicula = (AnchorPane) cargadorFxml.load();

            // Se pone el panel de la descripción de la persona en el centro del panel raíz.
            panelRaiz.setCenter(panelPelicula);

            // Se da acceso al pricipal desde el controlador.
            controladorPelicula = cargadorFxml.getController();
            controladorPelicula.setPrincipal(this);

        } catch (IOException e) {
	    	//throw new LmdbException("Error al cargar el archivo FXML este otro.");
        	e.printStackTrace();

        } catch (IllegalStateException ex) {
        	throw new LmdbException("No se encuentra el archivo FXML.");
        }
	}

	/**
	 * Carga los datos de las películas en una lista.
	 */
	public static void cargarDatosPeliculas() {
		List<Pelicula> listaPeliculas;
		
		try {
			listaPeliculas = peliculaDao.listarPeliculas();
			
			for (Pelicula pelicula : listaPeliculas) {
				datosPeliculas.add(pelicula);
			}
			
		} catch (LmdbException e) {
			listaPeliculas = new ArrayList<Pelicula>();
		}
	}
	
	/**
	 * Obtiene los datos de las películas.
	 * @return Devuelve la lista con los datos de las películas.
	 */
	public ObservableList<Pelicula> obtenerDatosPeliculas() {
        return datosPeliculas;
    }
	
	/**
	 * Añade una película a la lista.
	 * @param pelicula Película a añadir.
	 */
	public static void anhadirPeliculaALista(Pelicula pelicula) {
		datosPeliculas.add(pelicula);
	}
	
	/**
	 * Muestra el escenario de ModificarPelicula.
	 * @param pelicula Pelicula a crear o modificar.
	 * @return Devuelve un boolean indicando si se ha pulsado el botón OK.
	 */
	public boolean mostrarModificarPelicula(Pelicula pelicula) {
		try {
			// Se inicia el cargador de FXML.
            cargadorFxml = new FXMLLoader();

            // Se define el FXML a cargar.
            cargadorFxml.setLocation(Principal.class.getResource("vista_controlador/ModificarPelicula.fxml"));

            // Se carga el FXML en un AnchorPane.
            panelModificarPelicula = (AnchorPane) cargadorFxml.load();

            // Se crea y configura el escenario de diálogo.
            escenarioDialogo = new Stage();
            escenarioDialogo.setTitle("LocalMovieDatabase");
            escenarioDialogo.initModality(Modality.WINDOW_MODAL);
            escenarioDialogo.initOwner(escenarioPrincipal);
            escenarioDialogo.setResizable(false);
            escenarioDialogo.getIcons().add(new Image("file:resources/img/claqueta.png"));
            
            // Se pone la escena en el escenario.
            escenaModificarPelicula = new Scene(panelModificarPelicula);
            escenaModificarPelicula.getStylesheets().add(Principal.class.getResource("vista_controlador/bootstrap3.css").toExternalForm());
            escenarioDialogo.setScene(escenaModificarPelicula);
            
            // Se pasan parámetros al controlador.
            controladorModificarPelicula = cargadorFxml.getController();
            controladorModificarPelicula.setEscenarioDialogo(escenarioDialogo);
            controladorModificarPelicula.setPelicula(pelicula);
            
            // Se muestra el escenario.
            escenarioDialogo.showAndWait();

            // Devuelve si el botón OK ha sido pulsado.
            return controladorModificarPelicula.okPulsado();
            
        } catch (IOException e) {
        	e.printStackTrace();
        	return false;

        } catch (IllegalStateException ex) {
        	ex.printStackTrace();
        	return false;
        }
	}
	
	//========================================================================================//
	// MÉTODOS DEL MENÚ PRINCIPAL
	//========================================================================================//
	/**
	 * Metodo que muestra el menu principal.
	 */
	private static void mostrarMenuPrincipal() {
		int opcion;
		
		System.out.println("====================");
		System.out.println("|| MENÚ PRINCIPAL ||");
		System.out.println("====================");
		System.out.println("[1] PELÍCULAS");
		System.out.println("[2] DIRECTORES/AS");
		System.out.println("[3] ACTORES/ACTRICES");
		System.out.println("[4] SALIR");
		
		opcion = Principal.solicitarOpcion(OPCION_MAXIMA_PRINCIPAL);
		Principal.tratarMenuPrincipal(opcion);
	}
	
	/**
	 * Metodo que trata la opcion del menu principal elegida.
	 * @param opcion Opcion a tratar.
	 */
	private static void tratarMenuPrincipal(int opcion) {
		switch (opcion) {
		case 1:
			// [1] PELÍCULAS
			Principal.mostrarMenuPelicula();
			break;
		case 2:
			// [2] DIRECTORES
			Principal.mostrarMenuDirector();
			break;
		case 3:
			// [3] ACTORES
			Principal.mostrarMenuActor();
			break;
		default:
			// [4] SALIR
			System.exit(0);
			break;
		}
	}

	//========================================================================================//
	// MÉTODOS DEL MENÚ PELÍCULAS
	//========================================================================================//
	/**
	 * Metodo que muestra el menu de peliculas.
	 */
	private static void mostrarMenuPelicula() {
		int opcion;
		
		System.out.println();
		System.out.println("====================");
		System.out.println("|| MENÚ PELÍCULAS ||");
		System.out.println("====================");
		System.out.println("[1] Consultar todas las películas");
		System.out.println("[2] Consultar películas por año");
		System.out.println("[3] Consultar películas por país");
		System.out.println("[4] Consultar películas por género");
		System.out.println("[5] Consultar películas por rango de duración");
		System.out.println("[6] Añadir una película");
		System.out.println("[7] Modificar una película");
		System.out.println("[8] Eliminar una película");
		System.out.println("[9] Volver al menú principal");
		
		opcion = Principal.solicitarOpcion(OPCION_MAXIMA_PELICULA);
		Principal.tratarOpcionMenuPelicula(opcion);
	}
	
	/**
	 * Metodo que trata la opcion del menu pelicula elegida.
	 * @param opcion Opcion elegida.
	 */
	private static void tratarOpcionMenuPelicula(int opcion) {
		switch (opcion) {
		case 1:
			// [1] Consultar todas las películas
			System.out.println();
			Principal.mostrarPeliculas();
			break;
		case 2:
			// [2] Consultar películas por año
			System.out.println();
			Principal.mostrarPeliculasPorAnho();
			break;
		case 3:
			// [3] Consultar películas por país
			System.out.println();
			Principal.mostrarPeliculasPorPais();
			break;
		case 4:
			// [4] Consultar películas por género
			System.out.println();
			Principal.mostrarPeliculasPorGenero();
			break;
		case 5:
			// [5] Consultar películas por rango de duración
			System.out.println();
			Principal.mostrarPeliculasPorRangoDuracion();
			break;
		case 6:
			// [6] Añadir una película
			System.out.println();
			Principal.altaPelicula();
			break;
		case 7:
			// [7] Modificar una película
			System.out.println();
			Principal.modificarPelicula();
			break;
		case 8:
			// [8] Eliminar una película
			System.out.println();
			Principal.bajaPelicula();
			break;
		default:
			// [9] Volver al menú principal
			System.out.println();
			Principal.mostrarMenuPrincipal();
			break;
		}
	}

	/**
	 * Metodo que muestra todas las peliculas.
	 */
	private static void mostrarPeliculas() {
		List<Pelicula> listaPeliculas = null;
		SimpleDateFormat formatoAnho;
		int maximoTituloEspanha, maximoTituloOriginal, maximoPais, maximoDirector, maximoGenero, maximoReparto;
		String director, reparto;
		
		try {
			listaPeliculas = peliculaDao.listarPeliculas();
			formatoAnho = new SimpleDateFormat("yyyy");
			
			maximoTituloEspanha = ANCHO_MINIMO_CELDA_TITULO_ESPANHA;
			maximoTituloOriginal = ANCHO_MINIMO_CELDA_TITULO_ORIGINAL;
			maximoPais = ANCHO_MINIMO_CELDA_PAIS;
			maximoDirector = ANCHO_MINIMO_CELDA_DIRECTOR;
			maximoGenero = ANCHO_MINIMO_CELDA_GENERO;
			maximoReparto = ANCHO_MINIMO_CELDA_REPARTO;
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getTituloEspanha().length() + 2 >= maximoTituloEspanha) {
					maximoTituloEspanha = pelicula.getTituloEspanha().length() + 3;
				}
				
				if (pelicula.getTituloOriginal().length() + 2 >= maximoTituloOriginal) {
					maximoTituloOriginal = pelicula.getTituloOriginal().length() + 3;
				}
				
				if (pelicula.getPais().toString().length() + 2 >= maximoPais) {
					maximoPais = pelicula.getPais().toString().length() + 3;
				}
				
				if (pelicula.getDirector() != null) {
					if (pelicula.getDirector().getNombre().length() + 2 >= maximoDirector) {
						maximoDirector = pelicula.getDirector().getNombre().length() + 3;
					}
				}
				
				if (pelicula.getGenero().toString().length() + 2 >= maximoGenero) {
					maximoGenero = pelicula.getGenero().toString().length() + 3;
				}
				
				if (pelicula.getActores() != null ) {
					if (pelicula.cadenaActores().length() + 2 >= maximoReparto) {
						maximoReparto = pelicula.cadenaActores().length() + 3;
					}
				}
			}
			
			System.out.println("==========================");
			System.out.println("|| LISTADO DE PELÍCULAS ||");
			System.out.println("==========================");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					" ID", "| TÍTULO EN ESPAÑA", "| TÍTULO ORIGINAL", "| AÑO", "| DURACIÓN", "| PAÍS", "| DIRECTOR", "| GÉNERO", "| REPARTO");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getDirector() != null) {
					director = pelicula.getDirector().getNombre();
				} else {
					director = "Sin asignar";
				}
				
				if (pelicula.getActores() != null) {
					if (pelicula.getActores().size() != 0) {
						reparto = pelicula.cadenaActores();
					} else {
						reparto = "Sin asignar";
					}
				} else {
					reparto = "Sin asignar";
				}
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						" " + pelicula.getId(), "| " + pelicula.getTituloEspanha(), "| " + pelicula.getTituloOriginal(), "| " + Integer.parseInt(formatoAnho.format(pelicula.getAnho())), "| " + pelicula.getDuracion() + " min.", "| " + pelicula.getPais().toString(), "| " + director, "| " + pelicula.getGenero().toString(), "| " + reparto);
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoTituloEspanha), crearCadenaGuion(maximoTituloOriginal), crearCadenaGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaGuion(maximoPais), crearCadenaGuion(maximoDirector), crearCadenaGuion(maximoGenero), crearCadenaGuion(maximoReparto));
				
			}
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuPelicula();
	}
	
	/**
	 * Metodo que muestra todas las peliculas segun un anho.
	 */
	private static void mostrarPeliculasPorAnho() {
		List<Pelicula> listaPeliculas;
		SimpleDateFormat formatoAnho;
		int maximoTituloEspanha, maximoTituloOriginal, maximoPais, maximoDirector, maximoGenero, maximoReparto;
		String director, reparto;
		Date anho;
		
		try {
			formatoAnho = new SimpleDateFormat("yyyy");
			anho = solicitarAnho(">>> Año de la consulta: ");
			listaPeliculas = peliculaDao.listarPeliculasPorAnho(formatoAnho.format(anho));
			
			maximoTituloEspanha = ANCHO_MINIMO_CELDA_TITULO_ESPANHA;
			maximoTituloOriginal = ANCHO_MINIMO_CELDA_TITULO_ORIGINAL;
			maximoPais = ANCHO_MINIMO_CELDA_PAIS;
			maximoDirector = ANCHO_MINIMO_CELDA_DIRECTOR;
			maximoGenero = ANCHO_MINIMO_CELDA_GENERO;
			maximoReparto = ANCHO_MINIMO_CELDA_REPARTO;
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getTituloEspanha().length() + 2 >= maximoTituloEspanha) {
					maximoTituloEspanha = pelicula.getTituloEspanha().length() + 3;
				}
				
				if (pelicula.getTituloOriginal().length() + 2 >= maximoTituloOriginal) {
					maximoTituloOriginal = pelicula.getTituloOriginal().length() + 3;
				}
				
				if (pelicula.getPais().toString().length() + 2 >= maximoPais) {
					maximoPais = pelicula.getPais().toString().length() + 3;
				}
				
				if (pelicula.getDirector() != null) {
					if (pelicula.getDirector().getNombre().length() + 2 >= maximoDirector) {
						maximoDirector = pelicula.getDirector().getNombre().length() + 3;
					}
				}
				
				if (pelicula.getGenero().toString().length() + 2 >= maximoGenero) {
					maximoGenero = pelicula.getGenero().toString().length() + 3;
				}
				
				if (pelicula.getActores() != null ) {
					if (pelicula.cadenaActores().length() + 2 >= maximoReparto) {
						maximoReparto = pelicula.cadenaActores().length() + 3;
					}
				}
			}
			
			System.out.println("========================================");
			System.out.println("|| LISTADO DE PELÍCULAS POR AÑO: " + String.valueOf(formatoAnho.format(anho)) + " ||");
			System.out.println("========================================");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					" ID", "| TÍTULO EN ESPAÑA", "| TÍTULO ORIGINAL", "| AÑO", "| DURACIÓN", "| PAÍS", "| DIRECTOR", "| GÉNERO", "| REPARTO");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getDirector() != null) {
					director = pelicula.getDirector().getNombre();
				} else {
					director = "Sin asignar";
				}
				
				if (pelicula.getActores() != null) {
					if (pelicula.getActores().size() != 0) {
						reparto = pelicula.cadenaActores();
					} else {
						reparto = "Sin asignar";
					}
				} else {
					reparto = "Sin asignar";
				}
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						" " + pelicula.getId(), "| " + pelicula.getTituloEspanha(), "| " + pelicula.getTituloOriginal(), "| " + Integer.parseInt(formatoAnho.format(pelicula.getAnho())), "| " + pelicula.getDuracion() + " min.", "| " + pelicula.getPais().toString(), "| " + director, "| " + pelicula.getGenero().toString(), "| " + reparto);
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoTituloEspanha), crearCadenaGuion(maximoTituloOriginal), crearCadenaGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaGuion(maximoPais), crearCadenaGuion(maximoDirector), crearCadenaGuion(maximoGenero), crearCadenaGuion(maximoReparto));
				
			}
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuPelicula();
	}
	
	/**
	 * Metodo que muestra todas las peliculas segun un pais.
	 */
	private static void mostrarPeliculasPorPais() {
		List<Pelicula> listaPeliculas;
		SimpleDateFormat formatoAnho;
		int maximoTituloEspanha, maximoTituloOriginal, maximoPais, maximoDirector, maximoGenero, maximoReparto, indicePais, contadorPais = 1;
		String director, reparto;
		
		try {
			for (Pais paisActual : Pais.values()) {
				System.out.println(contadorPais + " - " + paisActual.toString());
				contadorPais++;
			}
			
			indicePais = solicitarEntero(">>> Índice de país de la consulta: ");
			listaPeliculas = peliculaDao.listarPeliculasPorPais(indicePais - 1);
			formatoAnho = new SimpleDateFormat("yyyy");
			
			maximoTituloEspanha = ANCHO_MINIMO_CELDA_TITULO_ESPANHA;
			maximoTituloOriginal = ANCHO_MINIMO_CELDA_TITULO_ORIGINAL;
			maximoPais = ANCHO_MINIMO_CELDA_PAIS;
			maximoDirector = ANCHO_MINIMO_CELDA_DIRECTOR;
			maximoGenero = ANCHO_MINIMO_CELDA_GENERO;
			maximoReparto = ANCHO_MINIMO_CELDA_REPARTO;
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getTituloEspanha().length() + 2 >= maximoTituloEspanha) {
					maximoTituloEspanha = pelicula.getTituloEspanha().length() + 3;
				}
				
				if (pelicula.getTituloOriginal().length() + 2 >= maximoTituloOriginal) {
					maximoTituloOriginal = pelicula.getTituloOriginal().length() + 3;
				}
				
				if (pelicula.getPais().toString().length() + 2 >= maximoPais) {
					maximoPais = pelicula.getPais().toString().length() + 3;
				}
				
				if (pelicula.getDirector() != null) {
					if (pelicula.getDirector().getNombre().length() + 2 >= maximoDirector) {
						maximoDirector = pelicula.getDirector().getNombre().length() + 3;
					}
				}
				
				if (pelicula.getGenero().toString().length() + 2 >= maximoGenero) {
					maximoGenero = pelicula.getGenero().toString().length() + 3;
				}
				
				if (pelicula.getActores() != null ) {
					if (pelicula.cadenaActores().length() + 2 >= maximoReparto) {
						maximoReparto = pelicula.cadenaActores().length() + 3;
					}
				}
			}
			
			System.out.println(crearCadenaDobleGuion(37 + solicitarPais(indicePais).toString().length()));
			System.out.println("|| LISTADO DE PELÍCULAS POR PAIS: " + solicitarPais(indicePais).toString() + " ||");
			System.out.println(crearCadenaDobleGuion(37 + solicitarPais(indicePais).toString().length()));
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					" ID", "| TÍTULO EN ESPAÑA", "| TÍTULO ORIGINAL", "| AÑO", "| DURACIÓN", "| PAÍS", "| DIRECTOR", "| GÉNERO", "| REPARTO");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getDirector() != null) {
					director = pelicula.getDirector().getNombre();
				} else {
					director = "Sin asignar";
				}
				
				if (pelicula.getActores() != null) {
					if (pelicula.getActores().size() != 0) {
						reparto = pelicula.cadenaActores();
					} else {
						reparto = "Sin asignar";
					}
				} else {
					reparto = "Sin asignar";
				}
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						" " + pelicula.getId(), "| " + pelicula.getTituloEspanha(), "| " + pelicula.getTituloOriginal(), "| " + Integer.parseInt(formatoAnho.format(pelicula.getAnho())), "| " + pelicula.getDuracion() + " min.", "| " + pelicula.getPais().toString(), "| " + director, "| " + pelicula.getGenero().toString(), "| " + reparto);
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoTituloEspanha), crearCadenaGuion(maximoTituloOriginal), crearCadenaGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaGuion(maximoPais), crearCadenaGuion(maximoDirector), crearCadenaGuion(maximoGenero), crearCadenaGuion(maximoReparto));
				
			}
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuPelicula();
	}
	
	/**
	 * Metodo que muestra todas las peliculas segun un genero.
	 */
	private static void mostrarPeliculasPorGenero() {
		List<Pelicula> listaPeliculas;
		SimpleDateFormat formatoAnho;
		int maximoTituloEspanha, maximoTituloOriginal, maximoPais, maximoDirector, maximoGenero, maximoReparto, indiceGenero, contadorGenero = 1;
		String director, reparto;
		
		try {
			for (Genero generoActual : Genero.values()) {
				System.out.println(contadorGenero + " - " + generoActual.toString());
				contadorGenero++;
			}
			
			indiceGenero = solicitarEntero(">>> Índice de género de la consulta: ");
			listaPeliculas = peliculaDao.listarPeliculasPorGenero(indiceGenero - 1);
			formatoAnho = new SimpleDateFormat("yyyy");
			
			maximoTituloEspanha = ANCHO_MINIMO_CELDA_TITULO_ESPANHA;
			maximoTituloOriginal = ANCHO_MINIMO_CELDA_TITULO_ORIGINAL;
			maximoPais = ANCHO_MINIMO_CELDA_PAIS;
			maximoDirector = ANCHO_MINIMO_CELDA_DIRECTOR;
			maximoGenero = ANCHO_MINIMO_CELDA_GENERO;
			maximoReparto = ANCHO_MINIMO_CELDA_REPARTO;
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getTituloEspanha().length() + 2 >= maximoTituloEspanha) {
					maximoTituloEspanha = pelicula.getTituloEspanha().length() + 3;
				}
				
				if (pelicula.getTituloOriginal().length() + 2 >= maximoTituloOriginal) {
					maximoTituloOriginal = pelicula.getTituloOriginal().length() + 3;
				}
				
				if (pelicula.getPais().toString().length() + 2 >= maximoPais) {
					maximoPais = pelicula.getPais().toString().length() + 3;
				}
				
				if (pelicula.getDirector() != null) {
					if (pelicula.getDirector().getNombre().length() + 2 >= maximoDirector) {
						maximoDirector = pelicula.getDirector().getNombre().length() + 3;
					}
				}
				
				if (pelicula.getGenero().toString().length() + 2 >= maximoGenero) {
					maximoGenero = pelicula.getGenero().toString().length() + 3;
				}
				
				if (pelicula.getActores() != null ) {
					if (pelicula.cadenaActores().length() + 2 >= maximoReparto) {
						maximoReparto = pelicula.cadenaActores().length() + 3;
					}
				}
			}
			
			System.out.println(crearCadenaDobleGuion(39 + solicitarGenero(indiceGenero).toString().length()));
			System.out.println("|| LISTADO DE PELÍCULAS POR GENERO: " + solicitarGenero(indiceGenero).toString() + " ||");
			System.out.println(crearCadenaDobleGuion(39 + solicitarGenero(indiceGenero).toString().length()));
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					" ID", "| TÍTULO EN ESPAÑA", "| TÍTULO ORIGINAL", "| AÑO", "| DURACIÓN", "| PAÍS", "| DIRECTOR", "| GÉNERO", "| REPARTO");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getDirector() != null) {
					director = pelicula.getDirector().getNombre();
				} else {
					director = "Sin asignar";
				}
				
				if (pelicula.getActores() != null) {
					if (pelicula.getActores().size() != 0) {
						reparto = pelicula.cadenaActores();
					} else {
						reparto = "Sin asignar";
					}
				} else {
					reparto = "Sin asignar";
				}
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						" " + pelicula.getId(), "| " + pelicula.getTituloEspanha(), "| " + pelicula.getTituloOriginal(), "| " + Integer.parseInt(formatoAnho.format(pelicula.getAnho())), "| " + pelicula.getDuracion() + " min.", "| " + pelicula.getPais().toString(), "| " + director, "| " + pelicula.getGenero().toString(), "| " + reparto);
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoTituloEspanha), crearCadenaGuion(maximoTituloOriginal), crearCadenaGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaGuion(maximoPais), crearCadenaGuion(maximoDirector), crearCadenaGuion(maximoGenero), crearCadenaGuion(maximoReparto));
				
			}
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuPelicula();
	}
	
	/**
	 * Metodo que muestra todas las peliculas segun un rango de duracion.
	 */
	private static void mostrarPeliculasPorRangoDuracion() {
		List<Pelicula> listaPeliculas;
		SimpleDateFormat formatoAnho;
		int maximoTituloEspanha, maximoTituloOriginal, maximoPais, maximoDirector, maximoGenero, maximoReparto, duracionMinima, duracionMaxima;
		String director, reparto;
		
		try {
			duracionMinima = Principal.solicitarEntero(">>> Duración mínima: ");
			duracionMaxima = Principal.solicitarEntero(">>> Duración máxima: ");
			listaPeliculas = peliculaDao.listarPeliculasPorRangoDuracion(duracionMinima, duracionMaxima);
			formatoAnho = new SimpleDateFormat("yyyy");
			
			maximoTituloEspanha = ANCHO_MINIMO_CELDA_TITULO_ESPANHA;
			maximoTituloOriginal = ANCHO_MINIMO_CELDA_TITULO_ORIGINAL;
			maximoPais = ANCHO_MINIMO_CELDA_PAIS;
			maximoDirector = ANCHO_MINIMO_CELDA_DIRECTOR;
			maximoGenero = ANCHO_MINIMO_CELDA_GENERO;
			maximoReparto = ANCHO_MINIMO_CELDA_REPARTO;
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getTituloEspanha().length() + 2 >= maximoTituloEspanha) {
					maximoTituloEspanha = pelicula.getTituloEspanha().length() + 3;
				}
				
				if (pelicula.getTituloOriginal().length() + 2 >= maximoTituloOriginal) {
					maximoTituloOriginal = pelicula.getTituloOriginal().length() + 3;
				}
				
				if (pelicula.getPais().toString().length() + 2 >= maximoPais) {
					maximoPais = pelicula.getPais().toString().length() + 3;
				}
				
				if (pelicula.getDirector() != null) {
					if (pelicula.getDirector().getNombre().length() + 2 >= maximoDirector) {
						maximoDirector = pelicula.getDirector().getNombre().length() + 3;
					}
				}
				
				if (pelicula.getGenero().toString().length() + 2 >= maximoGenero) {
					maximoGenero = pelicula.getGenero().toString().length() + 3;
				}
				
				if (pelicula.getActores() != null ) {
					if (pelicula.cadenaActores().length() + 2 >= maximoReparto) {
						maximoReparto = pelicula.cadenaActores().length() + 3;
					}
				}
			}
			
			System.out.println(crearCadenaDobleGuion(53 + String.valueOf(duracionMinima).length() + String.valueOf(duracionMaxima).length()));
			System.out.println("|| LISTADO DE PELÍCULAS POR RANGO DE DURACIÓN: " + duracionMinima + " - " + duracionMaxima + " ||");
			System.out.println(crearCadenaDobleGuion(53 + String.valueOf(duracionMinima).length() + String.valueOf(duracionMaxima).length()));
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					" ID", "| TÍTULO EN ESPAÑA", "| TÍTULO ORIGINAL", "| AÑO", "| DURACIÓN", "| PAÍS", "| DIRECTOR", "| GÉNERO", "| REPARTO");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
					crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(maximoTituloOriginal), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaDobleGuion(maximoPais), crearCadenaDobleGuion(maximoDirector), crearCadenaDobleGuion(maximoGenero), crearCadenaDobleGuion(maximoReparto));
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getDirector() != null) {
					director = pelicula.getDirector().getNombre();
				} else {
					director = "Sin asignar";
				}
				
				if (pelicula.getActores() != null) {
					if (pelicula.getActores().size() != 0) {
						reparto = pelicula.cadenaActores();
					} else {
						reparto = "Sin asignar";
					}
				} else {
					reparto = "Sin asignar";
				}
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						" " + pelicula.getId(), "| " + pelicula.getTituloEspanha(), "| " + pelicula.getTituloOriginal(), "| " + Integer.parseInt(formatoAnho.format(pelicula.getAnho())), "| " + pelicula.getDuracion() + " min.", "| " + pelicula.getPais().toString(), "| " + director, "| " + pelicula.getGenero().toString(), "| " + reparto);
				
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + maximoTituloOriginal + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s%-" + ANCHO_MINIMO_CELDA_DURACION + "s%-" + maximoPais + "s%-" + maximoDirector + "s%-" + maximoGenero + "s%-" + maximoReparto + "s\n",
						crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoTituloEspanha), crearCadenaGuion(maximoTituloOriginal), crearCadenaGuion(ANCHO_MINIMO_CELDA_ANHO), crearCadenaGuion(ANCHO_MINIMO_CELDA_DURACION), crearCadenaGuion(maximoPais), crearCadenaGuion(maximoDirector), crearCadenaGuion(maximoGenero), crearCadenaGuion(maximoReparto));
				
			}
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuPelicula();
	}
	
	/**
	 * Metodo que muestra las peliculas de forma simplificada.
	 */
	private static void mostrarPeliculasSimplifacado() {
		List<Pelicula> listaPeliculas;
		SimpleDateFormat formatoAnho;
		int maximoTituloEspanha;
		
		try {
			listaPeliculas = peliculaDao.listarPeliculas();
			formatoAnho = new SimpleDateFormat("yyyy");
			
			maximoTituloEspanha = ANCHO_MINIMO_CELDA_TITULO_ESPANHA;
			
			for (Pelicula pelicula : listaPeliculas) {
				if (pelicula.getTituloEspanha().length() + 2 >= maximoTituloEspanha) {
					maximoTituloEspanha = pelicula.getTituloEspanha().length() + 3;
				}
			}
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO));
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s\n", " ID", "| TÍTULO EN ESPAÑA", "| AÑO");
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoTituloEspanha), crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ANHO));
			
			for (Pelicula pelicula : listaPeliculas) {
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s\n", " " + pelicula.getId(), "| " + pelicula.getTituloEspanha(), "| " + Integer.parseInt(formatoAnho.format(pelicula.getAnho())));
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoTituloEspanha + "s%-" + ANCHO_MINIMO_CELDA_ANHO + "s\n", crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoTituloEspanha), crearCadenaGuion(ANCHO_MINIMO_CELDA_ANHO));
			}
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Metodo que da de alta una pelicula.
	 */
	private static void altaPelicula() {
		Pelicula nuevaPelicula;
		String tituloEspanha, tituloOriginal;
		Date anho;
		int duracion;
		Director director;
		Pais pais;
		Genero genero;
		Set<PeliculaActor> actores;
		
		System.out.println("======================");
		System.out.println("|| ALTA DE PELÍCULA ||");
		System.out.println("======================");
		
		try {
			tituloEspanha = Principal.solicitarCadena(">>> Título en España: ");
			tituloOriginal = Principal.solicitarCadena(">>> Título original: ");
			anho = Principal.solicitarAnho(">>> Año: ");
			duracion = Principal.solicitarEntero(">>> Duración: ");
			pais = Principal.solicitarPais(">>> Índice de país: ");
			director = Principal.tratarDirectorPelicula();
			genero = Principal.solicitarGenero(">>> Índice de género: ");
			actores = null;
			
			nuevaPelicula = new Pelicula(tituloEspanha, tituloOriginal, anho, duracion, pais, director, genero, actores);
			
			peliculaDao.guardar(nuevaPelicula);
			
			System.out.println("Se ha añadido la película " + tituloEspanha + ".");
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuPelicula();
	}

	/**
	 * Metodo que modifica una pelicula.
	 */
	private static void modificarPelicula() {
		Pelicula pelicula;
		int id, duracion;
		Date anho;
		String tituloEspanha, tituloOriginal;
		Pais pais;
		Genero genero;
		Director director;
		Set<PeliculaActor> actores;
		
		try {
			Principal.mostrarPeliculasSimplifacado();
			id = Principal.solicitarEntero(">>> ID de la película: ");
			pelicula = peliculaDao.localizar(id);
			
			tituloEspanha = Principal.solicitarCadena(">>> Nuevo título en España: ");
			tituloOriginal = Principal.solicitarCadena(">>> Nuevo título original: ");
			anho = Principal.solicitarAnho(">>> Nuevo año: ");
			duracion = Principal.solicitarEntero(">>> Nueva duración: ");
			pais = Principal.solicitarPais(">>> Nuevo índice de país: ");
			director = Principal.tratarDirectorPelicula();
			genero = Principal.solicitarGenero(">>> Nuevo índice de género: ");
			actores = null;
			
			pelicula.setTituloEspanha(tituloEspanha);
			pelicula.setTituloOriginal(tituloOriginal);
			pelicula.setAnho(anho);
			pelicula.setDuracion(duracion);
			pelicula.setPais(pais);
			pelicula.setDirector(director);
			pelicula.setGenero(genero);
			pelicula.setActores(actores);
			
			peliculaDao.actualizar(pelicula);
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuPelicula();
	}

	/**
	 * Metodo que trata el director al crear una pelicula.
	 * @return Director Devuelve el director.
	 */
	private static Director tratarDirectorPelicula() {
		Director director = null;
		int opcion = 0;
		boolean hayError;
		
		do {
			try {
				hayError = false;
				
				System.out.print(">>> ¿Quieres añadir un director/a existente? (1)\n"
						+ "    ¿O quieres crear un nuevo director/a? (2)\n"
						+ "    Respuesta: ");
				
				opcion = Integer.parseInt(teclado.nextLine());
				
				if (opcion < OPCION_MINIMA || opcion > OPCION_MAXIMA_DIRECTOR_PELICULA) {
					hayError = true;
				}
			} catch (NumberFormatException nfe) {
				hayError = true;
			}
		} while (hayError);
		
		switch (opcion) {
		case 1:
			// Añadir director/a existente.
			director = Principal.anhadirDirectorPelicula(director);
			break;
		case 2:
			// Crear un nuevo director/a.
			director = Principal.altaDirectorPelicula(director);
			break;
		default:
			break;
		}
		
		return director;
	}

	/**
	 * Metodo que anhade un director durante la creacion de la pelicula.
	 * @param director Director a anhadir.
	 * @return Devuelve el director.
	 */
	private static Director anhadirDirectorPelicula(Director director) {
		int id;
		
		try {
			if (directorDao.listarDirectores() != null) {
				Principal.mostrarDirectoresSimplificado();
				id = Principal.solicitarEntero(">>> ID del director/a: ");
				
				try {
					director = directorDao.localizar(id);
					
					
				} catch (LmdbException e) {
					System.out.println(e.getMessage());
					Principal.altaDirectorPelicula(director);
				}
			}
		} catch (LmdbException e1) {
			System.out.println(">>> Actualmente no hay directores/as, tendrás que crearlo.");
			Principal.altaDirectorPelicula(director);
		}
		
		return director;
	}

	/**
	 * Metodo que da de alta un director durante la creacion de la pelicula.
	 * @param director Director a crear.
	 * @return Devuelve el director.
	 */
	private static Director altaDirectorPelicula(Director director) {
		boolean hayError;
		String nombre;
		
		do {
			hayError = false;
			
			try {
				nombre = solicitarCadena(">>> Nombre del director/a: ");
				director = new Director(nombre);
				
				directorDao.guardar(director);
				
			} catch (LmdbException e) {
				System.out.println(e.getMessage());
				hayError = true;
			}
		} while (hayError);
		
		return director;
	}

	/**
	 * Metodo que da de baja una pelicula.
	 */
	private static void bajaPelicula() {
		Pelicula pelicula;
		int id;
		
		try {
			
			Principal.mostrarPeliculasSimplifacado();
			id = Principal.solicitarEntero(">>> ID de la película: ");
			pelicula = peliculaDao.localizar(id);
			peliculaDao.borrar(pelicula);
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuPelicula();
	}
	
	//========================================================================================//
	// MÉTODOS DEL MENÚ DIRECTORES/AS
	//========================================================================================//
	/**
	 * Metodo que muestra el menu de directores/as.
	 */
	private static void mostrarMenuDirector() {
		int opcion;
		
		System.out.println();
		System.out.println("========================");
		System.out.println("|| MENÚ DIRECTORES/AS ||");
		System.out.println("========================");
		System.out.println("[1] Consultar todos los directores/as");
		System.out.println("[2] Añadir un director/a");
		System.out.println("[3] Modificar un director/a");
		System.out.println("[4] Eliminar un director/a");
		System.out.println("[5] Volver al menú principal");
		
		opcion = Principal.solicitarOpcion(OPCION_MAXIMA_DIRECTOR);
		Principal.tratarOpcionMenuDirector(opcion);
	}
	
	/**
	 * Metodo que trata la opcion del menu directores/as elegida.
	 * @param opcion Opcion.
	 */
	private static void tratarOpcionMenuDirector(int opcion) {
		switch (opcion) {
		case 1:
			// [1] Consultar los directores/as.
			System.out.println();
			Principal.mostrarDirectores();
			break;
		case 2:
			// [2] Añadir un director/a.
			System.out.println();
			Principal.altaDirector();
			break;
		case 3:
			// [3] Modificar un director/a.
			System.out.println();
			Principal.modificarDirector();
			break;
		case 4:
			// [4] Eliminar un director/a.
			System.out.println();
			Principal.bajaDirector();
			break;
		default:
			// [5] Volver al menú principal.
			System.out.println();
			Principal.mostrarMenuPrincipal();
			break;
		}
	}
	
	/**
	 * Metodo que muestra los directores.
	 */
	private static void mostrarDirectores() {
		List<Director> listaDirectores;
		int maximoNombre;
		
		try {
			listaDirectores = directorDao.listarDirectores();
			
			maximoNombre = ANCHO_MINIMO_CELDA_NOMBRE;
			
			for (Director director : listaDirectores) {
				if (director.getNombre().length() >= maximoNombre) {
					maximoNombre = director.getNombre().length() + 3;
				}
			}
			
			System.out.println("==============================");
			System.out.println("|| LISTADO DE DIRECTORES/AS ||");
			System.out.println("==============================");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoNombre));
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", " ID", "| NOMBRE");
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoNombre));
			
			for (Director director : listaDirectores) {
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", " " + director.getId(),"| " + director.getNombre());
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoNombre));
			}
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuDirector();
	}
	
	/**
	 * Metodo que muestra los directores de forma simplificada.
	 */
	private static void mostrarDirectoresSimplificado() {
		List<Director> listaDirectores;
		int maximoNombre;
		
		try {
			listaDirectores = directorDao.listarDirectores();
			
			maximoNombre = ANCHO_MINIMO_CELDA_NOMBRE;
			
			for (Director director : listaDirectores) {
				if (director.getNombre().length() >= maximoNombre) {
					maximoNombre = director.getNombre().length() + 3;
				}
			}
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoNombre));
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", " ID", "| NOMBRE");
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoNombre));
			
			for (Director director : listaDirectores) {
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", " " + director.getId(),"| " + director.getNombre());
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoNombre));
			}
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Metodo que da de alta un director/a.
	 */
	private static void altaDirector() {
		Director director;
		String nombre;
		boolean hayError;
		
		do {
			hayError = false;
			
			try {
				nombre = solicitarCadena(">>> Nombre del director/a: ");
				director = new Director(nombre);
				
				directorDao.guardar(director);
				
				System.out.println(">>> Se ha dado de alta al director: " + director.getNombre() + ".");
			} catch (LmdbException e) {
				System.out.println(e.getMessage());
				hayError = true;
			}
		} while (hayError);
		
		Principal.mostrarMenuDirector();
	}
	
	/**
	 * Metodo que modifica un director/a.
	 */
	public static void modificarDirector() {
		Director director;
		int id;
		String nombre;
		
		try {
			Principal.mostrarDirectoresSimplificado();
			id = Principal.solicitarEntero(">>> ID del director/a: ");
			director = directorDao.localizar(id);
			
			nombre = solicitarCadena(">>> Nuevo nombre del director/a: ");
			
			director.setNombre(nombre);
			
			directorDao.actualizar(director);
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuDirector();
	}
	
	/**
	 * Metodo que da de baja un director/a.
	 */
	private static void bajaDirector() {
		Director director;
		int id;
		
		try {
			
			Principal.mostrarDirectoresSimplificado();
			id = Principal.solicitarEntero(">>> ID del director/a: ");
			director = directorDao.localizar(id);
			directorDao.borrar(director);
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuDirector();
	}

	//========================================================================================//
	// MÉTODOS DEL MENÚ ACTORES/ACTRICES
	//========================================================================================//
	/**
	 * Metodo que muestra el menu de actores/actrices.
	 */
	private static void mostrarMenuActor() {
		int opcion;
		
		System.out.println();
		System.out.println("======================");
		System.out.println("|| ACTORES/ACTRICES ||");
		System.out.println("======================");
		System.out.println("[1] Consultar todos los actores/actrices");
		System.out.println("[2] Añadir un actor/actriz");
		System.out.println("[3] Modificar un actor/actriz");
		System.out.println("[4] Eliminar un actor/actriz");
		System.out.println("[5] Añadir un actor/actriz a una película");
		System.out.println("[6] Volver al menú principal");
		
		opcion = Principal.solicitarOpcion(OPCION_MAXIMA_ACTOR);
		Principal.tratarOpcionMenuActor(opcion);
	}
	
	/**
	 * Metodo que trata la opcion del menu actores/actrices elegida.
	 * @param opcion Opcion elegida.
	 */
	private static void tratarOpcionMenuActor(int opcion) {
		switch (opcion) {
		case 1:
			// [1] Consultar los actores/actrices
			System.out.println();
			Principal.mostrarActores();
			break;
		case 2:
			// [2] Añadir un actor/actriz
			System.out.println();
			Principal.altaActor();
			break;
		case 3:
			// [3] Modificar un actor/actriz
			System.out.println();
			Principal.modificarActor();
			break;
		case 4:
			// [4] Eliminar un actor/actriz
			System.out.println();
			Principal.bajaActor();
			break;
		case 5:
			// [5] Añadir un actor/actriz a una película
			System.out.println();
			Principal.anhadirActorPelicula();
			break;
		default:
			// [6] Volver al menú principal
			System.out.println();
			Principal.mostrarMenuPrincipal();
			break;
		}
	}

	/**
	 * Metodo que muestra los actores.
	 */
	private static void mostrarActores() {
		List<Actor> listaActores;
		int maximoNombre;
		
		try {
			listaActores = actorDao.listarActores();
			
			maximoNombre = ANCHO_MINIMO_CELDA_NOMBRE;
			
			for (Actor actor : listaActores) {
				if (actor.getNombre().length() >= maximoNombre) {
					maximoNombre = actor.getNombre().length() + 3;
				}
			}
			
			System.out.println("=================================");
			System.out.println("|| LISTADO DE ACTORES/ACTRICES ||");
			System.out.println("=================================");
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoNombre));
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", " ID", "| NOMBRE");
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoNombre));
			
			for (Actor actor : listaActores) {
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", " " + actor.getId(), "| " + actor.getNombre());
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoNombre));
			}
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuActor();
	}
	
	/**
	 * Metodo que muestra los actores de forma simplificada.
	 */
	private static void mostrarActoresSimplificado() {
		List<Actor> listaActores;
		int maximoNombre;
		
		try {
			listaActores = actorDao.listarActores();
			
			maximoNombre = ANCHO_MINIMO_CELDA_NOMBRE;
			
			for (Actor actor : listaActores) {
				if (actor.getNombre().length() >= maximoNombre) {
					maximoNombre = actor.getNombre().length() + 3;
				}
			}
			
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoNombre));
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", " ID", "| NOMBRE");
			System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaDobleGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaDobleGuion(maximoNombre));
			
			for (Actor actor : listaActores) {
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", " " + actor.getId(), "| " + actor.getNombre());
				System.out.printf("%-" + ANCHO_MINIMO_CELDA_ID + "s%-" + maximoNombre + "s\n", crearCadenaGuion(ANCHO_MINIMO_CELDA_ID), crearCadenaGuion(maximoNombre));
			}
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Metodo que da de alta a un actor/actriz.
	 */
	private static void altaActor() {
		Actor actor;
		String nombre;
		boolean hayError;
		
		do {
			hayError = false;
			
			try {
				nombre = solicitarCadena(">>> Nombre del actor/actriz: ");
				actor = new Actor(nombre);
				
				actorDao.guardar(actor);
				
				System.out.println(">>> Se ha dado de alta al actor/actriz: " + actor.getNombre() + ".");
				
			} catch (LmdbException e) {
				System.out.println(e.getMessage());
				hayError = true;
			}
		} while (hayError);
		
		Principal.mostrarMenuActor();
	}
	
	/**
	 * Metodo que modifica un actor/actriz.
	 */
	private static void modificarActor() {
		Actor actor;
		int id;
		String nombre;
		
		try {
			Principal.mostrarActoresSimplificado();
			id = Principal.solicitarEntero(">>> ID del actor/actriz: ");
			actor = actorDao.localizar(id);
			
			nombre = solicitarCadena(">>> Nuevo nombre del actor/actriz: ");
			
			actor.setNombre(nombre);
			
			actorDao.actualizar(actor);
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuActor();
	}
	
	/**
	 * Metodo que da de baja un actor/actriz.
	 */
	private static void bajaActor() {
		Actor actor;
		int id;
		
		try {
			
			Principal.mostrarActoresSimplificado();
			id = Principal.solicitarEntero(">>> ID del actor/actriz: ");
			actor = actorDao.localizar(id);
			actorDao.borrar(actor);
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		}
		
		Principal.mostrarMenuActor();
	}

	/**
	 * Metodo que anhade un actor/actriz a una pelicula.
	 */
	private static void anhadirActorPelicula() {
		Actor actor = null;
		Pelicula pelicula = null;
		PeliculaActor peliculaActor;
		int idActor, idPelicula;
		boolean hayError;
		
		do {
			hayError = false;
			
			try {
				Principal.mostrarActoresSimplificado();
				idActor = solicitarEntero(">>> ID del actor/actriz: ");
				actor = actorDao.localizar(idActor);
				
			} catch (LmdbException e) {
				System.out.println(e.getMessage());
				hayError = true;
			}
		} while (hayError);
		
		do {
			hayError = false;
			
			try {
				Principal.mostrarPeliculasSimplifacado();
				idPelicula = solicitarEntero(">>> ID de la película: ");
				pelicula = peliculaDao.localizar(idPelicula);
				
			} catch (LmdbException e) {
				System.out.println(e.getMessage());
				hayError = true;
			}
		} while (hayError);
		
		
		try {
			peliculaActor = new PeliculaActor(actor, pelicula);
			
			genericDao.guardar(peliculaActor);
			
			System.out.println("Añadido actor/actriz " + actor.getNombre() + " a la película " + pelicula.getTituloEspanha() + ".");
			
			sesion.clear();
			
		} catch (LmdbException e) {
			System.out.println(e.getMessage());
		} catch (NonUniqueObjectException e) {
			System.out.println("Ya está ese actor/actriz en esa película.");
		}
		
		Principal.mostrarMenuActor();
	}
	
	//========================================================================================//
	// MÉTODOS PARA SOLICITAR
	//========================================================================================//
	/**
	 * Metodo que solicita una opcion de menu y la valida.
	 * @param opcionMaxima Opcion maxima valida.
	 * @return Devuelve la opcion elegida.
	 */
	private static int solicitarOpcion(int opcionMaxima) {
		int opcion = 0;
		boolean hayError;
		
		do {
			try {
				hayError = false;
				System.out.print(">>> Escribe una opción: ");
				opcion = Integer.parseInt(teclado.nextLine());
			} catch (NumberFormatException nfe) {
				hayError = true;
			}
			
		} while (opcion < OPCION_MINIMA || opcion > opcionMaxima || hayError);
		
		return opcion;
	}
	
	/**
	 * Metodo para solicitar un numero entero.
	 * @param mensaje Mensaje al solicitar.
	 * @param limite Limite del numero entero.
	 * @return Devuelve el numero entero solicitado.
	 */
	@SuppressWarnings("unused")
	private static int solicitarEntero(String mensaje, int limite) {
		int entero = 0;
		boolean hayError;
		
		do {
			hayError = false;
			
			try {
				System.out.print(mensaje);
				entero = Integer.parseInt(teclado.nextLine());
				
				if (entero < 1 || entero > 999) {
					hayError = true;
					System.out.println("Error. Debe introducir entre una y tres cifras mayores que cero.");
				}
				
			} catch (NumberFormatException e) {
				hayError = true;
				System.out.println("Error. Debe introducir un número entero positivo.");
			}
			
		} while (hayError);
		
		return entero;
	}
	
	/**
	 * Metodo para solicitar un numero entero.
	 * @param mensaje Mensaje al solicitar.¡
	 * @return Devuelve el numero entero solicitado.
	 */
	private static int solicitarEntero(String mensaje) {
		int entero = 0;
		boolean hayError;
		
		do {
			hayError = false;
			
			try {
				System.out.print(mensaje);
				entero = Integer.parseInt(teclado.nextLine());
				
				if (entero < 1) {
					hayError = true;
					System.out.println("Error. Debe introducir entre una cifra positiva.");
				}
				
			} catch (NumberFormatException e) {
				hayError = true;
				System.out.println("Error. Debe introducir un número entero positivo.");
			}
			
		} while (hayError);
		
		return entero;
	}

	/**
	 * Metodo que solicita una cadena.
	 * @param mensaje Mensaje al solicitar.
	 * @param limite Limite de caracteres de la cadena.
	 * @return Devuelve la cadena solicitada.
	 */
	@SuppressWarnings("unused")
	private static String solicitarCadena(String mensaje, int limite) {
		String cadena = "";
		boolean hayError;
		
		do {
			hayError = false;
			System.out.print(mensaje);
			cadena = teclado.nextLine();
			
			if (cadena.length() < 1 || cadena.length() > limite) {
				hayError = true;
				System.out.println("Error. Debe introducir entre 1 y " + limite + " caracteres.");
			}
		} while (hayError);
		
		return cadena;
	}
	
	/**
	 * Metodo que solicita una cadena.
	 * @param mensaje Mensaje al solicitar.
	 * @return Devuelve la cadena solicitada.
	 */
	private static String solicitarCadena(String mensaje) {
		String cadena = "";
		boolean hayError;
		
		do {
			hayError = false;
			System.out.print(mensaje);
			cadena = teclado.nextLine();
			
			if (cadena.length() < 1) {
				hayError = true;
				System.out.println("Error. Debe introducir un caracter o más.");
			}
		} while (hayError);
		
		return cadena;
	}
	
	/**
	 * Metodo que solicita un año.
	 * @param mensaje Mensaje al solicitar.
	 * @return Devuelve el anho solicitado.
	 */
	private static Date solicitarAnho(String mensaje) {
		Date anho = null;
		SimpleDateFormat formatoAnho;
		String textoAnho;
		boolean hayError;
		
		formatoAnho = new SimpleDateFormat("yyyy");
		
		do {
			hayError = false;
			System.out.print(mensaje);
			textoAnho = teclado.nextLine();
			
			try {
				anho = formatoAnho.parse(textoAnho);
			} catch (Exception e) {
				hayError = true;
				System.out.println("Error. Año incorrecto.");
			}
		} while (hayError);
		
		return anho;
	}
	
	/**
	 * Metodo que solicita un pais.
	 * @param mensaje Mensaje al solicitar.
	 * @return Devuelve el pais solicitado.
	 */
	private static Pais solicitarPais(String mensaje) {
		Pais pais = null;
		int contadorPais = 1, indicePais = 0;
		boolean hayError;
		
		for (Pais paisActual : Pais.values()) {
			System.out.println(contadorPais + " - " + paisActual.toString());
			contadorPais++;
		}
		
		do {
			try {
				hayError = false;
				System.out.print(mensaje);
				indicePais = Integer.parseInt(teclado.nextLine());
			} catch (NumberFormatException nfe) {
				hayError = true;
			}
		} while (indicePais < MINIMO_INDICE_PAIS || indicePais > contadorPais || hayError);
		
		switch (indicePais) {
		case 1:
			pais = Pais.ESTADOS_UNIDOS;
			break;
		case 2:
			pais = Pais.REINO_UNIDO;
			break;
		case 3:
			pais = Pais.INDIA;
			break;
		case 4:
			pais = Pais.FRANCIA;
			break;
		case 5:
			pais = Pais.ITALIA;
			break;
		case 6:
			pais = Pais.ESPANHA;
			break;
		case 7:
			pais = Pais.ALEMANIA;
			break;
		case 8:
			pais = Pais.JAPON;
			break;
		case 9:
			pais = Pais.COREA_DEL_SUR;
			break;
		default:
			break;
		}
		
		return pais;
	}
	
	/**
	 * Metodo que solicita un pais mediante su indice.
	 * @param indice Indice del pais.
	 * @return Devuelve el pais solicitado.
	 */
	public static Pais solicitarPais(int indice) {
		Pais pais = null;
		
		switch (indice) {
		case 1:
			pais = Pais.ESTADOS_UNIDOS;
			break;
		case 2:
			pais = Pais.REINO_UNIDO;
			break;
		case 3:
			pais = Pais.INDIA;
			break;
		case 4:
			pais = Pais.FRANCIA;
			break;
		case 5:
			pais = Pais.ITALIA;
			break;
		case 6:
			pais = Pais.ESPANHA;
			break;
		case 7:
			pais = Pais.ALEMANIA;
			break;
		case 8:
			pais = Pais.JAPON;
			break;
		case 9:
			pais = Pais.COREA_DEL_SUR;
			break;
		default:
			break;
		}
		
		return pais;
	}
	
	/**
	 * Metodo que solicita el genero.
	 * @param mensaje Mensaje al solicitar.
	 * @return Devuelve el genero solicitado.
	 */
	private static Genero solicitarGenero(String mensaje) {
		Genero genero = null;
		int contadorGenero = 1, indiceGenero = 0;
		boolean hayError;
		
		for (Genero generoActual : Genero.values()) {
			System.out.println(contadorGenero + " - " + generoActual.toString());
			contadorGenero++;
		}
		
		do {
			try {
				hayError = false;
				System.out.print(mensaje);
				indiceGenero = Integer.parseInt(teclado.nextLine());
			} catch (NumberFormatException nfe) {
				hayError = true;
			}
		} while (indiceGenero < MINIMO_INDICE_GENERO || indiceGenero > contadorGenero || hayError);
		
		switch (indiceGenero) {
		case 1:
			genero = Genero.ACCION;
			break;
		case 2:
			genero = Genero.ANIMACION;
			break;
		case 3:
			genero = Genero.AVENTURAS;
			break;
		case 4:
			genero = Genero.BELICO;
			break;
		case 5:
			genero = Genero.CIENCIA_FICCION;
			break;
		case 6:
			genero = Genero.COMEDIA;
			break;
		case 7:
			genero = Genero.DRAMA;
			break;
		case 8:
			genero = Genero.FANTASTICO;
			break;
		case 9:
			genero = Genero.INTRIGA;
			break;
		case 10:
			genero = Genero.MUSICAL;
			break;
		case 11:
			genero = Genero.ROMANCE;
			break;
		case 12:
			genero = Genero.TERROR;
			break;
		case 13:
			genero = Genero.THRILLER;
			break;
		case 14:
			genero = Genero.WESTERN;
			break;
		default:
			break;
		}
		
		return genero;
	}
	
	/**
	 * Metodo que solicita el genero por un indice.
	 * @param indice Indice del genero.
	 * @return Devuelve el genero solicitado.
	 */
	public static Genero solicitarGenero(int indice) {
		Genero genero = null;
		
		switch (indice) {
		case 1:
			genero = Genero.ACCION;
			break;
		case 2:
			genero = Genero.ANIMACION;
			break;
		case 3:
			genero = Genero.AVENTURAS;
			break;
		case 4:
			genero = Genero.BELICO;
			break;
		case 5:
			genero = Genero.CIENCIA_FICCION;
			break;
		case 6:
			genero = Genero.COMEDIA;
			break;
		case 7:
			genero = Genero.DRAMA;
			break;
		case 8:
			genero = Genero.FANTASTICO;
			break;
		case 9:
			genero = Genero.INTRIGA;
			break;
		case 10:
			genero = Genero.MUSICAL;
			break;
		case 11:
			genero = Genero.ROMANCE;
			break;
		case 12:
			genero = Genero.TERROR;
			break;
		case 13:
			genero = Genero.THRILLER;
			break;
		case 14:
			genero = Genero.WESTERN;
			break;
		default:
			break;
		}
		
		return genero;
	}
	
	//========================================================================================//
	// MÉTODOS DE CREACIÓN DE CADENAS
	//========================================================================================//
	/**
	 * Metodo que crea una cadena con guiones dado un tamanho especifico.
	 * @param tamanho Tamanho de la cadena.
	 * @return Devuelve la cadena creada.
	 */
	private static String crearCadenaGuion(int tamanho) {
		StringBuilder cadena = new StringBuilder(tamanho);
		char guion = '-';
		
		for (int contador = 0; contador < tamanho; contador++) {
			cadena.append(guion);
		}
		
		return cadena.toString();
	}
	
	/**
	 * Metodo que crea una cadena con dobles guiones dado un tamanho especifico.
	 * @param tamanho Tamanho de la cadena.
	 * @return Devuelve la cadena creada.
	 */
	private static String crearCadenaDobleGuion(int tamanho) {
		StringBuilder cadena = new StringBuilder(tamanho);
		char guion = '=';
		
		for (int contador = 0; contador < tamanho; contador++) {
			cadena.append(guion);
		}
		
		return cadena.toString();
	}
}