package mypackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		String apiKey = "826f47bab1f480cee7b4b5d472c98078";
		// Get the city from the form input
        String city = URLEncoder.encode(request.getParameter("city"), "UTF-8"); // URL-encode the city
        
        
        
        // Create the URL for the OpenWeatherMap API request
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
		
        //API Integration
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        //Reading the data from network
        InputStream inputStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        
        // Store in String
        StringBuilder responseContent = new StringBuilder();
        
        //input from the reader will create scanner object
        Scanner scanner = new Scanner(reader);
        
        while(scanner.hasNext()) {
        	responseContent.append(scanner.nextLine());
        }
        
        scanner.close();
        
        //TypeCasting = parsing the data into json
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
        System.out.println(jsonObject);
        
        //Date & Time
        long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
        String date = new Date(dateTimestamp).toString();
        
        //Temperature
        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
        //Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
        
        
        
     // Set the data as request attributes (for sending to the jsp page)
        request.setAttribute("date", date);
        
        String decodedCity = city.replace("+", " ");
        
        request.setAttribute("city", decodedCity);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", responseContent.toString());
        
        connection.disconnect();
        
        // Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
        
        
	}
}
