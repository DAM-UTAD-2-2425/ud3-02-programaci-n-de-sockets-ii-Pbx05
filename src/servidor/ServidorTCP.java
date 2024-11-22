package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: Complementa esta clase para que acepte conexiones TCP con clientes
 * para recibir un boleto, generar la respuesta y finalizar la sesion
 */
public class ServidorTCP {
	private String [] respuesta;
	private int [] combinacion;
	private int reintegro;
	private int complementario;
	private Socket socketCliente;
	private ServerSocket socketServidor;
	private BufferedReader entrada;
	private PrintWriter salida;

	/**
	 * Constructor
	 */
	public ServidorTCP (int puerto) {
		this.respuesta = new String [9];
		this.respuesta[0] = "Boleto inv�lido - N�meros repetidos";
		this.respuesta[1] = "Boleto inv�lido - n�meros incorretos (1-49)";
		this.respuesta[2] = "6 aciertos";
		this.respuesta[3] = "5 aciertos + complementario";
		this.respuesta[4] = "5 aciertos";
		this.respuesta[5] = "4 aciertos";
		this.respuesta[6] = "3 aciertos";
		this.respuesta[7] = "Reintegro";
		this.respuesta[8] = "Sin premio";
		generarCombinacion();
		imprimirCombinacion();
		this.socketCliente = null;
		this.socketServidor = null;
		this.entrada = null;
		this.salida = null;
		try {
			socketServidor = new ServerSocket(puerto);
			System.out.println("Esperando conexión...");
			socketCliente = socketServidor.accept();
			System.out.println("Conexión acceptada: " + socketCliente);
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.out.println("No puede escuchar en el puerto: " + puerto);
			System.exit(-1);
		}
	}
	
	
	/**
	 * @return Debe leer la combinacion de numeros que le envia el cliente
	 */
	public String leerCombinacion () {
		String respuesta = "Sin hacer";
		try {
			respuesta = entrada.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return respuesta;
	}
	
	/**
	 * @param linea 
	 * @return Debe devolver una de las posibles respuestas configuradas
	 */
	public String comprobarBoleto (String linea) {
		// Reconvierto a un array de int la combinacion del cliente, primero la paso a un array de string para poder convertir con el split y luego
		// la convierto de ahi a un array de int con un for y el integer.parseint
		String respuesta = "Sin hacer comprobar";
		String[] combinacionClienteString = linea.split(" ");
		int[] combinacionCliente = new int[6];
		int numeroAciertos = 0;
		boolean complementarioAcertado = false;
		boolean reintegroAcertado = false;
		boolean numerosRepetidos = false;
		boolean numeroIncorrecto = false;
		for (int i = 0; i < combinacionCliente.length; i++) {
			combinacionCliente[i] = Integer.parseInt(combinacionClienteString[i]);			
		}
		
		// Compruebo que el numero sea correcto
		for (int i = 0; i < combinacionCliente.length; i++) {
			if(combinacionCliente[i] > 49 || combinacionCliente[i] < 1) {
				respuesta = this.respuesta[1];
				numeroIncorrecto = true;
			}			
		}	
		// Ordeno el array para comprobar de forma mas comoda que no haya repetidos
		Arrays.sort(combinacionCliente);
		for (int i = 0; i < combinacionCliente.length - 1; i++) {
			if(combinacionCliente[i] == combinacionCliente[i + 1]) {
				respuesta = this.respuesta[0];
				numerosRepetidos = true;
			}
		}
		
		// Compruebo que si ninguno de los es falso, compruebe cuantos aciertos tiene el cliente y si ha acertado el reintegro o el complementario
		if(numeroIncorrecto == false && numerosRepetidos == false) {
			for (int i = 0; i < combinacion.length; i++) {
				for (int j = 0; j < combinacionCliente.length; j++) {
					if(combinacion[i] == combinacionCliente[j]) {
						numeroAciertos++;
					}
					if(reintegro == combinacionCliente[j]) {
						respuesta = this.respuesta[7];
						reintegroAcertado = true;
					}
					if(complementario == combinacionCliente[j]) {
						complementarioAcertado = true;
					}
				}
			}
			// COn un switch imprimo un mensaje depende de cada acierto
			switch(numeroAciertos) {
			case 0,1,2: 
				respuesta = this.respuesta[8];
				break;
			case 3:
				respuesta = this.respuesta[6];
				break;
			case 4:
				respuesta = this.respuesta[5];
				break;
			case 5:
				if(complementarioAcertado) {
					respuesta = this.respuesta[3];
				}else {
					respuesta = this.respuesta[4];
				}
				break;
			case 6:
				respuesta = this.respuesta[2];
				break;
			}
		}
		// Imprimo el reintegro si lo ha acertado y si el numero de aciertos es menor de 3
		if(numeroAciertos < 3 && reintegroAcertado) {
			respuesta = this.respuesta[7];
		}
		return respuesta;
	}

	/**
	 * @param respuesta se debe enviar al ciente
	 */
	public void enviarRespuesta (String respuesta) {
		salida.println(respuesta);
	}
	
	/**
	 * Cierra el servidor
	 */
	public void finSesion () {
		try {
			socketCliente.close();
			socketServidor.close();
			entrada.close();
			salida.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Servidor acabado");
	}
	
	/**
	 * Metodo que genera una combinacion. NO MODIFICAR
	 */
	private void generarCombinacion () {
		Set <Integer> numeros = new TreeSet <Integer>();
		Random aleatorio = new Random ();
		while (numeros.size()<6) {
			numeros.add(aleatorio.nextInt(49)+1);
		}
		int i = 0;
		this.combinacion = new int [6];
		for (Integer elto : numeros) {
			this.combinacion[i++]=elto;
		}
		this.reintegro = aleatorio.nextInt(49) + 1;
		this.complementario = aleatorio.nextInt(49) + 1;
	}
	
	/**
	 * Metodo que saca por consola del servidor la combinacion
	 */
	private void imprimirCombinacion () {
		System.out.print("Combinaci�n ganadora: ");
		for (Integer elto : this.combinacion) 
			System.out.print(elto + " ");
		System.out.println("");
		System.out.println("Complementario:       " + this.complementario);
		System.out.println("Reintegro:            " + this.reintegro);
	}

}

