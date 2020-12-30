package me.eddie.blackboardDownloader.http;

import org.openqa.selenium.Cookie;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class HttpClient {

    public static class Response<T> {
        private T response;
        private int httpCode;

        public Response(T response, int httpCode) {
            this.response = response;
            this.httpCode = httpCode;
        }

        public T getResponse() {
            return response;
        }

        public int getHttpCode() {
            return httpCode;
        }
    }

    /**
     * Sends a POST request to the URL given with the request body as the serializable object provided transformed into JSON.
     *
     * @param targetURL The URL to make the request to
     * @param elem      The object to transform into JSON (Using GSON)
     * @return The server's response, or null if no response
     */
    public static String executeJSONPost(String targetURL, Object elem) {
        return executePost(targetURL, GsonUtil.getGson().toJson(elem));
    }

/*    *
     * Sends a GET request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @return The server's response, or null if no response
    public static byte[] executeGetOfBytes(String targetURL, boolean useBrowserAgent){
        return executeGetOfBytes(targetURL, useBrowserAgent, new TaskManager.TaskDisplay(null, ""));
    }*/

    /**
     * Sends a GET request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @return The server's response, or null if no response
     */
    public static byte[] executeGetOfBytes(String targetURL, boolean useBrowserAgent)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            if(useBrowserAgent) {
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
                //connection.setRequestProperty("Accept-Encoding", "identity");
            }

            connection.setUseCaches (false);
            connection.setDoOutput(true);

            //Get Response
            InputStream is = connection.getInputStream();
            /*BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));*/
	      /*String line;
	      StringBuffer response = new StringBuffer();
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\r');
	      }*/

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];
            long totalLen = connection.getContentLengthLong();

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                int percent = (int) (buffer.size() / totalLen);
//                taskDisplay.setTaskStatusString("Downloading ("+percent+"%)...");
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return buffer.toByteArray();

        } catch (Exception e) {

            e.printStackTrace();
            /*return e.getMessage();*/

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
        return new byte[]{};
    }

    /**
     * Sends a GET request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @return The server's response, or null if no response
     */
    public static Response<byte[]> executeGetOfBytes(String targetURL, boolean useBrowserAgent, Set<Cookie> cookies)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            StringBuilder cookieStr = new StringBuilder();
            for(Cookie c:cookies){
                if(cookieStr.length() > 0){
                    cookieStr.append(";");
                }
                cookieStr.append(c.getName()+"="+c.getValue());
            }
            connection.addRequestProperty("Cookie",cookieStr.toString());
            if(useBrowserAgent) {
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
                //connection.setRequestProperty("Accept-Encoding", "identity");
            }

            connection.setUseCaches (false);
            connection.setDoOutput(true);

            //Get Response
            InputStream is = connection.getInputStream();
            /*BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));*/
	      /*String line;
	      StringBuffer response = new StringBuffer();
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\r');
	      }*/

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];
            long totalLen = connection.getContentLengthLong();

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                int percent = (int) (buffer.size() / totalLen);
//                taskDisplay.setTaskStatusString("Downloading ("+percent+"%)...");
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return new Response<>(buffer.toByteArray(), connection.getResponseCode());

        } catch (Exception e) {

            e.printStackTrace();
            /*return e.getMessage();*/

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
        int code = 0;
        try {
            code = connection != null ? connection.getResponseCode() : -1;
        } catch (IOException e) {
            code = -1;
            e.printStackTrace();
        }
        return new Response<>(new byte[]{}, code);
    }

    /**
     * Sends a GET request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @return The server's response, or null if no response
     */
    public static String executeGet(String targetURL)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);

            connection.setUseCaches (false);
            connection.setDoOutput(true);

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            /*e.printStackTrace();*/
            return "Error: "+e.getMessage();

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Sends a GET request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @return The server's response, or null if no response
     */
    public static String executeGet(String targetURL, Set<Cookie> cookies)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            StringBuilder cookieStr = new StringBuilder();
            for(Cookie c:cookies){
                if(cookieStr.length() > 0){
                    cookieStr.append(";");
                }
                cookieStr.append(c.getName()+"="+c.getValue());
            }
            connection.addRequestProperty("Cookie",cookieStr.toString());
            connection.setInstanceFollowRedirects(true);

            connection.setUseCaches (false);
            connection.setDoOutput(true);

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            /*e.printStackTrace();*/
            return "Error: "+e.getMessage();

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Sends a GET request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @return The server's response, or null if no response
     */
    public static Response<String> resolveRedirectDestination(String targetURL, Set<Cookie> cookies)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            StringBuilder cookieStr = new StringBuilder();
            for(Cookie c:cookies){
                if(cookieStr.length() > 0){
                    cookieStr.append(";");
                }
                cookieStr.append(c.getName()+"="+c.getValue());
            }
            connection.addRequestProperty("Cookie",cookieStr.toString());
            connection.setInstanceFollowRedirects(true);

            connection.setUseCaches (false);
            connection.setDoOutput(true);

            //Get Response
            InputStream is = connection.getInputStream();
            is.close();
            return new Response<>(connection.getURL().toExternalForm(), connection.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
            /*e.printStackTrace();*/
            try {
                return new Response<>("Error: "+e.getMessage(), connection.getResponseCode());
            } catch (Exception ioException) {
                return new Response<>("Error: "+e.getMessage(), -1);
            }

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Sends a GET request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @return The server's response, or null if no response
     */
    public static String resolveRedirectDestination(String targetURL)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);

            connection.setUseCaches (false);
            connection.setDoOutput(true);

            //Get Response
            InputStream is = connection.getInputStream();
            is.close();
            return connection.getURL().toExternalForm();

        } catch (Exception e) {
            e.printStackTrace();
            /*e.printStackTrace();*/
            return "Error: "+e.getMessage();

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Sends a POST request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @param body The request body
     * @return The server's response, or null if no response
     */
    public static String executePost(String targetURL, String body){
        return executePost(targetURL, body, "application/x-www-form-urlencoded");
    }

    /**
     * Sends a POST request to the URL given with the request body as the string provided
     *
     * @param targetURL The URL to make the request to
     * @param body The request body
     * @return The server's response, or null if no response
     */
    public static String executePost(String targetURL, String body, String contentType)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    contentType);
            connection.setConnectTimeout(30000);

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(body.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
            bw.write (body);
            bw.flush ();
            bw.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}

