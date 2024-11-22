package servidor;

public class PrimitivaServidor {

	public static void main(String[] args) {
		ServidorTCP canal = new ServidorTCP(5555);
		String linea;
		String respuesta;
		do {
			linea = canal.leerCombinacion();
			// Creo un if para que no pete el programa al decir que no quieres volver a jugar, porque si dices que no al recibir un null en linea
			// explotara el metodo de comprobar boleto, si recibe null igualo el valor de linea a fin para que acabe el servidor 
			if(linea != null) {
				respuesta = canal.comprobarBoleto (linea);
				canal.enviarRespuesta (respuesta);
			}else {
				linea = "FIN";
			}
		} while (!linea.equalsIgnoreCase("FIN"));
		canal.finSesion();
		
	}

}
