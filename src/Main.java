import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
    private static final String API_URL = "http://localhost/PHPApi";

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Bienvenido a la aplicación de gestión de usuarios");
            System.out.println("1. Crear usuario");
            System.out.println("2. Consultar usuario");
            System.out.println("3. Editar usuario");
            System.out.println("4. Eliminar usuario");
            System.out.println("5. Salir");
            System.out.print("Selecciona una opción: ");

            try {
                int option = Integer.parseInt(reader.readLine());

                switch (option) {
                    case 1:
                        createUser(reader);
                        break;
                    case 2:
                        retrieveUser(reader);
                        break;
                    case 3:
                        updateUser(reader);
                        break;
                    case 4:
                        deleteUser(reader);
                        break;
                    case 5:
                        System.out.println("¡Hasta luego!");
                        return;
                    default:
                        System.out.println("Opción inválida");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error al leer la entrada del usuario");
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número válido");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            System.out.println();
        }
    }

    private static void createUser(BufferedReader reader) throws IOException, JSONException {
        System.out.print("Ingresa el nombre del usuario: ");
        String name = reader.readLine();
        System.out.print("Ingresa el teléfono del usuario: ");
        String phone = reader.readLine();
        System.out.print("Ingresa el correo electrónico del usuario: ");
        String email = reader.readLine();

        JSONObject userData = new JSONObject();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("email", email);

        sendRequest("POST", API_URL, userData.toString());
    }

    private static void retrieveUser(BufferedReader reader) throws IOException, JSONException {
        System.out.print("Ingresa el ID del usuario: ");
        String userId = reader.readLine();

        String url = API_URL + "/" + userId;
        String response = sendRequest("GET", url, null);
        if (response != null) {
            JSONObject user = new JSONObject(response);
            System.out.println("Usuario encontrado:");
            System.out.println("ID: " + user.getString("id"));
            System.out.println("Nombre: " + user.getString("name"));
            System.out.println("Teléfono: " + user.getString("phone"));
            System.out.println("Correo electrónico: " + user.getString("email"));
        }
    }

    private static void updateUser(BufferedReader reader) throws IOException, JSONException {
        System.out.print("Ingresa el ID del usuario: ");
        String userId = reader.readLine();

        System.out.print("Ingresa el nuevo nombre del usuario: ");
        String name = reader.readLine();
        System.out.print("Ingresa el nuevo teléfono del usuario: ");
        String phone = reader.readLine();
        System.out.print("Ingresa el nuevo correo electrónico del usuario: ");
        String email = reader.readLine();

        JSONObject userData = new JSONObject();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("email", email);

        String url = API_URL + "/" + userId;
        sendRequest("PUT", url, userData.toString());
    }


    private static void deleteUser(BufferedReader reader) throws IOException {
        System.out.print("Ingresa el ID del usuario a eliminar: ");
        String userId = reader.readLine();

        String url = API_URL + "/" + userId;
        sendRequest("DELETE", url, null);
    }

    private static String sendRequest(String method, String urlString, String payload) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        if (payload != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(payload.getBytes("UTF-8"));
            connection.getOutputStream().close();
        }

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();
        } else {
            System.out.println("Error en la solicitud. Código de respuesta: " + responseCode);
            return null;
        }
    }
}