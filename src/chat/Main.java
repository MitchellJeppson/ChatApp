package chat;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	//TODO: Resizing issue 
	
	private static Stage stage;
	@Override
	public void start(Stage primaryStage) {
		try {
			//primaryStage.resizableProperty().set(false);
			Main.stage = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("chatGUI.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent t) {
			        System.exit(0);
			    }
			});
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage getStage() {
		return stage;
	}
}
