package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    private Button startButton;
    @FXML
    private Button connectButton;
    @FXML
    private Button sendButton;

    @FXML
    private TextField localipTextField;
    @FXML
    private TextField remoteIpTextField;
    @FXML
    private TextField localPortTextField;
    @FXML
    private TextField remotePortTextField;

    @FXML
    private TextField messageTextField;


    @FXML
    private Label startStatusLabel;
    @FXML
    private Label connectionStatusLabel;
    @FXML
    private Label errorLabelLocal;
    @FXML
    private Label errorLabelRemote;

    @FXML
    private TextArea chatArea;


   private BufferedReader in;
    private PrintWriter out;

    public Controller() {}


    public String getOtherName() {
        if(remotePortTextField.getText().trim().toString().compareTo("")==0) {
            errorLabelRemote.setVisible(true);
            return remotePortTextField.getText().trim().toString();
        }
        else
            return remotePortTextField.getText().trim().toString();
    }


    public String getMyName() {
        if( localPortTextField.getText().trim().toString().compareTo("")==0) {
            errorLabelLocal.setVisible(true);
        return localPortTextField.getText().trim().toString();
        }
        else
            return localPortTextField.getText().trim().toString();
    }

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {



        startButton.setOnAction(e ->

        {
            String ip = localipTextField.getText().trim().toString(), p = localPortTextField.getText().trim().toString();
            if ((ip.isEmpty()) && (p.isEmpty())) {
                errorLabelLocal.setText("local ip or port is wrong");
                errorLabelLocal.setVisible(true);
            } else {
                startStatusLabel.setText("started");
                new Thread(() -> {
                    try {
                        Socket socket = new Socket("localhost", 9001);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream(), true);


                        while (true) {
                            String line = in.readLine();

                            if (line.startsWith("SUBMITNAME")) {
                                out.println(getMyName());
                            } else if (line.startsWith("NAMEACCEPTED")) {
                                messageTextField.setEditable(true);
                            } else if (line.startsWith("MESSAGE")) {
                                String[] tokens = line.split("@");
                                chatArea.appendText(tokens[2] + ": " + tokens[0].substring(8)+'\n');
                            }
                        }
                    } catch (IOException ex1) {
                    }
                }).start();
            }
        });
        connectButton.setOnAction(e ->

        {
            String ip = remoteIpTextField.getText().trim().toString(), p = remotePortTextField.getText().trim().toString();
            if ((ip.isEmpty()) && (p.isEmpty())) {
                errorLabelRemote.setText("remote ip or port is wrong");
                errorLabelRemote.setVisible(true);
            } else {
                connectionStatusLabel.setText("connected");
            }
        });

        messageTextField.textProperty().

                addListener((observable, oldValue, newValue) ->

                {
                    sendButton.setDisable(false);
                });

        sendButton.setOnAction(e ->

        {
            out.println((messageTextField.getText() + "@" + getOtherName() + "@" + getMyName()));
            chatArea.appendText(getMyName()+" to "+getOtherName()+": "+messageTextField.getText()+'\n');
            messageTextField.setText("");

        });
    }
}


