import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class HttpMMLab {
    static String input = "C:\\Users\\DucTien\\Desktop\\people.jpg";
    static String POST_URL = "http://api.mmlab.uit.edu.vn/api/v1/file/image?method=base64";
    static String GET_URL= "http://api.mmlab.uit.edu.vn/api/v1/vision/object-detection?fileName=out.jpg&fileID=";

    public static void main(String[] args) {
        BufferedImage image = null;
        File f = null;

        //Read image file
        try{
            //Read image
            f = new File(input);
            image = ImageIO.read(f);

            //Encode Image to Base 64
            String result = encodeToString(image,"jpg");
            String result2 = "data:image/jpeg;base64,"+ result;
            String payload = "&base64=" + URLEncoder.encode(result2,"UTF-8");
            //Post data connection and receive json
            sendRequest(payload);
        }catch(Exception e){
            System.out.println("Error: "+e);
        }
    }

    private static void receiveResponse(String fileID) throws IOException {
        URL obj = new URL(GET_URL + fileID + "&method=model1");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());

            //Recieve data

        } else {
            System.out.println("GET request not worked");
        }
    }

    private static void sendRequest(String base64) throws IOException  {
        //Http request post
        URL post = new URL(POST_URL);

        HttpURLConnection con = (HttpURLConnection) post.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/x-www-form-urlencoded" );
        con.setRequestProperty("Postman-Token","b6bd7915-3e4d-413e-892f-59917bda5a32");
        con.setUseCaches(false);


        //Add parameter
       // Map<String, String> parameters = new HashMap<>();
       // parameters.put("&method", "base64");

        con.setDoOutput(true);//Going to write data to connection
        // For POST only - START
        DataOutputStream  outputStreamWriter = new DataOutputStream(con.getOutputStream());
        outputStreamWriter.write(base64.getBytes(StandardCharsets.UTF_8));
       // outputStreamWriter.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        outputStreamWriter.flush();
        outputStreamWriter.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            //TEST TO CHECK
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response);
            System.out.println(response.indexOf("\"fileID\":"));
            int indexsemicolor= response.indexOf(",");
            int indexdoublecolor  = response.indexOf(":")+1;
            String indexFileID= response.substring(indexdoublecolor,indexsemicolor);

            receiveResponse(indexFileID);
        } else {
            System.out.println("POST request not worked");
        }
    }

    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}