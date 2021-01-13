package application;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;


public class Main extends Application {
	private config cfg=null;
	private String config_name ="config-datagen.xml";
		public void start(Stage primaryStage) {
			cfg = new config(config_name);
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/application/mainWindow.fxml"));
				mainWindowContr mwc = new mainWindowContr(cfg);
				loader.setController(mwc);
				Parent root = (Parent) loader.load();
				VBox root1 = (VBox) root;

				//FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/MainWindow.fxml"));
				
				Scene scene = new Scene(root1,640,400);
				primaryStage.setScene(scene);
				primaryStage.setTitle("DESA-Evo Data Generator");
				primaryStage.setResizable(false);
				
				//primaryStage.setMaximized(true);
				primaryStage.show();
				Platform.setImplicitExit(true);
				primaryStage.setOnCloseRequest((ae) -> {
					Platform.exit();
					System.exit(0);
				});
			} catch(Exception e) {
				e.printStackTrace();
			}
		}


		public static void main(String[] args) {
			
			launch(args);
			
			//launch(args);
		}
	}