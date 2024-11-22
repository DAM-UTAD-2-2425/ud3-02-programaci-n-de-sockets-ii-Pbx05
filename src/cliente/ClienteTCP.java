package cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TODO: Complementa esta clase para que genere la conexi�n TCP con el servidor
 * para enviar un boleto, recibir la respuesta y finalizar la sesion
 */
public class ClienteTCP {
	private Socket socketCliente = null;
	private BufferedReader entrada = null;
	private PrintWriter salida = null;
	/**
	 * Constructor
	 */
	public ClienteTCP(String ip, int puerto) {
		try {
			socketCliente = new Socket(ip, puerto);
			System.out.println("Conexión establecida: " + socketCliente);
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.err.printf("Imposible conectar con ip:%s / puerto:%d",ip,puerto);
			System.exit(-1);
		}
	}

	/**
	 * @param combinacion que se desea enviar
	 * @return respuesta del servidor con la respuesta del boleto
	 */
	public String comprobarBoleto(int[] combinacion) {
		String numeroCompleto = "";
		String numero = "";
		// Con este for convierto el la combinacion introducida a un numero de string con espacios para poderlo pasar al servidor ya que el servidor recibe 
		// un string 
		for (int i = 0; i < combinacion.length; i++) {
			if(i == combinacion.length - 1) {
				numero = combinacion[i] + "";
			}else {
				numero = combinacion[i] + " ";
			}
			numeroCompleto += numero;
		}
		salida.println(numeroCompleto);
		// leo la respuesta que mande el servidor 
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
	 * Sirve para finalizar la la conexi�n de Cliente y Servidor
	 */
	public void finSesion () {
		try {
			socketCliente.close();
			entrada.close();
			salida.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Cliente terminado");
	}
	
}
