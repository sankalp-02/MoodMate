package main.java.moodmate;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//importing for saving the mood with time
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


//importing for quote API [ZenQuotes API]
import java.net.HttpURLConnection;  //to open a connection to the API
import java.net.URL;  //to specify the API web address
import java.io.BufferedReader;  //reads the text response from the server
import java.io.InputStreamReader;
import java.io.IOException;  //handles no internet connection

public class MoodMateApp extends Application {

    //saving the mood with local time
    private void saveMoodToFile(String mood){
        try{
            FileWriter writer=new FileWriter("mood_history.txt",true); //true means append mode on
            LocalDateTime now=LocalDateTime.now();  //current local 
            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp=now.format(formatter);

            writer.write("Mood: "+mood+" | Time: "+timestamp + "\n");
            writer.close();
        }catch (IOException e){
            System.out.println("Error saving mood to file: "+ e.getMessage());
        }
    }

    //method contains the code for API connection [ZenQuotes]
    private String getQuoteFromAPI(){
        String apiUrl="https://zenquotes.io/api/random";
        StringBuilder response=new StringBuilder();


        try{
            URL url=new URL(apiUrl);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line=reader.readLine()) !=null){
                response.append(line);
            }

            reader.close();

            //parsing
//            String json=response.toString();
//
//            //q "quote":
//            int qStart=json.indexOf("\"q\":\"")+5;
//            int qEnd=json.indexOf("\",\"a\"");
//            String quote=json.substring(qStart,qEnd);
//
//            //extract author "a"
//            int aStart=json.indexOf("\"a\":\"");
//            int aEnd=json.indexOf("\"}]");
//            String author=json.substring(aStart,aEnd);

            //parse JSON safely using org.json
            JSONArray jsonArray=new JSONArray(response.toString());
            JSONObject quoteObject=jsonArray.getJSONObject(0);

            String quote=quoteObject.getString("q");
            String author=quoteObject.getString("a");

            return "\"" + quote+"\"-"+author;

        }catch(Exception e){
            System.out.println("Error fetching quote: "+e.getMessage());
            return "Unable to load the quote";
        }
    }

    @Override
    public void start(Stage primaryStage) {

        //Crafting the heading
        Label heading = new Label("Welcome to MoodMate!");
        heading.setStyle("-fx-font-size:22 px;-fx-font-weight:bold; -fx-text-fill: #2E86AB"); //blue color

//        Mood input
        Label moodLabel=new Label("How are you feeling today?");
        moodLabel.setStyle("-fx-font-size: 14px;");
        TextField moodInput=new TextField();
        moodInput.setPromptText("Enter your mood here");
        moodInput.setMaxWidth(250);

        //Response Label
        Label responseLabel=new Label();
        responseLabel.setStyle("-fx-font-size:14px; -fx-text-fill:#444444;");


        //Quote Label
        Label quoteLabel=new Label();
        quoteLabel.setWrapText(true);
        quoteLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#555555; -fx-padding:10 0 0 0;");

        //Submit button
        Button submitButton=new Button("Submit");
        submitButton.setStyle("-fx-background-color:#2ECC71; -fx-text-fill:white; -fx-font-weight:bold;");
        submitButton.setOnAction(e -> {
            String mood=moodInput.getText().toLowerCase().trim();
//            System.out.println("User's mood: "+mood);
            String response="";

            //Mood logic here-
            if (mood.contains("happy")){
                response = "Aree waah! aise hi hashte raho";
            }
            else if (mood.contains("sad")){
                response="kya ho gya yaar! kya load hai! tension ko goli maar";
            }
            else if(mood.contains("angry")){
                response="Gussa thook de yaar!";
            }
            else if(mood.contains("anxious") || mood.contains("nervous")) {
                response="You are doing your best!! keep going";
            }
            else if(mood.contains("excited")){
                response="Naacho BC!!!";
            }else if(mood.contains("horny")){
                response="It's time for your \"ME\" time";
            }
            else if (mood.isEmpty()) {
                response="Please enter your mood";
            }
            else{
                response="Thanks for sharing your mood!! Have a good day!!😉";
            }

            //Set response to label-
            responseLabel.setText(response); //Shows the message in the VBox.
            saveMoodToFile(mood);  //save mood with timestamp

            //Fetch and display the quote
            String quote=getQuoteFromAPI();
            quoteLabel.setText(quote);
        });

        //reset button
        Button resetButton=new Button("Reset");
        resetButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill:white; -fx-font-weight:bold;");
        resetButton.setOnAction(e ->{
            moodInput.clear(); //clear the text field
            responseLabel.setText(""); //clears the displayed message
        });


        //VBox Layout [POP op box]
        VBox layout=new VBox(12);
        layout.setAlignment(Pos.CENTER); //centered with space and padding
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(heading,moodLabel,moodInput,submitButton,resetButton,responseLabel,quoteLabel);

        Scene scene=new Scene(layout,600,250);
        primaryStage.setTitle("MoodMate-Mood Input");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
